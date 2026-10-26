package org.apache.activemq.broker.jmx.util.base;

import jakarta.jms.JMSException;
import org.apache.activemq.command.ActiveMQBlobMessage;

import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.SimpleType;
import java.util.Map;

class ActiveMQBlobMessageOpenTypeFactory extends MessageOpenTypeFactory {

    @Override
    protected String getTypeName() {
        return ActiveMQBlobMessage.class.getName();
    }

    @Override
    protected void init() throws OpenDataException {
        super.init();
        addItem(CompositeDataConstants.MESSAGE_URL, "Body Url", SimpleType.STRING);
    }

    @Override
    public Map<String, Object> getFields(Object o) throws OpenDataException {
        ActiveMQBlobMessage m = (ActiveMQBlobMessage) o;
        Map<String, Object> rc = super.getFields(o);
        try {
            rc.put(CompositeDataConstants.MESSAGE_URL, "" + m.getURL().toString());
        } catch (JMSException e) {
            rc.put(CompositeDataConstants.MESSAGE_URL, "");
        }
        return rc;
    }
}
