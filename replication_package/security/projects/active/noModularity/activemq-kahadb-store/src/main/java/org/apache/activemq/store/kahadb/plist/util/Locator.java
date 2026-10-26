package org.apache.activemq.store.kahadb.plist.util;

import org.apache.activemq.store.kahadb.plist.impl.PListImpl;

public class Locator {
    private final PListImpl pList;
    public final String id;

    public Locator(PListImpl pList, String id) {
        this.pList = pList;
        this.id = id;
    }

    public PListImpl plist() {
        return pList;
    }
}
