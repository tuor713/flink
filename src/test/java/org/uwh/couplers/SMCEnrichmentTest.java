package org.uwh.couplers;

import gsp.SecurityIdType;
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
        DataStream<Security> securities = StreamBuilder.empty(Security.class).item(security("123", "", "ABC", true, 1)).build(env);

        SMCPEnrichment.smcEnrichment(prices, securities, p -> p.isin, sec -> sec.xrefs.get(SecurityIdType.ISIN), Price::withSMCP, Price::getIsin, Price.class).addSink(sink);
        env.execute();

        assertEquals(1, sink.size());
        assertEquals("123", sink.get(0).smcp);
    }

    @Test
    public void testPriceThenSecurityEnrichment() throws Exception {
        DataStream<Price> prices = StreamBuilder.empty(Price.class).item(price("ABC", 101)).build(env);
        DataStream<Security> securities = StreamBuilder.empty(Security.class).delay(50).item(security("123", "", "ABC", true, 1)).build(env);

        SMCPEnrichment.smcEnrichment(prices, securities, p -> p.isin, sec -> sec.xrefs.get(SecurityIdType.ISIN), Price::withSMCP, Price::getIsin, Price.class).addSink(sink);
        env.execute();

        assertEquals(1, sink.size());
        assertEquals("123", sink.get(0).smcp);
    }

    @Test
    public void testMultiPricesThenSecurityEnrichment() throws Exception {
        DataStream<Price> prices = StreamBuilder.empty(Price.class).item(price("ABC", 101)).item(price("ABC", 102)).build(env);
        DataStream<Security> securities = StreamBuilder.empty(Security.class).delay(100).item(security("123", "", "ABC", true, 1)).build(env);

        SMCPEnrichment.smcEnrichment(prices, securities, p -> p.isin, sec -> sec.xrefs.get(SecurityIdType.ISIN), Price::withSMCP, Price::getIsin, Price.class).addSink(sink);
        env.execute();

        // only the last update gets through
        assertEquals(1, sink.size());
        assertEquals("123", sink.get(0).smcp);
        assertEquals(102, sink.get(0).price, 0.01);
    }

    @Test
    public void testSecurityInactiveThenAnotherOne() throws Exception {
        DataStream<Price> prices = StreamBuilder.empty(Price.class).item(price("ABC", 101)).build(env);
        DataStream<Security> securities = StreamBuilder.empty(Security.class)
                .delay(50)
                .item(security("123", "", "ABC", true, 1))
                .item(security("123", "", "ABC", false, 2))
                .item(security("234", "", "ABC", true, 1))
                .build(env);

        SMCPEnrichment.smcEnrichment(prices, securities, p -> p.isin, sec -> sec.xrefs.get(SecurityIdType.ISIN), Price::withSMCP, Price::getIsin, Price.class).addSink(sink);
        env.execute();

        assertEquals(2, sink.size());
    }

    private static class Price implements Serializable  {
        public String smcp;
        public String isin;
        public double price;

        public Price(String smcp, String isin, double price) {
            this.smcp = smcp;
            this.isin = isin;
            this.price = price;
        }

        public Price withSMCP(String smcp) {
            return new Price(smcp, this.isin, this.price);
        }

        public String getIsin() { return isin; }
    }

    private Price price(String isin, double price) {
        return new Price(null, isin, price);
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
