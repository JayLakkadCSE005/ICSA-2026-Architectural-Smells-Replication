/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.security;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Set;

import org.apache.activemq.command.ActiveMQDestination;

/**
 * 
 */
public interface AuthorizationMap {

    /**
     * Returns the set of all ACLs capable of administering temp destination
     */
    Set<?> getTempDestinationAdminACLs();

    /**
     * Returns the set of all ACLs capable of reading from temp destination
     */
    Set<?> getTempDestinationReadACLs();

    /**
     * Returns the set of all ACLs capable of writing to temp destination
     */
    Set<?> getTempDestinationWriteACLs();

    /**
     * Returns the set of all ACLs capable of administering the given
     * destination
     */
    Set<?> getAdminACLs(ActiveMQDestination destination);

    /**
     * Returns the set of all ACLs capable of reading (consuming from) the given
     * destination
     */
    Set<?> getReadACLs(ActiveMQDestination destination);

    /**
     * Returns the set of all ACLs capable of writing to the given destination
     */
    Set<?> getWriteACLs(ActiveMQDestination destination);
    final String WILDCARD = "*";
    public static final String DEFAULT_GROUP_CLASS = "org.apache.activemq.jaas.GroupPrincipal";
    public static Object createGroupPrincipal(String name, String groupClass) throws Exception {
        if (WILDCARD.equals(name)) {
            // simple match all group principal - match any name and class
            return new Principal() {
                @Override
                public String getName() {
                    return WILDCARD;
                }
                @Override
                public boolean equals(Object other) {
                    return true;
                }

                @Override
                public int hashCode() {
                    return WILDCARD.hashCode();
                }
            };
        }
        Object[] param = new Object[]{name};

        Class<?> cls = Class.forName(groupClass);

        Constructor<?>[] constructors = cls.getConstructors();
        int i;
        Object instance;
        for (i = 0; i < constructors.length; i++) {
            Class<?>[] paramTypes = constructors[i].getParameterTypes();
            if (paramTypes.length == 1 && paramTypes[0].equals(String.class)) {
                break;
            }
        }
        if (i < constructors.length) {
            instance = constructors[i].newInstance(param);
        } else {
            instance = cls.getConstructor().newInstance();
            Method[] methods = cls.getMethods();
            i = 0;
            for (i = 0; i < methods.length; i++) {
                Class<?>[] paramTypes = methods[i].getParameterTypes();
                if (paramTypes.length == 1 && methods[i].getName().equals("setName") && paramTypes[0].equals(String.class)) {
                    break;
                }
            }

            if (i < methods.length) {
                methods[i].invoke(instance, param);
            } else {
                throw new NoSuchMethodException();
            }
        }

        return instance;
    }

}
