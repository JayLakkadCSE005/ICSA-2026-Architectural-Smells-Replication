package org.apache.activemq.broker.jmx.util;

import org.apache.activemq.broker.jmx.util.factory.AbstractOpenTypeFactory;
import org.apache.activemq.broker.region.policy.slowconsumer.SlowConsumerEntry;

import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.SimpleType;
import java.util.Map;

public class SlowConsumerEntryOpenTypeFactory extends AbstractOpenTypeFactory {
    @Override
    protected String getTypeName() {
        return SlowConsumerEntry.class.getName();
    }

    @Override
    protected void init() throws OpenDataException {
        super.init();
        addItem("subscription", "the subscription view", SimpleType.OBJECTNAME);
        addItem("slowCount", "number of times deemed slow", SimpleType.INTEGER);
        addItem("markCount", "number of periods remaining slow", SimpleType.INTEGER);
    }

    @Override
    public Map<String, Object> getFields(Object o) throws OpenDataException {
        SlowConsumerEntry entry = (SlowConsumerEntry) o;
        Map<String, Object> rc = super.getFields(o);
        rc.put("subscription", entry.getSubscription());
        rc.put("slowCount", entry.getSlowCount());
        rc.put("markCount", entry.getMarkCount());
        return rc;
    }
}
