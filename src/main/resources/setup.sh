#!/bin/bash

# General Setup

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/
export PATH=$JAVA_HOME/bin:$PATH

# Kafka

export KAFKA_HOME=/Users/vmahrwald/data/kafka_2.11-2.0.0
cd $KAFKA_HOME

# Start Kafka
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties

# Create topics
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic accounts --partitions 1 --replication-factor 1 --config cleanup.policy=compact --config segment.bytes=8388608

bin/kafka-topics.sh --zookeeper localhost:2181 --list
bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic accounts

bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type topics --entity-name accounts --alter --add-config segment.bytes=1048576

bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic offerings --partitions 10 --replication-factor 1
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic stacks --partitions 10 --replication-factor 1

# Testing various compaction policies
bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type topics --entity-name offerings --alter --add-config cleanup.policy=compact
bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type topics --entity-name offerings --alter --add-config segment.bytes=1048576
bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type topics --entity-name stacks --alter --add-config cleanup.policy=compact
bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type topics --entity-name stacks --alter --add-config segment.bytes=1048576

bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic accounts --offset earliest --partition 0

# Number topic
bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic numbers
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic numbers --partitions 10 --replication-factor 1

# Stop Kafka

bin/kafka-server-stop.sh
# then
bin/zookeeper-server-stop.sh

# Flink

export FLINK_HOME=/Users/vmahrwald/Development/flink-1.9.1
cd $FLINK_HOME

bin/jobmanager.sh start

bin/taskmanager.sh start-foreground
bin/taskmanager.sh start-foreground
bin/taskmanager.sh start-foreground
bin/taskmanager.sh start-foreground

# Run Job

bin/flink run -d -c org.uwh.Stateful /Users/vmahrwald/Documents/code/work/flink/target/flink-1.0-SNAPSHOT.jar

bin/flink cancel <id>

