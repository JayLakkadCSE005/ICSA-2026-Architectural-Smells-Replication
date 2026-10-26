package org.apache.oozie.dependency.handler;

/**
 * Type of the dependency. PULL dependencies are those whose availability is determined by
 * polling and PUSH dependencies are those whose availability is known from notifications
 */
public enum DependencyType {
    PULL,
    PUSH;
}
