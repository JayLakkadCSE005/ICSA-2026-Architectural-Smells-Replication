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

package org.apache.oozie.servlet.v0;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;
import org.apache.oozie.BaseEngineException;
import org.apache.oozie.DagEngine;
import org.apache.oozie.DagEngineException;
import org.apache.oozie.client.rest.JsonBean;
import org.apache.oozie.service.DagEngineService;
import org.apache.oozie.service.Services;
import org.apache.oozie.servlet.base.BaseJobServlet;
import org.apache.oozie.servlet.base.XServletException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.apache.oozie.ErrorCode;

@SuppressWarnings("serial")
public class V0JobServlet extends BaseJobServlet {

    private static final String INSTRUMENTATION_NAME = "v0job";

    final static String NOT_SUPPORTED_MESSAGE = "Not supported in v0";

    public V0JobServlet() {
        super(INSTRUMENTATION_NAME);
    }

    /*
     * v0 service method to start a job
     */
    @Override
    public void startJob(HttpServletRequest request, HttpServletResponse response) throws XServletException,
            IOException {
        DagEngine dagEngine = Services.get().get(DagEngineService.class).getDagEngine(getUser(request));

        String jobId = getResourceName(request);
        try {
            dagEngine.start(jobId);
        }
        catch (DagEngineException ex) {
            throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ex);
        }
    }

    /*
     * v0 service method to resume a job
     */
    @Override
    public void resumeJob(HttpServletRequest request, HttpServletResponse response) throws XServletException,
            IOException {
        DagEngine dagEngine = Services.get().get(DagEngineService.class).getDagEngine(getUser(request));

        String jobId = getResourceName(request);
        try {
            dagEngine.resume(jobId);
        }
        catch (DagEngineException ex) {
            throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ex);
        }
    }

    /*
     * v0 service method to suspend a job
     */
    @Override
    public void suspendJob(HttpServletRequest request, HttpServletResponse response) throws XServletException,
            IOException {
        DagEngine dagEngine = Services.get().get(DagEngineService.class).getDagEngine(getUser(request));

        String jobId = getResourceName(request);
        try {
            dagEngine.suspend(jobId);
        }
        catch (DagEngineException ex) {
            throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ex);
        }
    }

    /*
     * v0 service method to kill a job
     */
    @Override
    public JSONObject killJob(HttpServletRequest request, HttpServletResponse response) throws XServletException,
            IOException {
        DagEngine dagEngine = Services.get().get(DagEngineService.class).getDagEngine(getUser(request));

        String jobId = getResourceName(request);
        try {
            dagEngine.kill(jobId);
        }
        catch (DagEngineException ex) {
            throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ex);
        }
        return null;
    }

    /*
     * v0 service method to change a job
     */
    public void changeJob(HttpServletRequest request, HttpServletResponse response) throws XServletException,
            IOException {
        // This code should not be reached. But if it happens somehow, we throw
        // bad request exception.
        throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ErrorCode.E1014);
    }

    /*
     * v0 service method to reRun a job
     */
    @Override
    public JSONObject reRunJob(HttpServletRequest request, HttpServletResponse response, Configuration conf)
            throws XServletException, IOException {
        DagEngine dagEngine = Services.get().get(DagEngineService.class).getDagEngine(getUser(request));

        String jobId = getResourceName(request);
        try {
            dagEngine.reRun(jobId, conf);
        }
        catch (DagEngineException ex) {
            throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ex);
        }
        return null;
    }

    /*
     * v0 service method to get a job in JsonBean representation
     */
    @Override
    public JsonBean getJob(HttpServletRequest request, HttpServletResponse response) throws XServletException,
            IOException {
        DagEngine dagEngine = Services.get().get(DagEngineService.class).getDagEngine(getUser(request));

        JsonBean jobBean = null;
        String jobId = getResourceName(request);
        try {
            jobBean = (JsonBean) dagEngine.getJob(jobId);
        }
        catch (DagEngineException ex) {
            throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ex);
        }

        return jobBean;
    }

    /*
     * v0 service method to get a job definition in String format
     */
    @Override
    public String getJobDefinition(HttpServletRequest request, HttpServletResponse response)
            throws XServletException, IOException {
        DagEngine dagEngine = Services.get().get(DagEngineService.class).getDagEngine(getUser(request));

        String wfDefinition = null;
        String jobId = getResourceName(request);
        try {
            wfDefinition = dagEngine.getDefinition(jobId);
        }
        catch (DagEngineException ex) {
            throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ex);
        }
        return wfDefinition;
    }

    /*
     * v0 service method to stream a job log into response object
     */
    @Override
    public void streamJobLog(HttpServletRequest request, HttpServletResponse response) throws XServletException,
            IOException {
        DagEngine dagEngine = Services.get().get(DagEngineService.class).getDagEngine(getUser(request));

        String jobId = getResourceName(request);
        try {
            dagEngine.streamLog(jobId, response.getWriter(), request.getParameterMap());
        }
        catch (BaseEngineException ex) {
            throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ex);
        }
    }

    @Override
    public void streamJobErrorLog(HttpServletRequest request, HttpServletResponse response) throws XServletException,
            IOException {
        throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ErrorCode.E0306);
    }

    @Override
    public void streamJobAuditLog(HttpServletRequest request, HttpServletResponse response) throws XServletException,
            IOException {
        throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ErrorCode.E0306);
    }

    /*
     * Not implemented in v0
     */
    @Override
    public void streamJobGraph(HttpServletRequest request, HttpServletResponse response)
            throws XServletException, IOException {
        // Should this error code be NOT_IMPLEMENTED?
        throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ErrorCode.E0306);
    }

    @Override
    public String getJMSTopicName(HttpServletRequest request, HttpServletResponse response) throws XServletException,
            IOException {
        throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ErrorCode.E0306);
    }

    @Override
    public JSONObject getJobsByParentId(HttpServletRequest request, HttpServletResponse response) throws XServletException,
            IOException {
        throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ErrorCode.E0302, NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public JSONObject updateJob(HttpServletRequest request, HttpServletResponse response, Configuration conf)
            throws XServletException, IOException {
        throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ErrorCode.E0302, NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public JSONObject ignoreJob(HttpServletRequest request, HttpServletResponse response) throws XServletException, IOException {
        throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ErrorCode.E0302, NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public String getJobStatus(HttpServletRequest request, HttpServletResponse response) throws XServletException,
            IOException {
        throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ErrorCode.E0302, NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void slaEnableAlert(HttpServletRequest request, HttpServletResponse response) throws XServletException, IOException {
        throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ErrorCode.E0302, NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void slaDisableAlert(HttpServletRequest request, HttpServletResponse response) throws XServletException,
            IOException {
        throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ErrorCode.E0302, NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public void slaChange(HttpServletRequest request, HttpServletResponse response) throws XServletException, IOException {
        throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ErrorCode.E0302, NOT_SUPPORTED_MESSAGE);
    }

    @Override
   public JSONArray getActionRetries(HttpServletRequest request, HttpServletResponse response) throws XServletException,
            IOException {
        throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ErrorCode.E0302, NOT_SUPPORTED_MESSAGE);
    }

    @Override
    public JSONObject getCoordActionMissingDependencies(HttpServletRequest request, HttpServletResponse response)
            throws XServletException, IOException {
        throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, ErrorCode.E0302, NOT_SUPPORTED_MESSAGE);

    }

}
