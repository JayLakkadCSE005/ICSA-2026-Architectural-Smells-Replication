/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.broker.jmx.util.base;

import org.apache.activemq.broker.jmx.core.healthview.health.HealthStatus;
import org.apache.activemq.broker.jmx.util.SlowConsumerEntryOpenTypeFactory;
import org.apache.activemq.broker.jmx.util.StreamMessageOpenTypeFactory;
import org.apache.activemq.broker.jmx.util.factory.*;
import org.apache.activemq.broker.region.policy.slowconsumer.SlowConsumerEntry;
import org.apache.activemq.broker.scheduler.Job;
import org.apache.activemq.command.ActiveMQBlobMessage;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQStreamMessage;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import java.util.HashMap;
import java.util.Map;

public final class OpenTypeSupport {

    private static final Map<Class, AbstractOpenTypeFactory> OPEN_TYPE_FACTORIES = new HashMap<Class, AbstractOpenTypeFactory>();


    static {
        OPEN_TYPE_FACTORIES.put(ActiveMQMessage.class, new MessageOpenTypeFactory());
        OPEN_TYPE_FACTORIES.put(ActiveMQBytesMessage.class, new ByteMessageOpenTypeFactory());
        OPEN_TYPE_FACTORIES.put(ActiveMQMapMessage.class, new MapMessageOpenTypeFactory());
        OPEN_TYPE_FACTORIES.put(ActiveMQObjectMessage.class, new ObjectMessageOpenTypeFactory());
        OPEN_TYPE_FACTORIES.put(ActiveMQStreamMessage.class, new StreamMessageOpenTypeFactory());
        OPEN_TYPE_FACTORIES.put(ActiveMQTextMessage.class, new TextMessageOpenTypeFactory());
        OPEN_TYPE_FACTORIES.put(Job.class, new JobOpenTypeFactory());
        OPEN_TYPE_FACTORIES.put(SlowConsumerEntry.class, new SlowConsumerEntryOpenTypeFactory());
        OPEN_TYPE_FACTORIES.put(ActiveMQBlobMessage.class, new ActiveMQBlobMessageOpenTypeFactory());
        OPEN_TYPE_FACTORIES.put(HealthStatus.class, new HealthStatusOpenTypeFactory());
    }

    private OpenTypeSupport() {
    }

    public static OpenTypeFactory getFactory(Class<?> clazz) throws OpenDataException {
        return OPEN_TYPE_FACTORIES.get(clazz);
    }

    public static CompositeData convert(Object message) throws OpenDataException {
        OpenTypeFactory f = getFactory(message.getClass());
        if (f == null) {
            throw new OpenDataException("Cannot create a CompositeData for type: " + message.getClass().getName());
        }
        CompositeType ct = f.getCompositeType();
        Map<String, Object> fields = f.getFields(message);
        return new CompositeDataSupport(ct, fields);
    }

}
