package org.apache.activemq.broker.jmx.util.base;

import jakarta.jms.JMSException;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.SimpleType;
import java.util.Map;

class TextMessageOpenTypeFactory extends MessageOpenTypeFactory {

    @Override
    protected String getTypeName() {
        return ActiveMQTextMessage.class.getName();
    }

    @Override
    protected void init() throws OpenDataException {
        super.init();
        addItem(CompositeDataConstants.MESSAGE_TEXT, CompositeDataConstants.MESSAGE_TEXT, SimpleType.STRING);
    }

    @Override
    public Map<String, Object> getFields(Object o) throws OpenDataException {
        ActiveMQTextMessage m = (ActiveMQTextMessage) o;
        Map<String, Object> rc = super.getFields(o);
        try {
            rc.put(CompositeDataConstants.MESSAGE_TEXT, "" + m.getText());
        } catch (JMSException e) {
            rc.put(CompositeDataConstants.MESSAGE_TEXT, "");
        }
        return rc;
    }
}
