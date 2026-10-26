package org.apache.oozie;

interface BaseEngineCallable<V> {
    V callOrThrow() throws BaseEngineException;
}
