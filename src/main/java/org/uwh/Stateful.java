package org.uwh;

import gsp.Numero;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uwh.util.AvroDeserializer;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Stateful {
    private static Logger logger = LoggerFactory.getLogger(Stateful.class);

    private static SourceFunction<Numero> numberSource() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("zookeeper.connect", "localhost:2181");
        props.setProperty("group.id", "flink.test");

        FlinkKafkaConsumer<Numero> consumer = new FlinkKafkaConsumer<>("numbers", new AvroDeserializer<>(Numero.class), props);
        consumer.setStartFromEarliest();

        return consumer;
    }

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);
        env.enableCheckpointing(60000);
        env.setRestartStrategy(
                RestartStrategies.fixedDelayRestart(1000000, Time.of(5, TimeUnit.SECONDS))
        );

        DataStream<Long> stream = env.addSource(numberSource()).map(Numero::getPayload).map(num -> {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            logger.info(String.format("Mapper: %10d", num));

            return num;
        });

        KeyedStream<Long,Integer> streamByModulo = stream.keyBy(num -> (int) (num%4));
        streamByModulo.process(new RunningCount()).print().setParallelism(1);

        JobExecutionResult res = env.execute("number_state_test");
    }

    private static class RunningCount extends KeyedProcessFunction<Integer, Long, String> {
        private transient ValueState<Long> aggregate;

        @Override
        public void open(Configuration parameters) throws Exception {
            ValueStateDescriptor<Long> desc = new ValueStateDescriptor<Long>("sum", TypeInformation.of(Long.class), 0L);
            aggregate = getRuntimeContext().getState(desc);
        }

        @Override
        public void processElement(Long aLong, Context context, Collector<String> collector) throws Exception {
            long newValue = aggregate.value();

            logger.info(String.format("Key process: %10d", aLong));

            // sum, sum of squares, sum of cubes, sum of square roots
            switch(context.getCurrentKey()) {
                case 0: newValue += aLong; break;
                case 1: newValue += aLong*aLong; break;
                case 2: newValue += aLong*aLong*aLong; break;
                default: newValue += (long) Math.sqrt(aLong); break;
            }

            aggregate.update(newValue);


            collector.collect(String.format("Input=%10d, Key=%1d, Agg=%15d", aLong, context.getCurrentKey(), newValue));
        }
    }
}
