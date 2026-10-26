package org.apache.activemq.broker.jmx.util.base;

import jakarta.jms.JMSException;
import org.apache.activemq.command.ActiveMQMapMessage;

import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.SimpleType;
import java.util.Map;

class MapMessageOpenTypeFactory extends MessageOpenTypeFactory {

    @Override
    protected String getTypeName() {
        return ActiveMQMapMessage.class.getName();
    }

    @Override
    protected void init() throws OpenDataException {
        super.init();
        addItem(CompositeDataConstants.CONTENT_MAP, "Content map", SimpleType.STRING);
    }

    @Override
    public Map<String, Object> getFields(Object o) throws OpenDataException {
        ActiveMQMapMessage m = (ActiveMQMapMessage) o;
        Map<String, Object> rc = super.getFields(o);
        try {
            rc.put(CompositeDataConstants.CONTENT_MAP, "" + m.getContentMap());
        } catch (JMSException e) {
            rc.put(CompositeDataConstants.CONTENT_MAP, "");
        }
        return rc;
    }
}
