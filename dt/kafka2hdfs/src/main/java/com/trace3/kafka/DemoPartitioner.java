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

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;


/**
 * A simple partitioner class for test purpose
 * Key is a int string
 * Messages are distributed to 2 partitions
 * One for even number, the other for odd
 *
 * @since 0.9.3
 */
public class DemoPartitioner implements Partitioner
{
  public DemoPartitioner (VerifiableProperties props) {
    
  }
  @Override
  public int partition(Object key, int num_Partitions)
  {
    return Integer.parseInt((String)key)%num_Partitions;
  }
}
