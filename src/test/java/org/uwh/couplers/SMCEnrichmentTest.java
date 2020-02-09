package org.uwh.couplers;

import gsp.CommonSecurityIdType;
import gsp.Price;
import gsp.SecurityIdType;
import gsp.SecurityMapping;
import org.apache.avro.generic.IndexedRecord;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.test.util.MiniClusterResourceConfiguration;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import org.uwh.util.CollectSink;
import org.uwh.util.StreamBuilder;

import java.io.Serializable;

public class SMCEnrichmentTest {
    public static MiniClusterWithClientResource flinkCluster =
            new MiniClusterWithClientResource(
                    new MiniClusterResourceConfiguration.Builder()
                            .setNumberSlotsPerTaskManager(2)
                            .setNumberTaskManagers(1)
                            .build()
            );

    private StreamExecutionEnvironment env;
    private CollectSink<Price> sink;

    @BeforeAll
    public static void classSetup() throws Exception { flinkCluster.before(); }

    @AfterAll
    public static void classTeardown() {
        flinkCluster.after();
    }

    @BeforeEach
    public void setup() {
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        sink = new CollectSink<>();
        CollectSink.values.clear();
    }

    @Test
    public void testSingleEnrichment() throws Exception {
        DataStream<Price> prices = StreamBuilder.empty(Price.class).delay(50).item(price("ABC", 101)).build(env);
        DataStream<SecurityMapping> securities = StreamBuilder.empty(SecurityMapping.class).item(isinMapping("ABC", "123")).build(env);

        SMCPEnrichment.smcEnrichment(prices, securities, p -> getSecurityId(p), (p,map) -> withSMCP(p, map), Price::getISIN, Price.class).addSink(sink);
        env.execute();

        assertEquals(1, sink.size());
        assertEquals("123", sink.get(0).getCommonSecurityId());
    }

    @Test
    public void testPriceThenSecurityEnrichment() throws Exception {
        DataStream<Price> prices = StreamBuilder.empty(Price.class).item(price("ABC", 101)).build(env);
        DataStream<SecurityMapping> securities = StreamBuilder.empty(SecurityMapping.class).delay(50).item(isinMapping("ABC", "123")).build(env);

        SMCPEnrichment.smcEnrichment(prices, securities, p -> getSecurityId(p), (p,map) -> withSMCP(p, map), Price::getISIN, Price.class).addSink(sink);
        env.execute();

        assertEquals(1, sink.size());
        assertEquals("123", sink.get(0).getCommonSecurityId());
    }

    @Test
    public void testMultiPricesThenSecurityEnrichment() throws Exception {
        DataStream<Price> prices = StreamBuilder.empty(Price.class).item(price("ABC", 101)).item(price("ABC", 102)).build(env);
        DataStream<SecurityMapping> securities = StreamBuilder.empty(SecurityMapping.class).delay(100).item(isinMapping("ABC", "123")).build(env);

        SMCPEnrichment.smcEnrichment(prices, securities, p -> getSecurityId(p), (p,map) -> withSMCP(p, map), Price::getISIN, Price.class).addSink(sink);
        env.execute();

        // only the last update gets through
        assertEquals(1, sink.size());
        assertEquals("123", sink.get(0).getCommonSecurityId());
        assertEquals(102, sink.get(0).getPrice(), 0.01);
    }

    @Test
    public void testSecurityInactiveThenAnotherOne() throws Exception {
        DataStream<Price> prices = StreamBuilder.empty(Price.class).item(price("ABC", 101)).build(env);
        DataStream<SecurityMapping> securities = StreamBuilder.empty(SecurityMapping.class)
                .delay(50)
                .item(isinMapping("ABC", "123"))
                .item(isinMapping("ABC", "234"))
                .build(env);

        SMCPEnrichment.smcEnrichment(prices, securities, p -> getSecurityId(p), (p,map) -> withSMCP(p, map), Price::getISIN, Price.class).addSink(sink);
        env.execute();

        assertEquals(2, sink.size());
    }

    public static Tuple2<SecurityIdType, String> getSecurityId(Price price) {
        return new Tuple2<>(SecurityIdType.ISIN, price.getISIN());
    }

    public static Price withSMCP(Price price, Tuple2<CommonSecurityIdType, String> commonSecurityId) {
        return new Price(commonSecurityId.f0, commonSecurityId.f1, price.getISIN(), price.getPrice());
    }

    private Price price(String isin, double price) {
        return new Price(null, null, isin, price);
    }

    private SecurityMapping isinMapping(String isin, String smcp) {
        return new SecurityMapping(SecurityIdType.ISIN, isin, CommonSecurityIdType.SMCP, smcp, System.currentTimeMillis());
    }

    private Security security(String smcp, String cusip, String isin, boolean isActive, int version) {
        Security sec = new Security();
        sec.smcp = smcp;
        sec.xrefs.put(SecurityIdType.CSP, cusip);
        sec.xrefs.put(SecurityIdType.ISIN, isin);

        sec.isOperational = isActive;
        sec.version = version;
        return sec;
    }
}
