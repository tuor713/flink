package org.uwh;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.api.java.typeutils.MapTypeInfo;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.api.java.utils.Option;
import org.apache.flink.api.scala.typeutils.OptionTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

public class OnePricing {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);

        env.setParallelism(1);

        StreamTableEnvironment tEnv = TableEnvironment.getTableEnvironment(env);

        DataStream<Tuple2<String,Double>> prices =
                env.fromCollection(
                        new EndlessIterator<>(samplePrices, Tuple2.of("CF", 100.0)),
                        new TupleTypeInfo<>(TypeInformation.of(String.class), TypeInformation.of(Double.class)));

        DataStream<Tuple5<String,String,String,Double,Double>> positions =
                env.fromCollection(
                        new EndlessIterator<>(samplePositions, Tuple5.of("ABOOK:CF", "ABOOK", "CF", 1000.0, 100.0)),
                        new TupleTypeInfo<>(
                                TypeInformation.of(String.class),
                                TypeInformation.of(String.class),
                                TypeInformation.of(String.class),
                                TypeInformation.of(Double.class),
                                TypeInformation.of(Double.class)));

        prices.print("XXX - PRICE");
        positions.print("XXX - POSITION");

        prices.connect(positions)
                .keyBy(t -> t.f0, t -> t.f2)
                .process(new Joiner<>(
                        px -> px.f0,
                        pos -> pos.f2,
                        px -> px.f0,
                        pos -> pos.f0,
                        new TupleTypeInfo<>(TypeInformation.of(String.class), TypeInformation.of(Double.class)),
                        new TupleTypeInfo<>(TypeInformation.of(String.class), TypeInformation.of(String.class), TypeInformation.of(String.class), TypeInformation.of(Double.class), TypeInformation.of(Double.class)),
                        TypeInformation.of(String.class),
                        TypeInformation.of(String.class),
                        TypeInformation.of(String.class)))
                .print("XXX - JOIN");


        /*
        - Lateral tables appears not to work too well. Initial events don't come through. Also the price to position join doesn't work correctly.

        Table priceTable = tEnv.fromDataStream(prices, "CUSIP,Price,proctime.proctime");
        Table positionTable = tEnv.fromDataStream(positions, "PositionId,Book,CUSIP,Quantity,StartOfDayPrice,proctime.proctime");

        tEnv.registerTable("Prices", priceTable);
        tEnv.registerTable("Positions", positionTable);
        tEnv.registerFunction("PriceLateral",
                priceTable.createTemporalTableFunction("proctime", "CUSIP"));
        tEnv.registerFunction("PositionLateral",
                positionTable.createTemporalTableFunction("proctime", "CUSIP"));

        Table posJoin = tEnv.sqlQuery(
                "SELECT 'position' as Side, pos.Book, pos.CUSIP, px.Price / 100 * pos.Quantity AS MTM, pos.Quantity, px.Price FROM Positions AS pos, LATERAL TABLE (PriceLateral(pos.proctime)) as px WHERE px.CUSIP = pos.CUSIP"
        );


        Table priceJoin = tEnv.sqlQuery(
                "SELECT 'price' as Side, pos.Book, pos.CUSIP, px.Price / 100 * pos.Quantity as MTM, pos.Quantity, px.Price FROM Prices AS px, LATERAL TABLE (PositionLateral(px.proctime)) as pos WHERE pos.CUSIP = px.CUSIP"
        );

        DataStream<Tuple6> posJoinStream = tEnv.toAppendStream(posJoin, new TupleTypeInfo<>(TypeInformation.of(String.class), TypeInformation.of(String.class), TypeInformation.of(String.class), TypeInformation.of(Double.class), TypeInformation.of(Double.class), TypeInformation.of(Double.class)));
        DataStream<Tuple6> priceJoinStream = tEnv.toAppendStream(priceJoin, new TupleTypeInfo<>(TypeInformation.of(String.class), TypeInformation.of(String.class), TypeInformation.of(String.class), TypeInformation.of(Double.class), TypeInformation.of(Double.class), TypeInformation.of(Double.class)));

        DataStream<Tuple6> union = posJoinStream.union(priceJoinStream);
        union.print("XXX - MTM");
         */

        env.execute();
    }

    private static List<Tuple2<String,Double>> samplePrices = Arrays.asList(
            Tuple2.of("CA",101.0),
            Tuple2.of("CB",100.0),
            Tuple2.of("CC",100.0),
            Tuple2.of("CD", 102.0),
            Tuple2.of("CE", 99.0),
            Tuple2.of("CA", 102.0)
            );

    private static List<Tuple5<String,String,String,Double,Double>> samplePositions = Arrays.asList(
            Tuple5.of("ABOOK:CA", "ABOOK", "CA", 100.0, 100.0),
            Tuple5.of("BBOOK:CB", "BBOOK", "CB", 100.0, 100.0),
            Tuple5.of("ABOOK:CA", "ABOOK", "CA", 1000.0, 100.0),
            Tuple5.of("ABOOK:CA", "ABOOK", "CA", 2000.0, 100.0)
    );

    static class Joiner<LEFT,RIGHT,KEY,LEFTPRIMARY,RIGHTPRIMARY> extends CoProcessFunction<LEFT, RIGHT, Tuple2<LEFT, RIGHT>> {
        private transient MapState<KEY,Map<LEFTPRIMARY,LEFT>> leftCache;
        private transient MapState<KEY,Map<RIGHTPRIMARY,RIGHT>> rightCache;
        private KeySelector<LEFT,KEY> leftKey;
        private KeySelector<RIGHT,KEY> rightKey;
        private KeySelector<LEFT,LEFTPRIMARY> leftPrimaryKey;
        private KeySelector<RIGHT,RIGHTPRIMARY> rightPrimaryKey;
        private TypeInformation<LEFT> leftType;
        private TypeInformation<RIGHT> rightType;
        private TypeInformation<KEY> keyType;
        private TypeInformation<LEFTPRIMARY> leftPrimary;
        private TypeInformation<RIGHTPRIMARY> rightPrimary;


        public Joiner(KeySelector<LEFT,KEY> leftKey,
                      KeySelector<RIGHT,KEY> rightKey,
                      KeySelector<LEFT,LEFTPRIMARY> leftPrimaryKey,
                      KeySelector<RIGHT,RIGHTPRIMARY> rightPrimaryKey,
                      TypeInformation<LEFT> leftType, TypeInformation<RIGHT> rightType,
                      TypeInformation<KEY> keyType,
                      TypeInformation<LEFTPRIMARY> leftPrimary,
                      TypeInformation<RIGHTPRIMARY> rightPrimary) {
            this.leftKey = leftKey;
            this.rightKey = rightKey;
            this.leftType = leftType;
            this.rightType = rightType;
            this.keyType = keyType;
            this.leftPrimary = leftPrimary;
            this.rightPrimary = rightPrimary;
            this.leftPrimaryKey = leftPrimaryKey;
            this.rightPrimaryKey = rightPrimaryKey;
        }

        @Override
        public void open(Configuration parameters) {
            leftCache = getRuntimeContext().getMapState(new MapStateDescriptor<>(
                    "leftCache",
                    keyType,
                    new MapTypeInfo<>(leftPrimary, leftType)
            ));

            rightCache = getRuntimeContext().getMapState(new MapStateDescriptor<>(
                    "rightCache",
                    keyType,
                    new MapTypeInfo<>(rightPrimary, rightType)
            ));
        }

        @Override
        public void processElement1(LEFT msg, Context context, Collector<Tuple2<LEFT,RIGHT>> collector) throws Exception {
            KEY k = leftKey.getKey(msg);
            Map<LEFTPRIMARY,LEFT> map = leftCache.get(k);
            if (map == null) {
                map = new HashMap<>();
            }
            map.put(leftPrimaryKey.getKey(msg), msg);
            leftCache.put(k, map);

            Map<RIGHTPRIMARY,RIGHT> rights = rightCache.get(k);
            if (rights != null) {
                for (RIGHT right : rights.values()) {
                    collector.collect(Tuple2.of(msg, right));
                }
            }
        }

        @Override
        public void processElement2(RIGHT msg, Context context, Collector<Tuple2<LEFT, RIGHT>> collector) throws Exception {
            KEY k = rightKey.getKey(msg);
            Map<RIGHTPRIMARY,RIGHT> map = rightCache.get(k);
            if (map == null) {
                map = new HashMap<>();
            }
            map.put(rightPrimaryKey.getKey(msg), msg);
            rightCache.put(k, map);

            Map<LEFTPRIMARY,LEFT> lefts = leftCache.get(k);
            if (lefts != null) {
                for (LEFT left : lefts.values()) {
                    collector.collect(Tuple2.of(left, msg));
                }
            }
        }
    }

    static class EndlessIterator<T> implements Iterator<T>, Serializable {
        private List<T> source;
        private T repeat;
        private Iterator<T> iterator;

        EndlessIterator(List<T> coll, T repeat) {
            source = coll;
            this.repeat = repeat;
        }

        public boolean hasNext() { return true; }

        public T next() {
            if (iterator == null) iterator = source.iterator();

            if (iterator.hasNext()) {
                return iterator.next();
            }

            try {
                Thread.sleep(100);
            } catch (Exception e) {}

            return repeat;
        }
    }

    private static String[] CUSIPS = new String[] { "CA", "CB", "CC", "CD", "CE" };
    private static String[] BOOKS = new String[] { "ABOOK", "BBOOK", "CBOOK" };

    static class PriceIterator implements Iterator<Tuple2<String,Double>>, Serializable {
        private Random r = new Random();

        public boolean hasNext() { return true; }

        public Tuple2 next() {
            try { Thread.sleep(r.nextInt(100)); } catch (Exception e) {}
            return new Tuple2<>(CUSIPS[r.nextInt(CUSIPS.length)], 95.0+10*r.nextDouble());
        }
    }

    static class PositionIterator implements Iterator<Tuple5<String,String,String,Double,Double>>, Serializable {
        private Random r = new Random();

        public boolean hasNext() { return true; }

        public Tuple5<String,String,String,Double,Double> next() {
            try { Thread.sleep(r.nextInt(100)); } catch (Exception e) {}

            String book = BOOKS[r.nextInt(BOOKS.length)];
            String cusip = CUSIPS[r.nextInt(CUSIPS.length)];
            return new Tuple5<>(
                    book + ":"+cusip,
                    book,
                    cusip,
                    1000000*r.nextDouble(),
                    95.0+10*r.nextDouble()
            );
        }
    }
}
