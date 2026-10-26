package org.apache.oozie.command.purge;

import org.apache.oozie.executor.jpa.JPAExecutorException;

public interface JPAFunction<T, R> {
    R apply(T t) throws JPAExecutorException;
}
