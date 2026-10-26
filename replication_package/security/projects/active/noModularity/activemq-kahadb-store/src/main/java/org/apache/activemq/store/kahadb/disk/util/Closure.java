package org.apache.activemq.store.kahadb.disk.util;

public interface Closure<T extends Throwable> {
    public void execute(long value) throws T;
}
