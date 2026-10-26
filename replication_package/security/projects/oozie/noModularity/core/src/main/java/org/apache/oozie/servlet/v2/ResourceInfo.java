package org.apache.oozie.servlet.v2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This bean defines a REST resource.
 */
public class ResourceInfo {
    String name;
    boolean wildcard;
    List<String> methods;
    Map<String, ParameterInfo> parameters = new HashMap<String, ParameterInfo>();

    /**
     * Creates a ResourceInfo with a REST resource definition.
     *
     * @param name       name of the REST resource, it can be an fixed resource name, empty or a wildcard ('*').
     * @param methods    HTTP methods supported by the resource.
     * @param parameters parameters supported by the resource.
     */
    public ResourceInfo(String name, List<String> methods, List<ParameterInfo> parameters) {
        this.name = name;
        wildcard = name.equals("*");
        for (ParameterInfo parameter : parameters) {
            this.parameters.put(parameter.name, parameter);
        }
        this.methods = Objects.requireNonNull(methods, "methods cannot be null");
    }
}
