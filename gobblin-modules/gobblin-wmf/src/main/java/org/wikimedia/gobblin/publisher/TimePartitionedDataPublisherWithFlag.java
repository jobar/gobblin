/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wikimedia.gobblin.publisher;

import lombok.extern.slf4j.Slf4j;
import org.apache.gobblin.configuration.ConfigurationKeys;
import org.apache.gobblin.configuration.State;
import org.apache.gobblin.configuration.WorkUnitState;
import org.apache.gobblin.publisher.TimePartitionedDataPublisher;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.Collection;

@Slf4j
public class TimePartitionedDataPublisherWithFlag extends TimePartitionedDataPublisher {

    protected final TimePartitionedFlagDataPublisher flagPublisher;

    public TimePartitionedDataPublisherWithFlag(State state) throws IOException {
        super(state);
        this.flagPublisher = this.closer.register(new TimePartitionedFlagDataPublisher(state));
    }

    @Override
    public void publish(Collection<? extends WorkUnitState> states) throws IOException {
        super.publish(states);

        // Update PUBLISHER_DIRS from parent-task
        log.debug("Adding published dirs to global state before applying flag-publishing");
        for (Path path : this.publisherOutputDirs) {
            this.state.appendToSetProp(ConfigurationKeys.PUBLISHER_DIRS, path.toString());
            log.debug("Adding " + path.toString() + " to publisher-dirs");
        }

        this.flagPublisher.publish(states);
    }

}