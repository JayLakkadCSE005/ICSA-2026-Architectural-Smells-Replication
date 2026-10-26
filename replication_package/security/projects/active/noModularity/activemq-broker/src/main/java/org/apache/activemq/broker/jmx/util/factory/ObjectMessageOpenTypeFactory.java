package org.apache.activemq.broker.jmx.util.factory;

import org.apache.activemq.broker.jmx.util.base.MessageOpenTypeFactory;
import org.apache.activemq.command.ActiveMQObjectMessage;

import javax.management.openmbean.OpenDataException;
import java.util.Map;

public class ObjectMessageOpenTypeFactory extends MessageOpenTypeFactory {
    @Override
    protected String getTypeName() {
        return ActiveMQObjectMessage.class.getName();
    }

    @Override
    protected void init() throws OpenDataException {
        super.init();
    }

    @Override
    public Map<String, Object> getFields(Object o) throws OpenDataException {
        Map<String, Object> rc = super.getFields(o);
        return rc;
    }
}
