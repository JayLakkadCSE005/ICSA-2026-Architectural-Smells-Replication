package org.apache.activemq.broker.jmx.util.factory;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import java.util.Map;

public interface OpenTypeFactory {
    CompositeType getCompositeType() throws OpenDataException;

    Map<String, Object> getFields(Object o) throws OpenDataException;
}
