package org.apache.activemq.store.kahadb.disk.page.closure;

import org.apache.activemq.store.kahadb.disk.page.engine.Transaction;

/**
 * This closure interface is intended for the end user implement callbacks for the Transaction.exectue() method.
 *
 * @param <T> The type of exceptions that operation will throw.
 */
public interface Closure<T extends Throwable> {
    public void execute(Transaction tx) throws T;
}
