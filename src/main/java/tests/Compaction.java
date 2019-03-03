package tests;

import gsp.FirmAccount;
import gsp.Offering;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.log4j.Logger;
import producer.Producers;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class Compaction {
    private static Logger logger = Logger.getLogger("Compaction");

    static void producerMain() {
        logger.info("Start producer");
        Producers.produceFirmAccounts(10000, 10);
        logger.info("Producer done");
    }

    private static class IgnoreDeserializer implements Deserializer<Object> {
        public void configure(Map map, boolean b) {}
        public Object deserialize(String s, byte[] bytes) {
            return null;
        }
        public void close() {}
    }

    static void consumerMain() {
        int i=0;
        logger.info("Start consumer");

        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test.compaction");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass());

        KafkaConsumer<String, Object> consumer = new KafkaConsumer<>(props, Serdes.String().deserializer(), new IgnoreDeserializer());
        TopicPartition tp = new TopicPartition("accounts", 0);
        consumer.assign(Collections.singleton(tp));
        consumer.seekToBeginning(Collections.singleton(tp));

        while (true) {
            ConsumerRecords<String, Object> recs = consumer.poll(Duration.ofMillis(100));
            i += recs.count();
            logger.info("Received " + recs.count() + " messages, total "+ i);
        }
    }

    public static class AvroDeserializer<T> implements Deserializer<T> {
        private final Class<T> clazz;

        public AvroDeserializer(Class<T> clazz) {
            this.clazz = clazz;
        }

        public void configure(Map<String, ?> map, boolean b) {}
        public void close() {}

        @Override
        public T deserialize(String s, byte[] bytes) {
            try {
                // Can read with old schema
                // SpecificDatumReader<FirmAccount> reader = new SpecificDatumReader<>(oldSchema, FirmAccount.SCHEMA$);

                // Can read without old schema because schema is encoded in the payload -> this way we can mix and match
                SpecificDatumReader<T> reader = new SpecificDatumReader<>(clazz);

                DataFileReader<T> r = new DataFileReader<T>(new SeekableByteArrayInput(bytes), reader);

                return r.next();
            } catch (IOException e) {
                throw new RuntimeException("Unexpected IO error during deserialization");
            }
        }
    }

    static void deserializingFirmAccountConsumer() {
        int i=0;
        logger.info("Start consumer");

        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test.compaction");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass());

        KafkaConsumer<String, FirmAccount> consumer = new KafkaConsumer<>(props, Serdes.String().deserializer(), new AvroDeserializer<>(FirmAccount.class));
        TopicPartition tp = new TopicPartition("accounts", 0);
        consumer.assign(Collections.singleton(tp));
        consumer.seekToBeginning(Collections.singleton(tp));

        while (true) {
            ConsumerRecords<String, FirmAccount> recs = consumer.poll(Duration.ofMillis(100));
            i += recs.count();
            logger.info("Received " + recs.count() + " messages, total "+ i);

            for (ConsumerRecord<String, FirmAccount> rec: recs) {
                logger.info("Key="+rec.key()+", Value="+rec.value());
            }
        }
    }

    static void deserializingOfferingConsumer() {
        int i=0;
        logger.info("Start consumer");

        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test.compaction");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass());

        KafkaConsumer<String, Offering> consumer = new KafkaConsumer<>(props, Serdes.String().deserializer(), new AvroDeserializer<>(Offering.class));
        TopicPartition tp = new TopicPartition("offerings", 0);
        consumer.assign(Collections.singleton(tp));
        consumer.seekToBeginning(Collections.singleton(tp));

        while (true) {
            ConsumerRecords<String, Offering> recs = consumer.poll(Duration.ofMillis(100));
            i += recs.count();
            logger.info("Received " + recs.count() + " messages, total "+ i);

            for (ConsumerRecord<String, Offering> rec: recs) {
                logger.info("Key="+rec.key()+", Value="+rec.value());
            }
        }
    }

    // Runs
    // size is 8mb
    // - 20087
    // - 31336
    // - 26311
    // change size to 1mb
    // - 12050
    // - 12752

    public static void main(String[] args) {
        deserializingOfferingConsumer();
    }
}
