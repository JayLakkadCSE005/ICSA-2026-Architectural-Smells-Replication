package org.apache.oozie.servlet.v2;

import org.apache.oozie.util.ParamChecker;

import java.util.List;
import java.util.Objects;

/**
 * This bean defines a query string parameter.
 */
public class ParameterInfo {
    String name;
    Class type;
    List<String> methods;
    boolean required;

    /**
     * Creates a ParameterInfo with querystring parameter definition.
     *
     * @param name     querystring parameter name.
     * @param type     type for the parameter value, valid types are: <code>Integer, Boolean and String</code>
     * @param required indicates if the parameter is required.
     * @param methods  HTTP methods the parameter is used by.
     */
    public ParameterInfo(String name, Class type, boolean required, List<String> methods) {
        this.name = ParamChecker.notEmpty(name, "name");
        if (type != Integer.class && type != Boolean.class && type != String.class) {
            throw new IllegalArgumentException("Type must be integer, boolean or string");
        }
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.required = required;
        this.methods = Objects.requireNonNull(methods, "methods cannot be null");
    }

}
