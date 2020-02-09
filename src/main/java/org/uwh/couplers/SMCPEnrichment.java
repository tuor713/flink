package org.uwh.couplers;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;

public class SMCPEnrichment {
    public static<T> DataStream<T> smcEnrichment(DataStream<T> inStream,
                                                 DataStream<Security> securities,
                                                 KeySelector<T, String> payloadSelector,
                                                 KeySelector<Security, String> securitySelector,
                                                 JoinFunction<T,String,T> smcpEnricher,
                                                 KeySelector<T, String> payloadKeySelector,
                                                 Class<T> clazz) {
        return inStream.keyBy(payloadSelector)
                .connect(securities.keyBy(securitySelector))
                .process(new MergeProcessor<>(smcpEnricher, payloadKeySelector, clazz));
    }

    private static class MergeProcessor<T> extends KeyedCoProcessFunction<String, T, Security, T> {
        private Class<T> clazz;

        ValueState<String> smcpState;
        JoinFunction<T,String,T> smcpEnricher;
        KeySelector<T, String> payloadKeySelector;
        MapState<String, T> latestPayloads;

        public MergeProcessor(JoinFunction<T, String, T> smcpEnricher, KeySelector<T, String> payloadKeySelector, Class<T> clazz) {
            this.smcpEnricher = smcpEnricher;
            this.clazz = clazz;
            this.payloadKeySelector = payloadKeySelector;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            smcpState = getRuntimeContext().getState(new ValueStateDescriptor<String>("smcp", String.class));
            latestPayloads = getRuntimeContext().getMapState(new MapStateDescriptor<String, T>("latestPayloads", String.class, clazz));
        }

        @Override
        public void processElement1(T t, Context context, Collector<T> collector) throws Exception {
            System.out.println("Payload update");
            latestPayloads.put(payloadKeySelector.getKey(t), t);
            if (smcpState.value() != null) {
                collector.collect(smcpEnricher.join(t, smcpState.value()));
            }
        }

        @Override
        public void processElement2(Security security, Context context, Collector<T> collector) throws Exception {
            System.out.println("Security update");

            if (!security.isOperational) return;

            String currentSMCP = smcpState.value();
            smcpState.update(security.smcp);

            // republish if either no SMCP was set or it has changed
            if (currentSMCP == null || !security.smcp.equals(currentSMCP)) {
                for (T t : latestPayloads.values()) {
                    collector.collect(smcpEnricher.join(t, security.smcp));
                }
            }
        }
    }
}
