/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.oozie.handler;

import org.apache.oozie.*;
import org.apache.oozie.client.rest.RestConstants;

public class OozieClientOperationHandler {

    static final String WF = "wf";
    static final String BUNDLE = "bundle";
    static final String COORD = "coord";
    static final String GMT = "GMT";

    BaseEngine engine;

    public OozieClientOperationHandler(BaseEngine engine) {
        this.engine = engine;
    }


    public OozieOperationJob getOperationHandler(String operation, final String filter, final int start, final int len) {
        switch (operation) {
            case RestConstants.JOB_ACTION_KILL:
                return new KillOperation(this, filter, start, len);
            case RestConstants.JOB_ACTION_SUSPEND:
                return new SuspendingOperation(this, filter, start, len);
            case RestConstants.JOB_ACTION_RESUME:
                return new ResumingOperation(this, filter, start, len);
            default:
                throw new IllegalArgumentException(operation);
        }
    }
}
