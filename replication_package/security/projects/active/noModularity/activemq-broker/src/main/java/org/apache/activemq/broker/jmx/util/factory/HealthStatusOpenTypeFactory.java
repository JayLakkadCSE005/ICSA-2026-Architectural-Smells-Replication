package org.apache.activemq.broker.jmx.util.factory;

import org.apache.activemq.broker.jmx.core.healthview.health.HealthStatus;

import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.SimpleType;
import java.util.Map;

public class HealthStatusOpenTypeFactory extends AbstractOpenTypeFactory {
    @Override
    protected String getTypeName() {
        return HealthStatus.class.getName();
    }

    @Override
    protected void init() throws OpenDataException {
        super.init();
        addItem("healthId", "health check id", SimpleType.STRING);
        addItem("level", "severity", SimpleType.STRING);
        addItem("message", "severity", SimpleType.STRING);
        addItem("resource", "event resource", SimpleType.STRING);
    }

    @Override
    public Map<String, Object> getFields(Object o) throws OpenDataException {
        HealthStatus event = (HealthStatus) o;
        Map<String, Object> rc = super.getFields(o);
        rc.put("healthId", event.getHealthId());
        rc.put("level", event.getLevel());
        rc.put("message", event.getMessage());
        rc.put("resource", event.getResource());
        return rc;
    }
}
