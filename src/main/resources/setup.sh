#!/bin/bash

bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic accounts --partitions 1 --replication-factor 1 --config cleanup.policy=compact --config segment.bytes=8388608

bin/kafka-topics.sh --zookeeper localhost:2181 --list
bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic accounts

bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type topics --entity-name accounts --alter --add-config segment.bytes=1048576


bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic offerings --partitions 10 --replication-factor 1
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic stacks --partitions 10 --replication-factor 1

bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type topics --entity-name offerings --alter --add-config cleanup.policy=compact
bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type topics --entity-name offerings --alter --add-config segment.bytes=1048576
bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type topics --entity-name stacks --alter --add-config cleanup.policy=compact
bin/kafka-configs.sh --zookeeper localhost:2181 --entity-type topics --entity-name stacks --alter --add-config segment.bytes=1048576



bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic accounts --offset earliest --partition 0
