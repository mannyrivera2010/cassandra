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

package org.apache.cassandra.metrics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Timer;
import org.apache.cassandra.net.MessagingService;

import static org.apache.cassandra.metrics.CassandraMetricsRegistry.Metrics;

/**
 * Metrics for messages
 */
public class MessagingMetrics
{
    private static final MetricNameFactory factory = new DefaultNameFactory("Messaging");
    private final ConcurrentHashMap<MessagingService.Verb, Timer> processingLatency = new ConcurrentHashMap<>();

    public void addProcessingLatency(MessagingService.Verb verb, long timeTaken, TimeUnit timeUnit)
    {
        if (timeTaken < 0)
        {
            // the measurement is not accurate, ignore the negative timeTaken
            return;
        }

        Timer timer = processingLatency.get(verb);
        if (timer == null)
        {
            timer = processingLatency.computeIfAbsent(verb, k -> Metrics.timer(factory.createMetricName(verb.toString() + "-ProcessingLatency")));
        }
        timer.update(timeTaken, timeUnit);
    }
}