package org.apache.activemq.broker.jmx.util.base;

import jakarta.jms.DeliveryMode;
import org.apache.activemq.broker.jmx.util.factory.AbstractOpenTypeFactory;
import org.apache.activemq.command.ActiveMQMessage;
import org.fusesource.hawtbuf.UTF8Buffer;

import javax.management.openmbean.*;
import java.io.IOException;
import java.util.*;

public class MessageOpenTypeFactory extends AbstractOpenTypeFactory {
    protected TabularType stringPropertyTabularType;
    protected TabularType booleanPropertyTabularType;
    protected TabularType bytePropertyTabularType;
    protected TabularType shortPropertyTabularType;
    protected TabularType intPropertyTabularType;
    protected TabularType longPropertyTabularType;
    protected TabularType floatPropertyTabularType;
    protected TabularType doublePropertyTabularType;

    @Override
    protected String getTypeName() {
        return ActiveMQMessage.class.getName();
    }

    @Override
    protected void init() throws OpenDataException {
        super.init();
        addItem("JMSCorrelationID", "JMSCorrelationID", SimpleType.STRING);
        addItem("JMSDestination", "JMSDestination", SimpleType.STRING);
        addItem("JMSMessageID", "JMSMessageID", SimpleType.STRING);
        addItem("JMSReplyTo", "JMSReplyTo", SimpleType.STRING);
        addItem("JMSType", "JMSType", SimpleType.STRING);
        addItem("JMSDeliveryMode", "JMSDeliveryMode", SimpleType.STRING);
        addItem("JMSExpiration", "JMSExpiration", SimpleType.LONG);
        addItem("JMSPriority", "JMSPriority", SimpleType.INTEGER);
        addItem("JMSRedelivered", "JMSRedelivered", SimpleType.BOOLEAN);
        addItem("JMSTimestamp", "JMSTimestamp", SimpleType.DATE);
        addItem(CompositeDataConstants.JMSXGROUP_ID, "Message Group ID", SimpleType.STRING);
        addItem(CompositeDataConstants.JMSXGROUP_SEQ, "Message Group Sequence Number", SimpleType.INTEGER);
        addItem(CompositeDataConstants.JMSXUSER_ID, "The user that sent the message", SimpleType.STRING);
        addItem(CompositeDataConstants.BROKER_PATH, "Brokers traversed", SimpleType.STRING);
        addItem(CompositeDataConstants.ORIGINAL_DESTINATION, "Original Destination Before Senting To DLQ", SimpleType.STRING);
        addItem(CompositeDataConstants.PROPERTIES, "User Properties Text", SimpleType.STRING);

        // now lets expose the type safe properties
        stringPropertyTabularType = createTabularType(String.class, SimpleType.STRING);
        booleanPropertyTabularType = createTabularType(Boolean.class, SimpleType.BOOLEAN);
        bytePropertyTabularType = createTabularType(Byte.class, SimpleType.BYTE);
        shortPropertyTabularType = createTabularType(Short.class, SimpleType.SHORT);
        intPropertyTabularType = createTabularType(Integer.class, SimpleType.INTEGER);
        longPropertyTabularType = createTabularType(Long.class, SimpleType.LONG);
        floatPropertyTabularType = createTabularType(Float.class, SimpleType.FLOAT);
        doublePropertyTabularType = createTabularType(Double.class, SimpleType.DOUBLE);

        addItem(CompositeDataConstants.STRING_PROPERTIES, "User String Properties", stringPropertyTabularType);
        addItem(CompositeDataConstants.BOOLEAN_PROPERTIES, "User Boolean Properties", booleanPropertyTabularType);
        addItem(CompositeDataConstants.BYTE_PROPERTIES, "User Byte Properties", bytePropertyTabularType);
        addItem(CompositeDataConstants.SHORT_PROPERTIES, "User Short Properties", shortPropertyTabularType);
        addItem(CompositeDataConstants.INT_PROPERTIES, "User Integer Properties", intPropertyTabularType);
        addItem(CompositeDataConstants.LONG_PROPERTIES, "User Long Properties", longPropertyTabularType);
        addItem(CompositeDataConstants.FLOAT_PROPERTIES, "User Float Properties", floatPropertyTabularType);
        addItem(CompositeDataConstants.DOUBLE_PROPERTIES, "User Double Properties", doublePropertyTabularType);
    }

    @Override
    public Map<String, Object> getFields(Object o) throws OpenDataException {
        ActiveMQMessage m = (ActiveMQMessage) o;
        Map<String, Object> rc = super.getFields(o);
        rc.put("JMSCorrelationID", m.getJMSCorrelationID());
        rc.put("JMSDestination", "" + m.getJMSDestination());
        rc.put("JMSMessageID", m.getJMSMessageID());
        rc.put("JMSReplyTo", toString(m.getJMSReplyTo()));
        rc.put("JMSType", m.getJMSType());
        rc.put("JMSDeliveryMode", m.getJMSDeliveryMode() == DeliveryMode.PERSISTENT ? "PERSISTENT" : "NON-PERSISTENT");
        rc.put("JMSExpiration", m.getJMSExpiration());
        rc.put("JMSPriority", m.getJMSPriority());
        rc.put("JMSRedelivered", m.getJMSRedelivered());
        rc.put("JMSTimestamp", new Date(m.getJMSTimestamp()));
        rc.put(CompositeDataConstants.JMSXGROUP_ID, m.getGroupID());
        rc.put(CompositeDataConstants.JMSXGROUP_SEQ, m.getGroupSequence());
        rc.put(CompositeDataConstants.JMSXUSER_ID, m.getUserID());
        rc.put(CompositeDataConstants.BROKER_PATH, Arrays.toString(m.getBrokerPath()));
        rc.put(CompositeDataConstants.ORIGINAL_DESTINATION, toString(m.getOriginalDestination()));
        try {
            rc.put(CompositeDataConstants.PROPERTIES, "" + m.getProperties());
        } catch (IOException e) {
            rc.put(CompositeDataConstants.PROPERTIES, "");
        }

        try {
            rc.put(CompositeDataConstants.STRING_PROPERTIES, createTabularData(m, stringPropertyTabularType, String.class));
        } catch (IOException e) {
            rc.put(CompositeDataConstants.STRING_PROPERTIES, new TabularDataSupport(stringPropertyTabularType));
        }
        try {
            rc.put(CompositeDataConstants.BOOLEAN_PROPERTIES, createTabularData(m, booleanPropertyTabularType, Boolean.class));
        } catch (IOException e) {
            rc.put(CompositeDataConstants.BOOLEAN_PROPERTIES, new TabularDataSupport(booleanPropertyTabularType));
        }
        try {
            rc.put(CompositeDataConstants.BYTE_PROPERTIES, createTabularData(m, bytePropertyTabularType, Byte.class));
        } catch (IOException e) {
            rc.put(CompositeDataConstants.BYTE_PROPERTIES, new TabularDataSupport(bytePropertyTabularType));
        }
        try {
            rc.put(CompositeDataConstants.SHORT_PROPERTIES, createTabularData(m, shortPropertyTabularType, Short.class));
        } catch (IOException e) {
            rc.put(CompositeDataConstants.SHORT_PROPERTIES, new TabularDataSupport(shortPropertyTabularType));
        }
        try {
            rc.put(CompositeDataConstants.INT_PROPERTIES, createTabularData(m, intPropertyTabularType, Integer.class));
        } catch (IOException e) {
            rc.put(CompositeDataConstants.INT_PROPERTIES, new TabularDataSupport(intPropertyTabularType));
        }
        try {
            rc.put(CompositeDataConstants.LONG_PROPERTIES, createTabularData(m, longPropertyTabularType, Long.class));
        } catch (IOException e) {
            rc.put(CompositeDataConstants.LONG_PROPERTIES, new TabularDataSupport(longPropertyTabularType));
        }
        try {
            rc.put(CompositeDataConstants.FLOAT_PROPERTIES, createTabularData(m, floatPropertyTabularType, Float.class));
        } catch (IOException e) {
            rc.put(CompositeDataConstants.FLOAT_PROPERTIES, new TabularDataSupport(floatPropertyTabularType));
        }
        try {
            rc.put(CompositeDataConstants.DOUBLE_PROPERTIES, createTabularData(m, doublePropertyTabularType, Double.class));
        } catch (IOException e) {
            rc.put(CompositeDataConstants.DOUBLE_PROPERTIES, new TabularDataSupport(doublePropertyTabularType));
        }
        return rc;
    }

    protected String toString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }


    protected <T> TabularType createTabularType(Class<T> type, OpenType openType) throws OpenDataException {
        String typeName = "java.util.Map<java.lang.String, " + type.getName() + ">";
        String[] keyValue = new String[]{"key", "value"};
        OpenType[] openTypes = new OpenType[]{SimpleType.STRING, openType};
        CompositeType rowType = new CompositeType(typeName, typeName, keyValue, keyValue, openTypes);
        return new TabularType(typeName, typeName, rowType, new String[]{"key"});
    }

    protected TabularDataSupport createTabularData(ActiveMQMessage m, TabularType type, Class valueType) throws IOException, OpenDataException {
        TabularDataSupport answer = new TabularDataSupport(type);
        Set<Map.Entry<String, Object>> entries = m.getProperties().entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            Object value = entry.getValue();
            if (value instanceof UTF8Buffer && valueType.equals(String.class)) {
                String actual = value.toString();
                CompositeDataSupport compositeData = createTabularRowValue(type, entry.getKey(), actual);
                answer.put(compositeData);
            }
            if (valueType.isInstance(value)) {
                CompositeDataSupport compositeData = createTabularRowValue(type, entry.getKey(), value);
                answer.put(compositeData);
            }
        }
        return answer;
    }

    protected CompositeDataSupport createTabularRowValue(TabularType type, String key, Object value) throws OpenDataException {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("key", key);
        fields.put("value", value);
        return new CompositeDataSupport(type.getRowType(), fields);
    }
}
