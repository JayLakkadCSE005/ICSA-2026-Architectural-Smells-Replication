package org.apache.activemq.filter.function;

/**
 * Maintain a single function registration.
 */

public class functionRegistration {
    protected FilterFunction filterFunction;

    /**
     * Constructs a function registration for the given function implementation.
     */

    public functionRegistration(FilterFunction func) {
        this.filterFunction = func;
    }


    /**
     * Retrieve the filter function implementation.
     */

    public FilterFunction getFilterFunction() {
        return filterFunction;
    }


    /**
     * Set the filter function implementation for this registration.
     */

    public void setFilterFunction(FilterFunction func) {
        filterFunction = func;
    }
}
