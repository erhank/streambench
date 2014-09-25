/*
 * Copyright (c) 2013 DataTorrent, Inc. ALL Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.trace3.kafka;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import com.datatorrent.api.ActivationListener;
import com.datatorrent.api.Context.OperatorContext;
import com.datatorrent.api.DefaultPartition;
import com.datatorrent.api.annotation.ShipContainingJars;
import com.datatorrent.api.InputOperator;
import com.datatorrent.api.Partitioner;
import com.yammer.metrics.Metrics;

import java.util.Map;

/**
 * This operator keep sending constant messages(1kb each) in {@link #threadNum} threads
 * Messages are distributed evenly to partitions
 *
 * It will also be split to {@link #partitionNum} partitions
 * Please set the {@link #partitionNum} in property file to get optimized performance
 *
 * @since 0.9.3
 */
@ShipContainingJars(classes={kafka.javaapi.consumer.SimpleConsumer.class, org.I0Itec.zkclient.ZkClient.class, scala.ScalaObject.class, Metrics.class})
public class PartitionableKafkaOutputOperator implements Partitioner<PartitionableKafkaOutputOperator>, InputOperator, ActivationListener<OperatorContext>
{

  private String topic = "benchmark";

  private int partitionNum = 1;

  private String brokerList = "localhost:9092";

  private int threadNum = 1;

  //define constant message
  private byte[] constantMsg = null;

  private int msgSize = 1024;
  
  private int interval = 500;

  @Override
  public void beginWindow(long arg0)
  {

  }

  @Override
  public void endWindow()
  {
  }

  @Override
  public void setup(OperatorContext arg0)
  {

  }

  @Override
  public void teardown()
  {

  }

  @Override
  public void emitTuples()
  {

  }

  @Override
  public void partitioned(Map<Integer, Partition<PartitionableKafkaOutputOperator>> partitions)
  {
  }

  @Override
  public Collection<Partition<PartitionableKafkaOutputOperator>> definePartitions(Collection<Partition<PartitionableKafkaOutputOperator>> partitions, int pNum)
  {

    ArrayList<Partition<PartitionableKafkaOutputOperator>> newPartitions = new ArrayList<Partitioner.Partition<PartitionableKafkaOutputOperator>>(partitionNum);

    for (int i = 0; i < partitionNum; i++) {
      PartitionableKafkaOutputOperator bpkoo = new PartitionableKafkaOutputOperator();
      bpkoo.setPartitionNum(partitionNum);
      bpkoo.setTopic(topic);
      bpkoo.setBrokerList(brokerList);
      Partition<PartitionableKafkaOutputOperator> p = new DefaultPartition<PartitionableKafkaOutputOperator>(bpkoo);
      newPartitions.add(p);
    }
    return newPartitions;
  }

  @Override
  public void activate(OperatorContext arg0)
  {

    constantMsg = new byte[msgSize];
    for (int i = 0; i < constantMsg.length; i++) {
      constantMsg[i] = (byte) ('a' + i%26);
    }

    for (int i = 0; i < threadNum; i++) {
      new Thread(new Runnable() {
        @Override
        public void run()
        {
          Properties props = new Properties();
          props.setProperty("serializer.class", "kafka.serializer.StringEncoder");
          props.setProperty("key.serializer.class", "kafka.serializer.StringEncoder");
          props.put("metadata.broker.list", brokerList);
//          props.put("metadata.broker.list", "localhost:9092");
          props.setProperty("partitioner.class", DemoPartitioner.class.getCanonicalName());
          props.setProperty("producer.type", "async");
//          props.setProperty("send.buffer.bytes", "1048576");
          props.setProperty("topic.metadata.refresh.interval.ms", "100000");

          Producer<String, String>producer = new Producer<String, String>(new ProducerConfig(props));
          long k = 0;
          while (true) {
            producer.send(new KeyedMessage<String, String>(topic, "" + (k++), new String(constantMsg)));
            if(k==Long.MAX_VALUE)
              k=0;
            try {
              Thread.sleep(interval);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      }).start();
    }




  }

  @Override
  public void deactivate()
  {

  }

  public String getTopic()
  {
    return topic;
  }

  public void setTopic(String topic)
  {
    this.topic = topic;
  }

  public int getPartitionNum()
  {
    return partitionNum;
  }

  public void setPartitionNum(int partitionNum)
  {
    this.partitionNum = partitionNum;
  }

  public String getBrokerList()
  {
    return brokerList;
  }

  public void setBrokerList(String brokerList)
  {
    this.brokerList = brokerList;
  }

  public int getThreadNum()
  {
    return threadNum;
  }

  public void setThreadNum(int threadNum)
  {
    this.threadNum = threadNum;
  }

  public void setMsgSize(int msgSize)
  {
    this.msgSize = msgSize;
  }

  public int getMsgSize()
  {
    return msgSize;
  }
  
  public int getInterval()
  {
    return interval;
  }
  
  public void setInterval(int interval)
  {
    this.interval = interval;
  }


}
