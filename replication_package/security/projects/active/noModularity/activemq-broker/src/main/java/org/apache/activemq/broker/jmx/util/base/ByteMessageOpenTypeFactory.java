package org.apache.activemq.broker.jmx.util.base;

import jakarta.jms.JMSException;
import org.apache.activemq.command.ActiveMQBytesMessage;

import javax.management.openmbean.ArrayType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.SimpleType;
import java.util.Map;

class ByteMessageOpenTypeFactory extends MessageOpenTypeFactory {


    @Override
    protected String getTypeName() {
        return ActiveMQBytesMessage.class.getName();
    }

    @Override
    protected void init() throws OpenDataException {
        super.init();
        addItem(CompositeDataConstants.BODY_LENGTH, "Body length", SimpleType.LONG);
        addItem(CompositeDataConstants.BODY_PREVIEW, "Body preview", new ArrayType(1, SimpleType.BYTE));
    }

    @Override
    public Map<String, Object> getFields(Object o) throws OpenDataException {
        ActiveMQBytesMessage m = (ActiveMQBytesMessage) o;
        m.setReadOnlyBody(true);
        Map<String, Object> rc = super.getFields(o);
        long length = 0;
        try {
            length = m.getBodyLength();
            rc.put(CompositeDataConstants.BODY_LENGTH, length);
        } catch (JMSException e) {
            rc.put(CompositeDataConstants.BODY_LENGTH, 0L);
        }
        try {
            byte preview[] = new byte[(int) Math.min(length, 255)];
            m.readBytes(preview);
            m.reset();

            // This is whack! Java 1.5 JMX spec does not support primitive
            // arrays!
            // In 1.6 it seems it is supported.. but until then...
            Byte data[] = new Byte[preview.length];
            for (int i = 0; i < data.length; i++) {
                data[i] = preview[i];
            }

            rc.put(CompositeDataConstants.BODY_PREVIEW, data);
        } catch (JMSException e) {
            rc.put(CompositeDataConstants.BODY_PREVIEW, new Byte[]{});
        }
        return rc;
    }

}
