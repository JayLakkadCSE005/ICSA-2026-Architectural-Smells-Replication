package org.apache.activemq.store.kahadb.disk.page.closure;

import org.apache.activemq.store.kahadb.disk.page.engine.Transaction;

/**
 * This closure interface is intended for the end user implement callbacks for the Transaction.exectue() method.
 *
 * @param <R> The type of result that the closure produces.
 * @param <T> The type of exceptions that operation will throw.
 */
public interface CallableClosure<R, T extends Throwable> {
    public R execute(Transaction tx) throws T;
}
