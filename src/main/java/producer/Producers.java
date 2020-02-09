package producer;

import gsp.FirmAccount;
import gsp.Numero;
import gsp.Offering;
import gsp.gen.Generators;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

public class Producers {
    private static Logger logger = Logger.getLogger("Producers");

    public static void main(String[] args) throws Exception {
        // produceOfferings();
        produceNumbers(1000);
    }

    private static void produceNumbers(int max) {
        KafkaProducer<String, Numero> producer = new KafkaProducer<>(
                kafkaProperties(),
                Serdes.String().serializer(),
                new AvroSerializer<Numero>(Numero.getClassSchema(), Numero.class));

        for (long n=0; n<max; n++) {
            Numero num = new Numero(n);
            String key = String.valueOf(n);
            ProducerRecord<String, Numero> rec = new ProducerRecord<>("numbers", key, num);
            logger.info("Publishing "+n);
            producer.send(rec);
        }
    }

    private static Properties kafkaProperties() {
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass());
        return props;
    }

    private static void produceOfferings(String venue) throws Exception {
        KafkaProducer<String, Offering> producer = new KafkaProducer<>(
                kafkaProperties(),
                Serdes.String().serializer(),
                new AvroSerializer<Offering>(Offering.getClassSchema(), Offering.class));

        Random r = new Random();

        while (true) {
            Offering offering = Generators.generateOffering(r, venue);
            String key = offering.getVenue()+":"+offering.getSide()+":"+offering.getCUSIP();
            ProducerRecord<String, Offering> rec = new ProducerRecord<>("offerings", key, offering);
            logger.info("Publishing "+ key);
            producer.send(rec);
            Thread.sleep(10);
        }
    }

    private static void produceOfferings() throws Exception {
        new Thread(() -> { try { produceOfferings("VALUBOND"); } catch (Exception e)  {}}).start();
        new Thread(() -> { try { produceOfferings("ICEBOND"); } catch (Exception e)  {}}).start();
        new Thread(() -> { try { produceOfferings("MARKETAXESS"); } catch (Exception e)  {}}).start();

        produceOfferings("TRADEWEB");
    }

    public static class AvroSerializer<T> implements Serializer<T> {
        private final Schema schema;
        private final Class<T> clazz;

        public AvroSerializer(Schema schema, Class<T> clazz) {
            this.schema = schema;
            this.clazz = clazz;
        }

        public void configure(Map<String, ?> map, boolean b) {}
        public void close() {}

        @Override
        public byte[] serialize(String s, T obj) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                DatumWriter<T> dw = new SpecificDatumWriter<>(clazz);
                DataFileWriter<T> dfw = new DataFileWriter<>(dw);
                dfw.create(schema, bout);

                dfw.append(obj);
                dfw.close();

                return bout.toByteArray();

            } catch (IOException e) {
                throw new RuntimeException("Unexpected IO exception in serialization", e);
            }
        }
    }

    public static void produceFirmAccounts(int accounts, int batches) {
        KafkaProducer<String, FirmAccount> producer = new KafkaProducer<String, FirmAccount>(kafkaProperties(),
                Serdes.String().serializer(), new AvroSerializer<FirmAccount>(FirmAccount.getClassSchema(), FirmAccount.class));

        for (int i=0; i<batches; i++) {
            logger.info("Generating batch of " + accounts + " firm accounts");
            Generators.generateFirmAccounts(accounts).forEach(acc -> {
                ProducerRecord<String, FirmAccount> rec = new ProducerRecord<String, FirmAccount>("accounts", acc.getMnemonic().toString(), acc);
                producer.send(rec);
            });
        }
    }
}
