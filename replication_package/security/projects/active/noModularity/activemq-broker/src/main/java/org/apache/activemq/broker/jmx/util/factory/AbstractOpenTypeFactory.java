package org.apache.activemq.broker.jmx.util.factory;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractOpenTypeFactory implements OpenTypeFactory {

    private CompositeType compositeType;
    private final List<String> itemNamesList = new ArrayList<String>();
    private final List<String> itemDescriptionsList = new ArrayList<String>();
    private final List<OpenType> itemTypesList = new ArrayList<OpenType>();

    public synchronized CompositeType getCompositeType() throws OpenDataException {
        if (compositeType == null) {
            init();
            compositeType = createCompositeType();
        }
        return compositeType;
    }

    protected void init() throws OpenDataException {
    }

    protected CompositeType createCompositeType() throws OpenDataException {
        String[] itemNames = itemNamesList.toArray(new String[itemNamesList.size()]);
        String[] itemDescriptions = itemDescriptionsList.toArray(new String[itemDescriptionsList.size()]);
        OpenType[] itemTypes = itemTypesList.toArray(new OpenType[itemTypesList.size()]);
        return new CompositeType(getTypeName(), getDescription(), itemNames, itemDescriptions, itemTypes);
    }

    protected abstract String getTypeName();

    protected void addItem(String name, String description, OpenType type) {
        itemNamesList.add(name);
        itemDescriptionsList.add(description);
        itemTypesList.add(type);
    }

    protected String getDescription() {
        return getTypeName();
    }

    public Map<String, Object> getFields(Object o) throws OpenDataException {
        Map<String, Object> rc = new HashMap<String, Object>();
        return rc;
    }
}
