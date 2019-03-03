/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uwh;

import gsp.BuySell;
import gsp.FirmAccount;
import gsp.Offering;
import gsp.OfferingStack;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import scala.Char;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Skeleton for a Flink Streaming Job.
 *
 * <p>For a tutorial how to write a Flink streaming application, check the
 * tutorials and examples on the <a href="http://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class StreamingJob {
	private static class AvroDeserializer<T> implements DeserializationSchema<T> {
		private final Class<T> clazz;

		public AvroDeserializer(Class<T> clazz) {
			this.clazz = clazz;
		}

		@Override
		public T deserialize(byte[] bytes) throws IOException {
			SpecificDatumReader<T> reader = new SpecificDatumReader<>(clazz);
			DataFileReader<T> r = new DataFileReader<>(new SeekableByteArrayInput(bytes), reader);
			return r.next();
		}

		@Override
		public boolean isEndOfStream(T obj) {
			return false;
		}

		@Override
		public TypeInformation<T> getProducedType() {
			return TypeInformation.of(clazz);
		}
	}

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		Properties props = new Properties();
		props.setProperty("bootstrap.servers", "localhost:9092");
		props.setProperty("zookeeper.connect", "localhost:2181");
		props.setProperty("group.id", "flink.test");

		FlinkKafkaConsumer<Offering> consumer = new FlinkKafkaConsumer<>("offerings", new AvroDeserializer<>(Offering.class), props);
		consumer.setStartFromEarliest();

		DataStream<Offering> stream = env.addSource(consumer);
		KeyedStream<Offering,CharSequence> kstream = stream.keyBy(Offering::getCUSIP);
		kstream.process(new StackBuilder()).print();

		env.execute("Flink Streaming Java API Skeleton");
	}

	private static class StackBuilder extends KeyedProcessFunction<CharSequence, Offering, OfferingStack> {
		private transient ValueState<OfferingStack> stackState;

		@Override
		public void open(Configuration parameters) throws Exception {
			ValueStateDescriptor<OfferingStack> desc = new ValueStateDescriptor<OfferingStack>(
				"stack",
				TypeInformation.of(OfferingStack.class),
				null
			);
			stackState = getRuntimeContext().getState(desc);
		}

		@Override
		public void processElement(Offering offering, Context context, Collector<OfferingStack> collector) throws Exception {
			OfferingStack stack = stackState.value();

			if (stack == null) {
				stack = new OfferingStack(offering.getCUSIP(), new ArrayList<>(), new ArrayList<>());
			}

			stack = putOnStack(stack, offering);
			stackState.update(stack);

			collector.collect(stack);
		}

		private OfferingStack putOnStack(OfferingStack stack, Offering offering) {
			if (offering.getSide() == BuySell.BUY) {
				stack.setBuyPrices(mergeIntoStack(stack.getBuyPrices(), offering));
			} else {
				stack.setSellPrices(mergeIntoStack(stack.getSellPrices(), offering));
			}

			return stack;
		}

		private List<Offering> mergeIntoStack(List<Offering> offerings, Offering offering) {
			List<Offering> temp =
					offerings.stream()
					.filter(off -> !off.getVenue().equals(offering.getVenue()))
					.collect(Collectors.toList());

			temp.add(offering);
			temp.sort(new Comparator<Offering>() {
				@Override
				public int compare(Offering off1, Offering off2) {
					if (offering.getSide() == BuySell.BUY) {
						if (off2.getPrice() > off1.getPrice()) return 1;
						else if (off2.getPrice().equals(off1.getPrice())) return 0;
						else return -1;
					} else {
						if (off1.getPrice() > off2.getPrice()) return 1;
						else if (off2.getPrice().equals(off1.getPrice())) return 0;
						else return -1;
					}
				}
			});

			return temp;
		}
	}
}
