Cluster Details
===============

### Zookeeper 
  * node0:2181

### Brokers
  * node0:9092,node1:9092,node2:9092,node3:9092


Kafka Setup
===========


## Creating the Kafka topics
    
    bin/kafka-topics.sh --create --zookeeper node0:2181 --replication-factor 2 --partition 2 --topic streambench1
    
## Inspect data in topics

    bin/kafka-console-consumer.sh --zookeeper node0:2181 --topic streambench1

## Increase the partition

    bin/kafka-topics.sh --alter --zookeeper node0:2181 --replication-factor 2 --partition 3 --topic streambench1


Managing DT App
===============

### Build the app
    mvn clean install -DskipTests

### Configuration Sample
    vim ~/.dt/dt-site.xml
    
Example:

```
  <!-- kafka Ingestion application -->
  <property>
    <name>dt.operator.KafkaProducerOperator.prop.topic</name>
    <value>streambench1</value>
  </property>
  <property>
    <name>dt.operator.KafkaProducerOperator.prop.brokerList</name>
    <value>node0:9092,node1:9092,node2:9092,node3:9092/value>
  </property>
  <property>
    <name>dt.operator.KafkaProducerOperator.prop.partitionNum</name>
    <value>1</value>
  </property>
  <property>
    <name>dt.operator.KafkaIngestionConsumerOperator.prop.topic</name>
    <value>streambench1</value>
  </property>
  <property>
    <name>dt.operator.KafkaIngestionConsumerOperator.prop.brokerSet</name>
    <value>node0:9092,node1:9092,node2:9092,node3:9092</value>
  </property>
  <property>
    <name>dt.operator.KafkaIngestionConsumerOperator.prop.strategy</name>
    <value>one_to_one</value>
  </property>
  <property>
    <name>dt.operator.KafkaIngestionConsumerOperator.prop.initialOffset</name>
    <value>latest</value>
  </property>
  <property>
    <name>kafka.consumertype</name>
    <value>highlevel</value>
  </property>
  <property>
    <name>kafka.zookeeper</name>
    <value>localhost:2182</value>
  </property>
```

### Dynamically adjust the partition by adding kafka partition (dynamic deletion is not officially support in kafka)
    bin/kafka-topics.sh --alter --zookeeper node0:2181 --replication-factor 2 --partition 3 --topic streambench1


Design
===============

### Producer Operator
   * PartitionableKafkaOutputOperator is the producer output. 
   * You can setup partitionNum, threadNum(how many threads for each partition) and interval(between 2 messages) to control the throughput.

## Consumer Operator
   * PartitionableKafkaInputOperator directly extends AbstractPartitionableKafkaInputOperator(from Malhar library)
   * If you set the operator property "strategy" to "one_to_one"(case insensitive) The engine will dynamically allocate one operator partition for each kafka pertition. (You can dynamically change the kafka partition to see the partition change)
   * If you set the operator property  "strategy" to "one_to_many"(case insensitive) and set "msgRateUpperBound"/"byteRateUpperBound"(limit of msg/s, bytes/s per partition). It will dynamically allocate as few partitions as possible without breaking the limit (You can change the throughput of the Output Application to see the partition change)

