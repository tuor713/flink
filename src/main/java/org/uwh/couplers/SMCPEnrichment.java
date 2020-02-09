package org.uwh.couplers;

import gsp.CommonSecurityIdType;
import gsp.SecurityIdType;
import gsp.SecurityMapping;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;

public class SMCPEnrichment {
    public static<T> DataStream<T> smcEnrichment(DataStream<T> inStream,
                                                 DataStream<SecurityMapping> securities,
                                                 KeySelector<T, Tuple2<SecurityIdType, String>> payloadSecuritySelector,
                                                 JoinFunction<T,Tuple2<CommonSecurityIdType, String>,T> commonSecurityIdEnricher,
                                                 KeySelector<T, String> payloadKeySelector,
                                                 Class<T> clazz) {

        TypeInformation<Tuple2<SecurityIdType, String>> keyInfo = new TupleTypeInfo<>(TypeInformation.of(SecurityIdType.class), TypeInformation.of(String.class));
        return inStream.keyBy(payloadSecuritySelector, keyInfo)
                .connect(securities.keyBy(mapping -> new Tuple2<>(mapping.getSecurityIdType(), mapping.getSecurityId()), keyInfo))
                // Flink appears to need the explicit type information here, otherwise it assumes the output is of type SecurityMapping !?
                .process(new MergeProcessor<>(commonSecurityIdEnricher, payloadKeySelector, clazz), TypeInformation.of(clazz));
    }

    private static class MergeProcessor<T> extends KeyedCoProcessFunction<Tuple2<SecurityIdType, String>, T, SecurityMapping, T> {
        private Class<T> clazz;

        ValueState<Tuple2<CommonSecurityIdType, String>> commonSecurityState;
        JoinFunction<T,Tuple2<CommonSecurityIdType, String>,T> commonSecurityIdEnricher;
        KeySelector<T, String> payloadKeySelector;
        MapState<String, T> latestPayloads;

        public MergeProcessor(JoinFunction<T, Tuple2<CommonSecurityIdType, String>, T> commonSecurityIdEnricher, KeySelector<T, String> payloadKeySelector, Class<T> clazz) {
            this.commonSecurityIdEnricher = commonSecurityIdEnricher;
            this.clazz = clazz;
            this.payloadKeySelector = payloadKeySelector;
        }

        @Override
        public void open(Configuration parameters) {
            commonSecurityState = getRuntimeContext().getState(new ValueStateDescriptor<>("commonSecurityId", new TupleTypeInfo<>(TypeInformation.of(CommonSecurityIdType.class), TypeInformation.of(String.class))));
            latestPayloads = getRuntimeContext().getMapState(new MapStateDescriptor<>("latestPayloads", String.class, clazz));
        }

        @Override
        public void processElement1(T t, Context context, Collector<T> collector) throws Exception {
            System.out.println("Payload update");
            latestPayloads.put(payloadKeySelector.getKey(t), t);
            if (commonSecurityState.value() != null) {
                collector.collect(commonSecurityIdEnricher.join(t, commonSecurityState.value()));
            }
        }

        @Override
        public void processElement2(SecurityMapping security, Context context, Collector<T> collector) throws Exception {
            System.out.println("Security update");

            Tuple2<CommonSecurityIdType, String> currentMapping = commonSecurityState.value();
            Tuple2<CommonSecurityIdType, String> newMapping = new Tuple2<>(security.getCommonSecurityIdType(), security.getCommonSecurityId());
            commonSecurityState.update(newMapping);

            // republish if either no SMCP was set or it has changed
            if (!newMapping.equals(currentMapping)) {
                for (T t : latestPayloads.values()) {
                    collector.collect(commonSecurityIdEnricher.join(t, newMapping));
                }
            }
        }
    }
}
