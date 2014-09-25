package com.trace3.kafka2hdfs;

import java.util.HashSet;

import org.apache.hadoop.conf.Configuration;

import com.datatorrent.api.Context.OperatorContext;
import com.datatorrent.api.Context.PortContext;
import com.datatorrent.api.DAG;
import com.datatorrent.api.DAG.Locality;
import com.datatorrent.api.StreamingApplication;
import com.datatorrent.api.annotation.ApplicationAnnotation;
import com.datatorrent.contrib.kafka.KafkaConsumer;
import com.datatorrent.contrib.kafka.PartitionableKafkaSinglePortStringInputOperator;
import com.datatorrent.contrib.kafka.SimpleKafkaConsumer;
import com.datatorrent.lib.stream.DevNullCounter;

@ApplicationAnnotation(name="KafkaIngestionDemo")
public class Kafka2HdfsDemo implements StreamingApplication
{
  @Override
  public void populateDAG(DAG dag, Configuration conf)
  {
    // topic is set via property file
    PartitionableKafkaSinglePortStringInputOperator input = dag.addOperator("KafkaConsumerOperator", new PartitionableKafkaSinglePortStringInputOperator());
    KafkaConsumer consumer = new SimpleKafkaConsumer(null, 10000, 100000, "test_kafka_autop_client", new HashSet<Integer>());
    input.setTuplesBlast(1024 * 1024); // maximum number of events to emit in one go
    input.setConsumer(consumer);
    input.setBrokerSet("localhost:9092");
    input.setTopic("trace3.streambench1");

    DevNullCounter<Object> counter = dag.addOperator("Counter", new DevNullCounter<Object>());
    dag.addStream("Messages", input.outputPort, counter.data).setLocality(Locality.CONTAINER_LOCAL);
    dag.setInputPortAttribute(counter.data, PortContext.PARTITION_PARALLEL, true);
    dag.setAttribute(input, OperatorContext.INITIAL_PARTITION_COUNT, 1);
  }
}
