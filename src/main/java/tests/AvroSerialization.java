package tests;

import gsp.FirmAccount;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;

public class AvroSerialization {
    private static Logger logger = Logger.getLogger("AvroSerialization");

    private Schema oldFirmAccountSchema = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"FirmAccount\",\"namespace\":\"gsp\",\"fields\":[{\"name\":\"Mnemonic\",\"type\":\"string\"},{\"name\":\"LegalEntityCode\",\"type\":\"string\"},{\"name\":\"StrategyCode\",\"type\":\"string\"}]}");

    public static void main(String[] args) throws Exception {
        FirmAccount sut = new FirmAccount("CDSSOV", "001", "AV8", "GOC001");

        // file encoding is including full schema

        logger.info("=== Testing DataFileWriter ===");
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DatumWriter<FirmAccount> sdw = new SpecificDatumWriter<>(FirmAccount.class);
        DataFileWriter<FirmAccount> dfw = new DataFileWriter<>(sdw);
        dfw.create(sut.getSchema(), bout);

        logger.info("Serialized: " + bout.toByteArray().length + " bytes");
        logger.info("Serialized="+new String(bout.toByteArray()));

        // single object encoding is done by Confluent classes with schema fingerprint

        // binary encoding does not include the schema at all

        logger.info("=== Testing BinaryEncoder ===");
        EncoderFactory fac = new EncoderFactory();
        bout = new ByteArrayOutputStream();
        BinaryEncoder bin = fac.binaryEncoder(bout, null);
        sdw.write(sut, bin);
        bin.flush();

        logger.info("Serialized: " + bout.toByteArray().length + " bytes");
        logger.info("Serialized="+new String(bout.toByteArray()));
    }
}
