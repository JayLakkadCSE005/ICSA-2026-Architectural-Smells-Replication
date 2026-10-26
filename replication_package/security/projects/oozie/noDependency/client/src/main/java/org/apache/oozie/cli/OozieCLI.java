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

package org.apache.oozie.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.oozie.BuildInfo;
import org.apache.oozie.fluentjob.api.workflow.ApiJarLoader;
import org.apache.oozie.client.AuthOozieClient;
import org.apache.oozie.client.AuthOozieClient.AuthType;
import org.apache.oozie.client.BulkResponse;
import org.apache.oozie.client.BundleJob;
import org.apache.oozie.client.CoordinatorAction;
import org.apache.oozie.client.CoordinatorJob;
import org.apache.oozie.OozieClient;
import org.apache.oozie.OozieClient.SYSTEM_MODE;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.client.WorkflowAction;
import org.apache.oozie.client.WorkflowJob;
import org.apache.oozie.client.rest.JsonTags;
import org.apache.oozie.client.rest.JsonToBean;
import org.apache.oozie.client.rest.RestConstants;
import org.apache.oozie.fluentjob.api.serialization.WorkflowMarshaller;
import org.apache.oozie.fluentjob.api.workflow.Workflow;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.google.common.annotations.VisibleForTesting;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Oozie command line utility.
 */
public class OozieCLI {
    private static final String[] OOZIE_HELP = {
            "the env variable '" + OozieClientConstant.ENV_OOZIE_URL + "' is used as default value for the '-" + OozieClientConstant.OOZIE_OPTION + "' option",
            "the env variable '" + OozieClientConstant.ENV_OOZIE_TIME_ZONE + "' is used as default value for the '-" + OozieClientConstant.TIME_ZONE_OPTION + "' option",
            "the env variable '" + OozieClientConstant.ENV_OOZIE_AUTH + "' is used as default value for the '-" + OozieClientConstant.AUTH_OPTION + "' option",
            "custom headers for Oozie web services can be specified using '-D" + OozieClientConstant.WS_HEADER_PREFIX + "NAME=VALUE'" };

    private static final String RULER;
    private static final int LINE_WIDTH = 132;

    private static final int RETRY_COUNT = 4;

    private boolean used;

    private static final String INSTANCE_SEPARATOR = "#";

    private static final String MAPRED_MAPPER = "mapred.mapper.class";
    private static final String MAPRED_MAPPER_2 = "mapreduce.map.class";
    private static final String MAPRED_REDUCER = "mapred.reducer.class";
    private static final String MAPRED_REDUCER_2 = "mapreduce.reduce.class";
    private static final String MAPRED_INPUT = "mapred.input.dir";
    private static final String MAPRED_OUTPUT = "mapred.output.dir";

    private static final Pattern GMT_OFFSET_SHORTEN_PATTERN = Pattern.compile("(.* )GMT((?:-|\\+)\\d{2}:\\d{2})");

    static {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LINE_WIDTH; i++) {
            sb.append("-");
        }
        RULER = sb.toString();
    }

    /**
     * Entry point for the Oozie CLI when invoked from the command line.
     * <p>
     * Upon completion this method exits the JVM with '0' (success) or '-1' (failure).
     *
     * @param args options and arguments for the Oozie CLI.
     */
    public static void main(String[] args) {
        if (!System.getProperties().containsKey(AuthOozieClient.USE_AUTH_TOKEN_CACHE_SYS_PROP)) {
            System.setProperty(AuthOozieClient.USE_AUTH_TOKEN_CACHE_SYS_PROP, "true");
        }
        System.exit(new OozieCLI().run(args));
    }

    /**
     * Create an Oozie CLI instance.
     */
    public OozieCLI() {
        used = false;
    }

    /**
     * Return Oozie CLI top help lines.
     *
     * @return help lines.
     */
    protected String[] getCLIHelp() {
        return OOZIE_HELP;
    }

    /**
     * Add authentication specific options to oozie cli
     *
     * @param options the collection of options to add auth options
     */
    protected void addAuthOptions(Options options) {
        Option auth = new Option(OozieClientConstant.AUTH_OPTION, true, "select authentication type [SIMPLE|BASIC|KERBEROS]");
        options.addOption(auth);
        options.addOption(new Option(OozieClientConstant.USERNAME, true, "username for BASIC authentication"));
        options.addOption(new Option(OozieClientConstant.PASSWORD, true, "password for BASIC authentication"));
    }

    /**
     * Create option for command line option 'admin'
     * @return admin options
     */
    protected Options createAdminOptions() {
        Option oozie = new Option(OozieClientConstant.OOZIE_OPTION, true, "Oozie URL");
        Option system_mode = new Option(OozieClientConstant.SYSTEM_MODE_OPTION, true,
                "Supported in Oozie-2.0 or later versions ONLY. Change oozie system mode [NORMAL|NOWEBSERVICE|SAFEMODE]");
        Option status = new Option(OozieClientConstant.STATUS_OPTION, false, "show the current system status");
        Option version = new Option(OozieClientConstant.VERSION_OPTION, false, "show Oozie server build version");
        Option queuedump = new Option(OozieClientConstant.QUEUE_DUMP_OPTION, false, "show Oozie server queue elements");
        Option doAs = new Option(OozieClientConstant.DO_AS_OPTION, true, "doAs user, impersonates as the specified user");
        Option availServers = new Option(OozieClientConstant.AVAILABLE_SERVERS_OPTION, false, "list available Oozie servers"
                + " (more than one only if HA is enabled)");
        Option sharelibUpdate = new Option(OozieClientConstant.UPDATE_SHARELIB_OPTION, false, "Update server to use a newer version of sharelib");
        Option serverConfiguration = new Option(OozieClientConstant.SERVER_CONFIGURATION_OPTION, false, "show Oozie system configuration");
        Option osEnv = new Option(OozieClientConstant.SERVER_OS_ENV_OPTION, false, "show Oozie system OS environment");
        Option javaSysProps = new Option(OozieClientConstant.SERVER_JAVA_SYSTEM_PROPERTIES_OPTION, false, "show Oozie Java system properties");
        Option metrics = new Option(OozieClientConstant.METRICS_OPTION, false, "show Oozie system metrics");
        Option instrumentation = new Option(OozieClientConstant.INSTRUMENTATION_OPTION, false, "show Oozie system instrumentation");

        Option sharelib = new Option(OozieClientConstant.LIST_SHARELIB_LIB_OPTION, false,
                "List available sharelib that can be specified in a workflow action");
        sharelib.setOptionalArg(true);
        Option purge = new Option(OozieClientConstant.PURGE_OPTION, true, "purge old oozie workflow, coordinator and bundle records from DB " +
                "(parameter unit: day)");
        Option insecureAccess = new Option(OozieClientConstant.INSECURE_ACCESS_OPTION, false, "This option will allow SSL connections " +
                "even though there's a problem with the certificate. The connection will still be encrypted, " +
                "but Oozie client won't validate the server certificate.");

        Options adminOptions = new Options();
        adminOptions.addOption(oozie);
        adminOptions.addOption(doAs);
        adminOptions.addOption(insecureAccess);
        OptionGroup group = new OptionGroup();
        group.addOption(system_mode);
        group.addOption(status);
        group.addOption(version);
        group.addOption(queuedump);
        group.addOption(availServers);
        group.addOption(sharelibUpdate);
        group.addOption(sharelib);
        group.addOption(serverConfiguration);
        group.addOption(osEnv);
        group.addOption(javaSysProps);
        group.addOption(metrics);
        group.addOption(instrumentation);
        group.addOption(purge);
        adminOptions.addOptionGroup(group);
        addAuthOptions(adminOptions);
        return adminOptions;
    }

    /**
     * Create option for command line option 'job'
     * @return job options
     */
    protected Options createJobOptions() {
        Option oozie = new Option(OozieClientConstant.OOZIE_OPTION, true, "Oozie URL");
        Option config = new Option(OozieClientConstant.CONFIG_OPTION, true, "job configuration file '.xml' or '.properties'");
        Option submit = new Option(OozieClientConstant.SUBMIT_OPTION, false, "submit a job");
        Option run = new Option(OozieClientConstant.RUN_OPTION, false, "run a job");
        Option debug = new Option(OozieClientConstant.DEBUG_OPTION, false, "Use debug mode to see debugging statements on stdout");
        Option rerun = new Option(OozieClientConstant.RERUN_OPTION, true,
                "rerun a job  (coordinator requires -action or -date, bundle requires -coordinator or -date)");
        Option dryrun = new Option(OozieClientConstant.DRYRUN_OPTION, false, "Dryrun a workflow (since 3.3.2), a coordinator (since 2.0) "
                + " or a bundle (since 5.1) job without actually executing it");
        Option update = new Option(OozieClientConstant.UPDATE_OPTION, true, "Update coord definition and properties");
        Option showdiff = new Option(OozieClientConstant.SHOWDIFF_OPTION, true,
                "Show diff of the new coord definition and properties with the existing one (default true)");
        Option start = new Option(OozieClientConstant.START_OPTION, true, "start a job");
        Option suspend = new Option(OozieClientConstant.SUSPEND_OPTION, true, "suspend a job");
        Option resume = new Option(OozieClientConstant.RESUME_OPTION, true, "resume a job");
        Option kill = new Option(OozieClientConstant.KILL_OPTION, true, "kill a job (coordinator can mention -action or -date)");
        Option change = new Option(OozieClientConstant.CHANGE_OPTION, true, "change a coordinator or bundle job");
        Option changeValue = new Option(OozieClientConstant.CHANGE_VALUE_OPTION, true,
                "new endtime/concurrency/pausetime value for changing a coordinator job");
        Option info = new Option(OozieClientConstant.INFO_OPTION, true, "info of a job");
        Option poll = new Option(OozieClientConstant.POLL_OPTION, true, "poll Oozie until a job reaches a terminal state or a timeout occurs");
        Option offset = new Option(OozieClientConstant.OFFSET_OPTION, true, "job info offset of actions (default '1', requires -info)");
        Option len = new Option(OozieClientConstant.LEN_OPTION, true, "number of actions (default TOTAL ACTIONS, requires -info)");
        Option filter = new Option(OozieClientConstant.FILTER_OPTION, true,
                "<key><comparator><value>[;<key><comparator><value>]*\n"
                    + "(All Coordinator actions satisfying the filters will be retrieved).\n"
                    + "key: status or nominaltime\n"
                    + "comparator: =, !=, <, <=, >, >=. = is used as OR and others as AND\n"
                    + "status: values are valid status like SUCCEEDED, KILLED etc. Only = and != apply for status\n"
                    + "nominaltime: time of format yyyy-MM-dd'T'HH:mm'Z'");
        Option order = new Option(OozieClientConstant.ORDER_OPTION, true,
                "order to show coord actions (default ascending order, 'desc' for descending order, requires -info)");
        Option localtime = new Option(OozieClientConstant.LOCAL_TIME_OPTION, false, "use local time (same as passing your time zone to -" +
                OozieClientConstant.TIME_ZONE_OPTION + "). Overrides -" + OozieClientConstant.TIME_ZONE_OPTION + " option");
        Option timezone = new Option(OozieClientConstant.TIME_ZONE_OPTION, true,
                "use time zone with the specified ID (default GMT).\nSee 'oozie info -timezones' for a list");
        Option log = new Option(OozieClientConstant.LOG_OPTION, true, "job log");
        Option errorlog = new Option(OozieClientConstant.ERROR_LOG_OPTION, true, "job error log");
        Option auditlog = new Option(OozieClientConstant.AUDIT_LOG_OPTION, true, "job audit log");
        final Option generateAndCheck = new Option(OozieClientConstant.VALIDATE_JAR_OPTION, true, "generate and check job definition");
        final Option generateAndSubmit = new Option(OozieClientConstant.SUBMIT_JAR_OPTION, true, "generate and submit job definition");
        final Option generateAndRun = new Option(OozieClientConstant.RUN_JAR_OPTION, true, "generate and run job definition");
        Option logFilter = new Option(
                RestConstants.LOG_FILTER_OPTION, true,
                "job log search parameter. Can be specified as -logfilter opt1=val1;opt2=val1;opt3=val1. "
                + "Supported options are recent, start, end, loglevel, text, limit and debug");
        Option definition = new Option(OozieClientConstant.DEFINITION_OPTION, true, "job definition");
        Option config_content = new Option(OozieClientConstant.CONFIG_CONTENT_OPTION, true, "job configuration");
        Option verbose = new Option(OozieClientConstant.VERBOSE_OPTION, false, "verbose mode");
        Option action = new Option(OozieClientConstant.ACTION_OPTION, true,
                "coordinator rerun/kill on action ids (requires -rerun/-kill); coordinator log retrieval on action ids"
                        + " (requires -log)");
        Option date = new Option(OozieClientConstant.DATE_OPTION, true,
                "coordinator/bundle rerun on action dates (requires -rerun); "
                + "coordinator log retrieval on action dates (requires -log)");
        Option rerun_coord = new Option(OozieClientConstant.COORD_OPTION, true, "bundle rerun on coordinator names (requires -rerun)");
        Option rerun_refresh = new Option(OozieClientConstant.RERUN_REFRESH_OPTION, false,
                "re-materialize the coordinator rerun actions (requires -rerun)");
        Option rerun_nocleanup = new Option(OozieClientConstant.RERUN_NOCLEANUP_OPTION, false,
                "do not clean up output-events of the coordinator rerun actions (requires -rerun)");
        Option rerun_failed = new Option(OozieClientConstant.RERUN_FAILED_OPTION, false,
                "runs the failed workflow actions of the coordinator actions (requires -rerun)");
        Option property = OptionBuilder.withArgName("property=value").hasArgs(2).withValueSeparator().withDescription(
                "set/override value for given property").create("D");
        Option getAllWorkflows = new Option(OozieClientConstant.ALL_WORKFLOWS_FOR_COORD_ACTION, false,
                "Get workflow jobs corresponding to a coordinator action including all the reruns");
        Option ignore = new Option(OozieClientConstant.IGNORE_OPTION, true,
                "change status of a coordinator job or action to IGNORED"
                + " (-action required to ignore coord actions)");
        Option timeout = new Option(OozieClientConstant.TIMEOUT_OPTION, true, "timeout in minutes (default is 30, negative values indicate no "
                + "timeout, requires -poll)");
        timeout.setType(Integer.class);
        Option interval = new Option(OozieClientConstant.INTERVAL_OPTION, true, "polling interval in minutes (default is 5, requires -poll)");
        interval.setType(Integer.class);

        Option slaDisableAlert = new Option(OozieClientConstant.SLA_DISABLE_ALERT, true,
                "disables sla alerts for the job and its children");
        Option slaEnableAlert = new Option(OozieClientConstant.SLA_ENABLE_ALERT, true,
                "enables sla alerts for the job and its children");
        Option slaChange = new Option(OozieClientConstant.SLA_CHANGE, true,
                "Update sla param for jobs, supported param are should-start, should-end, nominal-time and max-duration");
        Option coordActionMissingDependencies = new Option(OozieClientConstant.COORD_ACTION_MISSING_DEPENDENCIES, true,
                "List missing dependencies of a coord action. To specify multiple actions, use with -action or -date option.");


        Option doAs = new Option(OozieClientConstant.DO_AS_OPTION, true, "doAs user, impersonates as the specified user");

        Option workflowActionRetries = new Option(OozieClientConstant.WORKFLOW_ACTIONS_RETRIES, true,
                "Get information of the retry attempts for a given workflow action");
        Option insecureAccess = new Option(OozieClientConstant.INSECURE_ACCESS_OPTION, false, "This option will allow SSL connections " +
                "even though there's a problem with the certificate. The connection will still be encrypted, " +
                "but Oozie client won't validate the server certificate.");

        OptionGroup actions = new OptionGroup();
        actions.addOption(submit);
        actions.addOption(start);
        actions.addOption(run);
        actions.addOption(dryrun);
        actions.addOption(suspend);
        actions.addOption(resume);
        actions.addOption(kill);
        actions.addOption(change);
        actions.addOption(update);
        actions.addOption(info);
        actions.addOption(rerun);
        actions.addOption(log);
        actions.addOption(errorlog);
        actions.addOption(auditlog);
        actions.addOption(generateAndCheck);
        actions.addOption(generateAndSubmit);
        actions.addOption(generateAndRun);
        actions.addOption(definition);
        actions.addOption(config_content);
        actions.addOption(ignore);
        actions.addOption(poll);
        actions.addOption(slaDisableAlert);
        actions.addOption(slaEnableAlert);
        actions.addOption(slaChange);
        actions.addOption(workflowActionRetries);
        actions.addOption(coordActionMissingDependencies);
        actions.setRequired(true);
        Options jobOptions = new Options();
        jobOptions.addOption(oozie);
        jobOptions.addOption(doAs);
        jobOptions.addOption(config);
        jobOptions.addOption(property);
        jobOptions.addOption(changeValue);
        jobOptions.addOption(localtime);
        jobOptions.addOption(timezone);
        jobOptions.addOption(verbose);
        jobOptions.addOption(debug);
        jobOptions.addOption(offset);
        jobOptions.addOption(len);
        jobOptions.addOption(filter);
        jobOptions.addOption(order);
        jobOptions.addOption(action);
        jobOptions.addOption(date);
        jobOptions.addOption(rerun_coord);
        jobOptions.addOption(rerun_refresh);
        jobOptions.addOption(rerun_nocleanup);
        jobOptions.addOption(rerun_failed);
        jobOptions.addOption(getAllWorkflows);
        jobOptions.addOptionGroup(actions);
        jobOptions.addOption(logFilter);
        jobOptions.addOption(timeout);
        jobOptions.addOption(interval);
        jobOptions.addOption(insecureAccess);
        addAuthOptions(jobOptions);
        jobOptions.addOption(showdiff);

        //Needed to make dryrun and update mutually exclusive options
        OptionGroup updateOption = new OptionGroup();
        updateOption.addOption(dryrun);
        jobOptions.addOptionGroup(updateOption);

        return jobOptions;
    }

    /**
     * Create option for command line option 'jobs'
     * @return jobs options
     */
    protected Options createJobsOptions() {
        Option oozie = new Option(OozieClientConstant.OOZIE_OPTION, true, "Oozie URL");
        Option start = new Option(OozieClientConstant.OFFSET_OPTION, true, "jobs offset (default '1')");
        Option jobtype = new Option(OozieClientConstant.JOBTYPE_OPTION, true,
                "job type ('Supported in Oozie-2.0 or later versions ONLY - 'coordinator' or 'bundle' or 'wf'(default))");
        Option len = new Option(OozieClientConstant.LEN_OPTION, true, "number of jobs (default '100')");
        Option filter = new Option(OozieClientConstant.FILTER_OPTION, true,
                "text=<*>\\;user=<U>\\;name=<N>\\;group=<G>\\;status=<S>\\;frequency=<F>\\;unit=<M>" +
                        "\\;startcreatedtime=<SC>\\;endcreatedtime=<EC> \\;sortBy=<SB>\n" +
                        "(text filter: matches partially with name and user or complete match with job ID. " +
                        "Valid unit values are 'months', 'days', 'hours' or 'minutes'. " +
                        "startcreatedtime, endcreatedtime: time of format yyyy-MM-dd'T'HH:mm'Z'. " +
                        "Valid values for sortBy are 'createdTime' or 'lastModifiedTime'.)");
        Option localtime = new Option(OozieClientConstant.LOCAL_TIME_OPTION, false, "use local time (same as passing your time zone to -" +
                OozieClientConstant.TIME_ZONE_OPTION + "). Overrides -" + OozieClientConstant.TIME_ZONE_OPTION + " option");
        Option kill = new Option(OozieClientConstant.KILL_OPTION, false, "bulk kill operation");
        Option suspend = new Option(OozieClientConstant.SUSPEND_OPTION, false, "bulk suspend operation");
        Option resume = new Option(OozieClientConstant.RESUME_OPTION, false, "bulk resume operation");
        Option timezone = new Option(OozieClientConstant.TIME_ZONE_OPTION, true,
                "use time zone with the specified ID (default GMT).\nSee 'oozie info -timezones' for a list");
        Option verbose = new Option(OozieClientConstant.VERBOSE_OPTION, false, "verbose mode");
        Option doAs = new Option(OozieClientConstant.DO_AS_OPTION, true, "doAs user, impersonates as the specified user");
        Option bulkMonitor = new Option(OozieClientConstant.BULK_OPTION, true, "key-value pairs to filter bulk jobs response. e.g. bundle=<B>\\;" +
                "coordinators=<C>\\;actionstatus=<S>\\;startcreatedtime=<SC>\\;endcreatedtime=<EC>\\;" +
                "startscheduledtime=<SS>\\;endscheduledtime=<ES>\\; bundle, " +
                "coordinators and actionstatus can be multiple comma separated values. " +
                "Bundle and coordinators can be id(s) or appName(s) of those jobs. " +
                "Specifying bundle is mandatory, other params are optional");
        Option insecureAccess = new Option(OozieClientConstant.INSECURE_ACCESS_OPTION, false, "This option will allow SSL connections " +
                "even though there's a problem with the certificate. The connection will still be encrypted, " +
                "but Oozie client won't validate the server certificate.");

        start.setType(Integer.class);
        len.setType(Integer.class);
        Options jobsOptions = new Options();
        jobsOptions.addOption(oozie);
        jobsOptions.addOption(doAs);
        jobsOptions.addOption(localtime);
        jobsOptions.addOption(kill);
        jobsOptions.addOption(suspend);
        jobsOptions.addOption(resume);
        jobsOptions.addOption(timezone);
        jobsOptions.addOption(start);
        jobsOptions.addOption(len);
        jobsOptions.addOption(oozie);
        jobsOptions.addOption(filter);
        jobsOptions.addOption(jobtype);
        jobsOptions.addOption(verbose);
        jobsOptions.addOption(bulkMonitor);
        jobsOptions.addOption(insecureAccess);
        addAuthOptions(jobsOptions);
        return jobsOptions;
    }

    /**
     * Create option for command line option 'sla'
     *
     * @return sla options
     */
    protected Options createSlaOptions() {
        Option oozie = new Option(OozieClientConstant.OOZIE_OPTION, true, "Oozie URL");
        Option start = new Option(OozieClientConstant.OFFSET_OPTION, true, "start offset (default '0')");
        Option len = new Option(OozieClientConstant.LEN_OPTION, true, "number of results (default '100', max '1000')");
        Option filter = new Option(OozieClientConstant.FILTER_OPTION, true, "filter of SLA events. e.g., jobid=<J>\\;appname=<A>");
        Option insecureAccess = new Option(OozieClientConstant.INSECURE_ACCESS_OPTION, false, "This option will allow SSL connections " +
                "even though there's a problem with the certificate. The connection will still be encrypted, " +
                "but Oozie client won't validate the server certificate.");

        start.setType(Integer.class);
        len.setType(Integer.class);
        Options slaOptions = new Options();
        slaOptions.addOption(start);
        slaOptions.addOption(len);
        slaOptions.addOption(filter);
        slaOptions.addOption(oozie);
        slaOptions.addOption(insecureAccess);
        addAuthOptions(slaOptions);
        return slaOptions;
    }

    /**
     * Create option for command line option 'validate'
     *
     * @return validate options
     */
    protected Options createValidateOptions() {
        Option oozie = new Option(OozieClientConstant.OOZIE_OPTION, true, "Oozie URL");
        Option insecureAccess = new Option(OozieClientConstant.INSECURE_ACCESS_OPTION, false, "This option will allow SSL connections " +
                "even though there's a problem with the certificate. The connection will still be encrypted, " +
                "but Oozie client won't validate the server certificate.");
        Options validateOption = new Options();
        validateOption.addOption(oozie);
        validateOption.addOption(insecureAccess);
        addAuthOptions(validateOption);
        return validateOption;
    }

    /**
     * Create option for command line option 'pig' or 'hive'
     * @param jobType type of job - pig or hive
     * @return pig or hive options
     */
    @SuppressWarnings("static-access")
    protected Options createScriptLanguageOptions(String jobType) {
        Option oozie = new Option(OozieClientConstant.OOZIE_OPTION, true, "Oozie URL");
        Option config = new Option(OozieClientConstant.CONFIG_OPTION, true, "job configuration file '.properties'");
        Option file = new Option(OozieClientConstant.SCRIPTFILE_OPTION, true, jobType + " script");
        Option property = OptionBuilder.withArgName("property=value").hasArgs(2).withValueSeparator().withDescription(
                "set/override value for given property").create("D");
        Option params = OptionBuilder.withArgName("property=value").hasArgs(2).withValueSeparator().withDescription(
                "set parameters for script").create("P");
        Option doAs = new Option(OozieClientConstant.DO_AS_OPTION, true, "doAs user, impersonates as the specified user");
        Option insecureAccess = new Option(OozieClientConstant.INSECURE_ACCESS_OPTION, false, "This option will allow SSL connections " +
                "even though there's a problem with the certificate. The connection will still be encrypted, " +
                "but Oozie client won't validate the server certificate.");

        Options Options = new Options();
        Options.addOption(oozie);
        Options.addOption(doAs);
        Options.addOption(config);
        Options.addOption(property);
        Options.addOption(params);
        Options.addOption(file);
        Options.addOption(insecureAccess);
        addAuthOptions(Options);
        return Options;
    }

    /**
     * Create option for command line option 'sqoop'
     * @return sqoop options
     */
    @SuppressWarnings("static-access")
    protected Options createSqoopCLIOptions() {
        Option oozie = new Option(OozieClientConstant.OOZIE_OPTION, true, "Oozie URL");
        Option config = new Option(OozieClientConstant.CONFIG_OPTION, true, "job configuration file '.properties'");
        Option command = OptionBuilder.withArgName(OozieClientConstant.SQOOP_COMMAND_OPTION).hasArgs().withValueSeparator().withDescription(
                "sqoop command").create(OozieClientConstant.SQOOP_COMMAND_OPTION);
        Option property = OptionBuilder.withArgName("property=value").hasArgs(2).withValueSeparator().withDescription(
                "set/override value for given property").create("D");
        Option doAs = new Option(OozieClientConstant.DO_AS_OPTION, true, "doAs user, impersonates as the specified user");
        Option insecureAccess = new Option(OozieClientConstant.INSECURE_ACCESS_OPTION, false, "This option will allow SSL connections " +
                "even though there's a problem with the certificate. The connection will still be encrypted, " +
                "but Oozie client won't validate the server certificate.");

        Options Options = new Options();
        Options.addOption(oozie);
        Options.addOption(doAs);
        Options.addOption(config);
        Options.addOption(property);
        Options.addOption(command);
        Options.addOption(insecureAccess);
        addAuthOptions(Options);
        return Options;
    }

    /**
     * Create option for command line option 'info'
     * @return info options
     */
    protected Options createInfoOptions() {
        Option timezones = new Option(OozieClientConstant.INFO_TIME_ZONES_OPTION, false, "display a list of available time zones");
        Options infoOptions = new Options();
        infoOptions.addOption(timezones);
        return infoOptions;
    }

    /**
     * Create option for command line option 'mapreduce'
     * @return mapreduce options
     */
    @SuppressWarnings("static-access")
    protected Options createMROptions() {
        Option oozie = new Option(OozieClientConstant.OOZIE_OPTION, true, "Oozie URL");
        Option config = new Option(OozieClientConstant.CONFIG_OPTION, true, "job configuration file '.properties'");
        Option property = OptionBuilder.withArgName("property=value").hasArgs(2).withValueSeparator().withDescription(
                "set/override value for given property").create("D");
        Option doAs = new Option(OozieClientConstant.DO_AS_OPTION, true, "doAs user, impersonates as the specified user");
        Option insecureAccess = new Option(OozieClientConstant.INSECURE_ACCESS_OPTION, false, "This option will allow SSL connections " +
                "even though there's a problem with the certificate. The connection will still be encrypted, " +
                "but Oozie client won't validate the server certificate.");

        Options mrOptions = new Options();
        mrOptions.addOption(oozie);
        mrOptions.addOption(doAs);
        mrOptions.addOption(config);
        mrOptions.addOption(property);
        mrOptions.addOption(insecureAccess);
        addAuthOptions(mrOptions);
        return mrOptions;
    }

    /**
     * Run a CLI programmatically.
     * <p>
     * It does not exit the JVM.
     * <p>
     * A CLI instance can be used only once.
     *
     * @param args options and arguments for the Oozie CLI.
     * @return '0' (success), '-1' (failure).
     */
    public synchronized int run(String[] args) {
        if (used) {
            throw new IllegalStateException("CLI instance already used");
        }
        used = true;
        final CLIParser parser = getCLIParser();
        try {
            final CLIParser.Command command = parser.parse(args);

            String doAsUser = command.getCommandLine().getOptionValue(OozieClientConstant.DO_AS_OPTION);

            if (doAsUser != null) {
                OozieClient.doAs(doAsUser, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        processCommand(parser, command);
                        return null;
                    }
                });
            }
            else {
                processCommand(parser, command);
            }
            return 0;
        }
        catch (OozieCLIException ex) {
            System.err.println("Error: " + ex.getMessage());
            return -1;
        }
        catch (ParseException ex) {
            System.err.println("Invalid sub-command: " + ex.getMessage());
            System.err.println();
            System.err.println(parser.shortHelp());
            return -1;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
            return -1;
        }
    }

    @VisibleForTesting
    public CLIParser getCLIParser(){
        CLIParser parser = new CLIParser(OozieClientConstant.OOZIE_OPTION, getCLIHelp());
        parser.addCommand(OozieClientConstant.HELP_CMD, "", "display usage for all commands or specified command", new Options(), false);
        parser.addCommand(OozieClientConstant.VERSION_CMD, "", "show client version", new Options(), false);
        parser.addCommand(OozieClientConstant.JOB_CMD, "", "job operations", createJobOptions(), false);
        parser.addCommand(OozieClientConstant.JOBS_CMD, "", "jobs status", createJobsOptions(), false);
        parser.addCommand(OozieClientConstant.ADMIN_CMD, "", "admin operations", createAdminOptions(), false);
        parser.addCommand(OozieClientConstant.VALIDATE_CMD, "", "validate a workflow, coordinator, bundle XML file", createValidateOptions(), true);
        parser.addCommand(OozieClientConstant.SLA_CMD, "", "sla operations (Deprecated with Oozie 4.0)", createSlaOptions(), false);
        parser.addCommand(OozieClientConstant.PIG_CMD, "-X ", "submit a pig job, everything after '-X' are pass-through parameters to pig, any '-D' "
                + "arguments after '-X' are put in <configuration>", createScriptLanguageOptions(OozieClientConstant.PIG_CMD), true);
        parser.addCommand(OozieClientConstant.HIVE_CMD, "-X ", "submit a hive job, everything after '-X' are pass-through parameters to hive, any '-D' "
                + "arguments after '-X' are put in <configuration>", createScriptLanguageOptions(OozieClientConstant.HIVE_CMD), true);
        parser.addCommand(OozieClientConstant.SQOOP_CMD, "-X ", "submit a sqoop job, everything after '-X' are pass-through parameters " +
                "to sqoop, any '-D' arguments after '-X' are put in <configuration>", createSqoopCLIOptions(), true);
        parser.addCommand(OozieClientConstant.INFO_CMD, "", "get more detailed info about specific topics", createInfoOptions(), false);
        parser.addCommand(OozieClientConstant.MR_CMD, "", "submit a mapreduce job", createMROptions(), false);
        return parser;
    }

    public void processCommand(CLIParser parser, CLIParser.Command command) throws Exception {
        switch (command.getName()) {
            case OozieClientConstant.JOB_CMD:
                jobCommand(command.getCommandLine());
                break;
            case OozieClientConstant.JOBS_CMD:
                jobsCommand(command.getCommandLine());
                break;
            case OozieClientConstant.ADMIN_CMD:
                adminCommand(command.getCommandLine());
                break;
            case OozieClientConstant.VERSION_CMD:
                versionCommand();
                break;
            case OozieClientConstant.VALIDATE_CMD:
                validateCommand(command.getCommandLine());
                break;
            case OozieClientConstant.SLA_CMD:
                slaCommand(command.getCommandLine());
                break;
            case OozieClientConstant.PIG_CMD:
                scriptLanguageCommand(command.getCommandLine(), OozieClientConstant.PIG_CMD);
                break;
            case OozieClientConstant.HIVE_CMD:
                scriptLanguageCommand(command.getCommandLine(), OozieClientConstant.HIVE_CMD);
                break;
            case OozieClientConstant.SQOOP_CMD:
                sqoopCommand(command.getCommandLine());
                break;
            case OozieClientConstant.INFO_CMD:
                infoCommand(command.getCommandLine());
                break;
            case OozieClientConstant.MR_CMD:
                mrCommand(command.getCommandLine());
                break;
            default:
                parser.showHelp(command.getCommandLine());
        }
    }
    protected String getOozieUrl(CommandLine commandLine) {
        String url = commandLine.getOptionValue(OozieClientConstant.OOZIE_OPTION);
        if (url == null) {
            url = System.getenv(OozieClientConstant.ENV_OOZIE_URL);
            if (url == null) {
                throw new IllegalArgumentException(
                        "Oozie URL is not available neither in command option or in the environment");
            }
        }
        return url;
    }

    private String getTimeZoneId(CommandLine commandLine)
    {
        if (commandLine.hasOption(OozieClientConstant.LOCAL_TIME_OPTION)) {
            return null;
        }
        if (commandLine.hasOption(OozieClientConstant.TIME_ZONE_OPTION)) {
            return commandLine.getOptionValue(OozieClientConstant.TIME_ZONE_OPTION);
        }
        String timeZoneId = System.getenv(OozieClientConstant.ENV_OOZIE_TIME_ZONE);
        if (timeZoneId != null) {
            return timeZoneId;
        }
        return "GMT";
    }

    // Canibalized from Hadoop <code>Configuration.loadResource()</code>.
    private Properties parse(InputStream is, Properties conf) throws IOException {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setNamespaceAware(true);
            // support for includes in the xml file
            docBuilderFactory.setXIncludeAware(true);
            // ignore all comments inside the xml file
            docBuilderFactory.setIgnoringComments(true);
            docBuilderFactory.setExpandEntityReferences(false);
            docBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
            Document doc = builder.parse(is);
            return parseDocument(doc, conf);
        }
        catch (SAXException | ParserConfigurationException e) {
            throw new IOException(e);
        }
    }

    // Canibalized from Hadoop <code>Configuration.loadResource()</code>.
    private Properties parseDocument(Document doc, Properties conf) throws IOException {
        try {
            Element root = doc.getDocumentElement();
            if (!"configuration".equals(root.getLocalName())) {
                throw new RuntimeException("bad conf file: top-level element not <configuration>");
            }
            NodeList props = root.getChildNodes();
            for (int i = 0; i < props.getLength(); i++) {
                Node propNode = props.item(i);
                if (!(propNode instanceof Element)) {
                    continue;
                }
                Element prop = (Element) propNode;
                if (!"property".equals(prop.getLocalName())) {
                    throw new RuntimeException("bad conf file: element not <property>");
                }
                NodeList fields = prop.getChildNodes();
                String attr = null;
                String value = null;
                for (int j = 0; j < fields.getLength(); j++) {
                    Node fieldNode = fields.item(j);
                    if (!(fieldNode instanceof Element)) {
                        continue;
                    }
                    Element field = (Element) fieldNode;
                    if ("name".equals(field.getLocalName()) && field.hasChildNodes()) {
                        attr = ((Text) field.getFirstChild()).getData();
                    }
                    if ("value".equals(field.getLocalName()) && field.hasChildNodes()) {
                        value = ((Text) field.getFirstChild()).getData();
                    }
                }

                if (attr != null && value != null) {
                    conf.setProperty(attr, value);
                }
            }
            return conf;
        }
        catch (DOMException e) {
            throw new IOException(e);
        }
    }

    private Properties getConfiguration(OozieClient wc, CommandLine commandLine) throws IOException {
        if (!isConfigurationSpecified(wc, commandLine)) {
            throw new IOException("configuration is not specified");
        }
        Properties conf = wc.createConfiguration();
        String configFile = commandLine.getOptionValue(OozieClientConstant.CONFIG_OPTION);
        if (configFile != null) {
            File file = new File(configFile);
            if (!file.exists()) {
                throw new IOException("configuration file [" + configFile + "] not found");
            }
            if (configFile.endsWith(".properties")) {
                conf.load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            }
            else if (configFile.endsWith(".xml")) {
                parse(new FileInputStream(configFile), conf);
            }
            else {
                throw new IllegalArgumentException("configuration must be a '.properties' or a '.xml' file");
            }
        }
        if (commandLine.hasOption("D")) {
            Properties commandLineProperties = commandLine.getOptionProperties("D");
            conf.putAll(commandLineProperties);
        }
        return conf;
    }

    /**
     * Check if configuration has specified
     * @param wc
     * @param commandLine
     * @return isConf
     * @throws IOException
     */
    private boolean isConfigurationSpecified(OozieClient wc, CommandLine commandLine) throws IOException {
        boolean isConf;
        String configFile = commandLine.getOptionValue(OozieClientConstant.CONFIG_OPTION);
        if (configFile == null) {
            isConf = false;
        }
        else {
            isConf = new File(configFile).exists();
        }
        if (commandLine.hasOption("D")) {
            isConf = true;
        }
        return isConf;
    }

    /**
     * @param commandLine command line string.
     * @return change value specified by -value.
     * @throws OozieCLIException
     */
	private String getChangeValue(CommandLine commandLine) throws OozieCLIException {
        String changeValue = commandLine.getOptionValue(OozieClientConstant.CHANGE_VALUE_OPTION);

        if (changeValue == null) {
            throw new OozieCLIException("-value option needs to be specified for -change option");
        }

        return changeValue;
    }

    protected void addHeader(OozieClient wc, CommandLine commandLine) {
        String username = commandLine.getOptionValue(OozieClientConstant.USERNAME);
        String password = commandLine.getOptionValue(OozieClientConstant.PASSWORD);
        if (username != null && password != null) {
            String encoded = Base64.getEncoder().encodeToString((username + ':' + password).getBytes(StandardCharsets.UTF_8));
            wc.setHeader("Authorization", "Basic " + encoded);
        }
        for (Map.Entry entry : System.getProperties().entrySet()) {
            String key = (String) entry.getKey();
            if (key.startsWith(OozieClientConstant.WS_HEADER_PREFIX)) {
                String header = key.substring(OozieClientConstant.WS_HEADER_PREFIX.length());
                wc.setHeader(header, (String) entry.getValue());
            }
        }
    }

    /**
     * Get auth option from command line
     *
     * @param commandLine the command line object
     * @return auth option
     * @throws OozieCLIException if wrong authentication parameters are used.
     */
    protected String getAuthOption(CommandLine commandLine) throws OozieCLIException {
        if (commandLine.hasOption(OozieClientConstant.USERNAME)) {
            if (commandLine.hasOption(OozieClientConstant.PASSWORD)) {
                return AuthType.BASIC.name();
            } else {
                throw new OozieCLIException(
                        "No " + OozieClientConstant.PASSWORD + " specified, it is required, if " + OozieClientConstant.USERNAME + " is set!");
            }
        } else {
            if (commandLine.hasOption(OozieClientConstant.PASSWORD)) {
                throw new OozieCLIException(
                        "No " + OozieClientConstant.USERNAME + " specified, it is required, if " + OozieClientConstant.PASSWORD + " is set!");
            }
        }
        String authOpt = commandLine.getOptionValue(OozieClientConstant.AUTH_OPTION);
        if (authOpt == null) {
            authOpt = System.getenv(OozieClientConstant.ENV_OOZIE_AUTH);
        }
        if (commandLine.hasOption(OozieClientConstant.DEBUG_OPTION)) {
            System.out.println(" Auth type : " + authOpt);
        }
        if (AuthType.BASIC.name().equalsIgnoreCase(authOpt)) {
            throw new OozieCLIException(
                    "BASIC authentication requires -" + OozieClientConstant.USERNAME + " and -" + OozieClientConstant.PASSWORD + " to set!");
        }
        return authOpt;
    }

    /**
     * Create a OozieClient.
     * <p>
     * It injects any '-Dheader:' as header to the the {@link OozieClient}.
     *
     * @param commandLine the parsed command line options.
     * @return a pre configured eXtended workflow client.
     * @throws OozieCLIException thrown if the OozieClient could not be configured.
     */
    protected OozieClient createOozieClient(CommandLine commandLine) throws OozieCLIException {
        return createXOozieClient(commandLine);
    }

    /**
     * Create a XOozieClient.
     * <p>
     * It injects any '-Dheader:' as header to the the {@link OozieClient}.
     *
     * @param commandLine the parsed command line options.
     * @return a pre configured eXtended workflow client.
     * @throws OozieCLIException thrown if the XOozieClient could not be configured.
     */
    protected XOozieClient createXOozieClient(CommandLine commandLine) throws OozieCLIException {
        XOozieClient wc = new AuthOozieClient(getOozieUrl(commandLine), getAuthOption(commandLine),
                isInsecureConnEnabled(commandLine));
        addHeader(wc, commandLine);
        setDebugMode(wc,commandLine.hasOption(OozieClientConstant.DEBUG_OPTION));
        setRetryCount(wc);
        return wc;
    }

    private boolean isInsecureConnEnabled(CommandLine commandLine) {
        return commandLine.hasOption(OozieClientConstant.INSECURE_ACCESS_OPTION);
    }

    protected void setDebugMode(OozieClient wc, boolean debugOpt) {

        String debug = System.getenv(OozieClientConstant.ENV_OOZIE_DEBUG);
        if (debug != null && !debug.isEmpty()) {
            int debugVal = 0;
            try {
                debugVal = Integer.parseInt(debug.trim());
            }
            catch (Exception ex) {
                System.out.println("Unable to parse the debug settings. May be not an integer [" + debug + "]");
                ex.printStackTrace();
            }
            wc.setDebugMode(debugVal);
        }
        else if(debugOpt){  // CLI argument "-debug" used
            wc.setDebugMode(1);
        }
    }

    protected void setRetryCount(OozieClient wc) {
        String retryCount = System.getProperty(OozieClientConstant.OOZIE_RETRY_COUNT);
        if (retryCount != null && !retryCount.isEmpty()) {
            try {
                int retry = Integer.parseInt(retryCount.trim());
                wc.setRetryCount(retry);
            }
            catch (Exception ex) {
                System.err.println("Unable to parse the retry settings. May be not an integer [" + retryCount + "]");
                ex.printStackTrace();
            }
        }
    }

    private static String JOB_ID_PREFIX = "job: ";

    private void jobCommand(CommandLine commandLine) throws IOException, OozieCLIException {
        XOozieClient wc = createXOozieClient(commandLine);

        List<String> options = new ArrayList<>();
        for (Option option : commandLine.getOptions()) {
            options.add(option.getOpt());
        }

        try {
            if (options.contains(OozieClientConstant.SUBMIT_OPTION)) {
                System.out.println(JOB_ID_PREFIX + wc.submit(getConfiguration(wc, commandLine)));
            }
            else if (options.contains(OozieClientConstant.START_OPTION)) {
                wc.start(commandLine.getOptionValue(OozieClientConstant.START_OPTION));
            }
            else if (options.contains(OozieClientConstant.DRYRUN_OPTION) && !options.contains(OozieClientConstant.UPDATE_OPTION)) {
                String dryrunStr = wc.dryrun(getConfiguration(wc, commandLine));
                if (dryrunStr.equals("OK")) {  // workflow
                    System.out.println("OK");
                }
                else if (dryrunStr.contains("<bundle-app")) {
                    // bundle
                    System.out.println("***Bundle job after parsing: ***");
                    System.out.println(dryrunStr);
                }
                else {
                    // coordinator
                    String[] dryrunStrs = dryrunStr.split("action for new instance");
                    int arraysize = dryrunStrs.length;
                    System.out.println("***coordJob after parsing: ***");
                    System.out.println(dryrunStrs[0]);
                    int aLen = dryrunStrs.length - 1;
                    if (aLen < 0) {
                        aLen = 0;
                    }
                    System.out.println("***total coord actions is " + aLen + " ***");
                    for (int i = 1; i <= arraysize - 1; i++) {
                        System.out.println(RULER);
                        System.out.println("coordAction instance: " + i + ":");
                        System.out.println(dryrunStrs[i]);
                    }
                }
            }
            else if (options.contains(OozieClientConstant.SUSPEND_OPTION)) {
                wc.suspend(commandLine.getOptionValue(OozieClientConstant.SUSPEND_OPTION));
            }
            else if (options.contains(OozieClientConstant.RESUME_OPTION)) {
                wc.resume(commandLine.getOptionValue(OozieClientConstant.RESUME_OPTION));
            }
            else if (options.contains(OozieClientConstant.IGNORE_OPTION)) {
                String ignoreScope = null;
                if (options.contains(OozieClientConstant.ACTION_OPTION)) {
                    ignoreScope = commandLine.getOptionValue(OozieClientConstant.ACTION_OPTION);
                    if (ignoreScope == null || ignoreScope.isEmpty()) {
                        throw new OozieCLIException("-" + OozieClientConstant.ACTION_OPTION + " is empty");
                    }
                }
                printCoordActionsStatus(wc.ignore(commandLine.getOptionValue(OozieClientConstant.IGNORE_OPTION), ignoreScope));
            }
            else if (options.contains(OozieClientConstant.KILL_OPTION)) {
                if (commandLine.getOptionValue(OozieClientConstant.KILL_OPTION).contains("-C")
                        && (options.contains(OozieClientConstant.DATE_OPTION) || options.contains(OozieClientConstant.ACTION_OPTION))) {
                    String coordJobId = commandLine.getOptionValue(OozieClientConstant.KILL_OPTION);
                    String scope;
                    String rangeType;
                    if (options.contains(OozieClientConstant.DATE_OPTION) && options.contains(OozieClientConstant.ACTION_OPTION)) {
                        throw new OozieCLIException("Invalid options provided for rerun: either" + OozieClientConstant.DATE_OPTION + " or "
                                + OozieClientConstant.ACTION_OPTION + " expected. Don't use both at the same time.");
                    }
                    if (options.contains(OozieClientConstant.DATE_OPTION)) {
                        rangeType = RestConstants.JOB_COORD_SCOPE_DATE;
                        scope = commandLine.getOptionValue(OozieClientConstant.DATE_OPTION);
                    }
                    else if (options.contains(OozieClientConstant.ACTION_OPTION)) {
                        rangeType = RestConstants.JOB_COORD_SCOPE_ACTION;
                        scope = commandLine.getOptionValue(OozieClientConstant.ACTION_OPTION);
                    }
                    else {
                        throw new OozieCLIException("Invalid options provided for rerun: " + OozieClientConstant.DATE_OPTION + " or "
                                + OozieClientConstant.ACTION_OPTION + " expected.");
                    }
                    printCoordActions(wc.kill(coordJobId, rangeType, scope));
                }
                else {
                    wc.kill(commandLine.getOptionValue(OozieClientConstant.KILL_OPTION));
                }
            }
            else if (options.contains(OozieClientConstant.CHANGE_OPTION)) {
                wc.change(commandLine.getOptionValue(OozieClientConstant.CHANGE_OPTION), getChangeValue(commandLine));
            }
            else if (options.contains(OozieClientConstant.RUN_OPTION)) {
                System.out.println(JOB_ID_PREFIX + wc.run(getConfiguration(wc, commandLine)));
            }
            else if (options.contains(OozieClientConstant.RERUN_OPTION)) {
                if (commandLine.getOptionValue(OozieClientConstant.RERUN_OPTION).contains("-W")) {
                    if (isConfigurationSpecified(wc, commandLine)) {
                        wc.reRun(commandLine.getOptionValue(OozieClientConstant.RERUN_OPTION), getConfiguration(wc, commandLine));
                    }
                    else {
                        wc.reRun(commandLine.getOptionValue(OozieClientConstant.RERUN_OPTION), new Properties());
                    }
                }
                else if (commandLine.getOptionValue(OozieClientConstant.RERUN_OPTION).contains("-B")) {
                    String bundleJobId = commandLine.getOptionValue(OozieClientConstant.RERUN_OPTION);
                    String coordScope = null;
                    String dateScope = null;
                    boolean refresh = false;
                    boolean noCleanup = false;
                    if (options.contains(OozieClientConstant.ACTION_OPTION)) {
                        throw new OozieCLIException("Invalid options provided for bundle rerun. " + OozieClientConstant.ACTION_OPTION
                                + " is not valid for bundle rerun");
                    }
                    if (options.contains(OozieClientConstant.DATE_OPTION)) {
                        dateScope = commandLine.getOptionValue(OozieClientConstant.DATE_OPTION);
                    }

                    if (options.contains(OozieClientConstant.COORD_OPTION)) {
                        coordScope = commandLine.getOptionValue(OozieClientConstant.COORD_OPTION);
                    }

                    if (options.contains(OozieClientConstant.RERUN_REFRESH_OPTION)) {
                        refresh = true;
                    }
                    if (options.contains(OozieClientConstant.RERUN_NOCLEANUP_OPTION)) {
                        noCleanup = true;
                    }
                    wc.reRunBundle(bundleJobId, coordScope, dateScope, refresh, noCleanup);
                    if (coordScope != null && !coordScope.isEmpty()) {
                        System.out.println("Coordinators [" + coordScope + "] of bundle " + bundleJobId
                                + " are scheduled to rerun on date ranges [" + dateScope + "].");
                    }
                    else {
                        System.out.println("All coordinators of bundle " + bundleJobId
                                + " are scheduled to rerun on the date ranges [" + dateScope + "].");
                    }
                }
                else {
                    String coordJobId = commandLine.getOptionValue(OozieClientConstant.RERUN_OPTION);
                    String scope;
                    String rerunType;
                    boolean refresh = false;
                    boolean noCleanup = false;
                    boolean failed = false;
                    if (options.contains(OozieClientConstant.DATE_OPTION) && options.contains(OozieClientConstant.ACTION_OPTION)) {
                        throw new OozieCLIException("Invalid options provided for rerun: either" + OozieClientConstant.DATE_OPTION + " or "
                                + OozieClientConstant.ACTION_OPTION + " expected. Don't use both at the same time.");
                    }
                    if (options.contains(OozieClientConstant.DATE_OPTION)) {
                        rerunType = RestConstants.JOB_COORD_SCOPE_DATE;
                        scope = commandLine.getOptionValue(OozieClientConstant.DATE_OPTION);
                    }
                    else if (options.contains(OozieClientConstant.ACTION_OPTION)) {
                        rerunType = RestConstants.JOB_COORD_SCOPE_ACTION;
                        scope = commandLine.getOptionValue(OozieClientConstant.ACTION_OPTION);
                    }
                    else {
                        throw new OozieCLIException("Invalid options provided for rerun: " + OozieClientConstant.DATE_OPTION + " or "
                                + OozieClientConstant.ACTION_OPTION + " expected.");
                    }
                    if (options.contains(OozieClientConstant.RERUN_REFRESH_OPTION)) {
                        refresh = true;
                    }
                    if (options.contains(OozieClientConstant.RERUN_NOCLEANUP_OPTION)) {
                        noCleanup = true;
                    }

                    Properties props = null;
                    if(isConfigurationSpecified(wc, commandLine)) {
                        props = getConfiguration(wc, commandLine);
                    }

                    if (options.contains(OozieClientConstant.RERUN_FAILED_OPTION)) {
                        failed = true;
                    }

                    printCoordActions(wc.reRunCoord(coordJobId, rerunType, scope, refresh, noCleanup, failed, props));
                }
            }
            else if (options.contains(OozieClientConstant.INFO_OPTION)) {
                String timeZoneId = getTimeZoneId(commandLine);
                final String optionValue = commandLine.getOptionValue(OozieClientConstant.INFO_OPTION);
                if (optionValue.endsWith("-B")) {
                    String filter = commandLine.getOptionValue(OozieClientConstant.FILTER_OPTION);
                    if (filter != null) {
                        throw new OozieCLIException("Filter option is currently not supported for a Bundle job");
                    }
                    printBundleJob(wc.getBundleJobInfo(optionValue), timeZoneId,
                            options.contains(OozieClientConstant.VERBOSE_OPTION));
                }
                else if (optionValue.endsWith("-C")) {
                    String s = commandLine.getOptionValue(OozieClientConstant.OFFSET_OPTION);
                    int start = Integer.parseInt((s != null) ? s : "-1");
                    s = commandLine.getOptionValue(OozieClientConstant.LEN_OPTION);
                    int len = Integer.parseInt((s != null) ? s : "-1");
                    String filter = commandLine.getOptionValue(OozieClientConstant.FILTER_OPTION);
                    String order = commandLine.getOptionValue(OozieClientConstant.ORDER_OPTION);
                    printCoordJob(wc.getCoordJobInfo(optionValue, filter, start, len, order), timeZoneId,
                            options.contains(OozieClientConstant.VERBOSE_OPTION));
                }
                else if (optionValue.contains("-C@")) {
                    if (options.contains(OozieClientConstant.ALL_WORKFLOWS_FOR_COORD_ACTION)) {
                        printWfsForCoordAction(wc.getWfsForCoordAction(optionValue), timeZoneId);
                    }
                    else {
                        String filter = commandLine.getOptionValue(OozieClientConstant.FILTER_OPTION);
                        if (filter != null) {
                            throw new OozieCLIException("Filter option is not supported for a Coordinator action");
                        }
                        printCoordAction(wc.getCoordActionInfo(optionValue), timeZoneId);
                    }
                }
                else if (optionValue.contains("-W@")) {
                    String filter = commandLine.getOptionValue(OozieClientConstant.FILTER_OPTION);
                    if (filter != null) {
                        throw new OozieCLIException("Filter option is not supported for a Workflow action");
                    }
                    printWorkflowAction(wc.getWorkflowActionInfo(optionValue), timeZoneId,
                            options.contains(OozieClientConstant.VERBOSE_OPTION));

                }
                else {
                    String filter = commandLine.getOptionValue(OozieClientConstant.FILTER_OPTION);
                    if (filter != null) {
                        throw new OozieCLIException("Filter option is currently not supported for a Workflow job");
                    }
                    String s = commandLine.getOptionValue(OozieClientConstant.OFFSET_OPTION);
                    int start = Integer.parseInt((s != null) ? s : "0");
                    s = commandLine.getOptionValue(OozieClientConstant.LEN_OPTION);
                    String jobtype = commandLine.getOptionValue(OozieClientConstant.JOBTYPE_OPTION);
                    jobtype = (jobtype != null) ? jobtype : "wf";
                    int len = Integer.parseInt((s != null) ? s : "0");
                    printJob(wc.getJobInfo(optionValue, start, len), timeZoneId,
                            options.contains(OozieClientConstant.VERBOSE_OPTION));
                }
            }
            else if (options.contains(OozieClientConstant.LOG_OPTION)) {
                PrintStream ps = System.out;
                String logFilter = null;
                if (options.contains(RestConstants.LOG_FILTER_OPTION)) {
                    logFilter = commandLine.getOptionValue(RestConstants.LOG_FILTER_OPTION);
                }
                if (commandLine.getOptionValue(OozieClientConstant.LOG_OPTION).contains("-C")) {
                    String logRetrievalScope = null;
                    String logRetrievalType = null;
                    if (options.contains(OozieClientConstant.ACTION_OPTION)) {
                        logRetrievalType = RestConstants.JOB_LOG_ACTION;
                        logRetrievalScope = commandLine.getOptionValue(OozieClientConstant.ACTION_OPTION);
                    }
                    if (options.contains(OozieClientConstant.DATE_OPTION)) {
                        logRetrievalType = RestConstants.JOB_LOG_DATE;
                        logRetrievalScope = commandLine.getOptionValue(OozieClientConstant.DATE_OPTION);
                    }
                    try {
                        wc.getJobLog(commandLine.getOptionValue(OozieClientConstant.LOG_OPTION), logRetrievalType, logRetrievalScope,
                                logFilter, ps);
                    }
                    finally {
                        ps.close();
                    }
                }
                else {
                    if (!options.contains(OozieClientConstant.ACTION_OPTION) && !options.contains(OozieClientConstant.DATE_OPTION)) {
                        wc.getJobLog(commandLine.getOptionValue(OozieClientConstant.LOG_OPTION), null, null, logFilter, ps);
                    }
                    else {
                        throw new OozieCLIException("Invalid options provided for log retrieval. " + OozieClientConstant.ACTION_OPTION
                                + " and " + OozieClientConstant.DATE_OPTION + " are valid only for coordinator job log retrieval");
                    }
                }
            }
            else if (options.contains(OozieClientConstant.ERROR_LOG_OPTION)) {
                try (PrintStream ps = System.out) {
                    wc.getJobErrorLog(commandLine.getOptionValue(OozieClientConstant.ERROR_LOG_OPTION), ps);
                }
            }
            else if (options.contains(OozieClientConstant.AUDIT_LOG_OPTION)) {
                try (PrintStream ps = System.out) {
                    wc.getJobAuditLog(commandLine.getOptionValue(OozieClientConstant.AUDIT_LOG_OPTION), ps);
                }
            }
            else if (options.contains(OozieClientConstant.DEFINITION_OPTION)) {
                System.out.println(wc.getJobDefinition(commandLine.getOptionValue(OozieClientConstant.DEFINITION_OPTION)));
            }
            else if (options.contains(OozieClientConstant.CONFIG_CONTENT_OPTION)) {
                if (commandLine.getOptionValue(OozieClientConstant.CONFIG_CONTENT_OPTION).endsWith("-C")) {
                    System.out.println(wc.getCoordJobInfo(commandLine.getOptionValue(OozieClientConstant.CONFIG_CONTENT_OPTION)).getConf());
                }
                else if (commandLine.getOptionValue(OozieClientConstant.CONFIG_CONTENT_OPTION).endsWith("-W")) {
                    System.out.println(wc.getJobInfo(commandLine.getOptionValue(OozieClientConstant.CONFIG_CONTENT_OPTION)).getConf());
                }
                else if (commandLine.getOptionValue(OozieClientConstant.CONFIG_CONTENT_OPTION).endsWith("-B")) {
                    System.out
                            .println(wc.getBundleJobInfo(commandLine.getOptionValue(OozieClientConstant.CONFIG_CONTENT_OPTION)).getConf());
                }
                else {
                    System.out.println("ERROR:  job id [" + commandLine.getOptionValue(OozieClientConstant.CONFIG_CONTENT_OPTION)
                            + "] doesn't end with either C or W or B");
                }
            }
            else if (options.contains(OozieClientConstant.UPDATE_OPTION)) {
                String coordJobId = commandLine.getOptionValue(OozieClientConstant.UPDATE_OPTION);
                Properties conf = null;

                String dryrun = "";
                String showdiff = "";

                if (commandLine.getOptionValue(OozieClientConstant.CONFIG_OPTION) != null) {
                    conf = getConfiguration(wc, commandLine);
                }
                if (options.contains(OozieClientConstant.DRYRUN_OPTION)) {
                    dryrun = "true";
                }
                if (commandLine.getOptionValue(OozieClientConstant.SHOWDIFF_OPTION) != null) {
                    showdiff = commandLine.getOptionValue(OozieClientConstant.SHOWDIFF_OPTION);
                }
                if (conf == null) {
                    System.out.println(wc.updateCoord(coordJobId, dryrun, showdiff));
                }
                else {
                    System.out.println(wc.updateCoord(coordJobId, conf, dryrun, showdiff));
                }
            }
            else if (options.contains(OozieClientConstant.POLL_OPTION)) {
                String jobId = commandLine.getOptionValue(OozieClientConstant.POLL_OPTION);
                int timeout = 30;
                int interval = 5;
                String timeoutS = commandLine.getOptionValue(OozieClientConstant.TIMEOUT_OPTION);
                if (timeoutS != null) {
                    timeout = Integer.parseInt(timeoutS);
                }
                String intervalS = commandLine.getOptionValue(OozieClientConstant.INTERVAL_OPTION);
                if (intervalS != null) {
                    interval = Integer.parseInt(intervalS);
                }
                boolean verbose = commandLine.hasOption(OozieClientConstant.VERBOSE_OPTION);
                wc.pollJob(jobId, timeout, interval, verbose);
            }
            else if (options.contains(OozieClientConstant.SLA_ENABLE_ALERT)) {
                slaAlertCommand(commandLine.getOptionValue(OozieClientConstant.SLA_ENABLE_ALERT), wc, commandLine, options);
            }
            else if (options.contains(OozieClientConstant.SLA_DISABLE_ALERT)) {
                slaAlertCommand(commandLine.getOptionValue(OozieClientConstant.SLA_DISABLE_ALERT), wc, commandLine, options);
            }
            else if (options.contains(OozieClientConstant.SLA_CHANGE)) {
                slaAlertCommand(commandLine.getOptionValue(OozieClientConstant.SLA_CHANGE), wc, commandLine, options);
            }
            else if (options.contains(OozieClientConstant.WORKFLOW_ACTIONS_RETRIES)) {
                printWorkflowActionRetries(
                        wc.getWorkflowActionRetriesInfo(commandLine.getOptionValue(OozieClientConstant.WORKFLOW_ACTIONS_RETRIES)),
                        commandLine.getOptionValue(OozieClientConstant.WORKFLOW_ACTIONS_RETRIES));
            }
            else if (options.contains(OozieClientConstant.COORD_ACTION_MISSING_DEPENDENCIES)) {
                String actions = null, dates = null;

                if (options.contains(OozieClientConstant.ACTION_OPTION)) {
                    actions = commandLine.getOptionValue(OozieClientConstant.ACTION_OPTION);
                }

                if (options.contains(OozieClientConstant.DATE_OPTION)) {
                    dates = commandLine.getOptionValue(OozieClientConstant.DATE_OPTION);
                }
                wc.getCoordActionMissingDependencies(commandLine.getOptionValue(OozieClientConstant.COORD_ACTION_MISSING_DEPENDENCIES),
                        actions, dates, System.out);
            }
            else if (options.contains(OozieClientConstant.VALIDATE_JAR_OPTION)) {
                checkApiJar(wc, commandLine, options.contains(OozieClientConstant.VERBOSE_OPTION));
            }
            else if (options.contains(OozieClientConstant.SUBMIT_JAR_OPTION)) {
                submitApiJar(wc, commandLine, options.contains(OozieClientConstant.VERBOSE_OPTION));
            }
            else if (options.contains(OozieClientConstant.RUN_JAR_OPTION)) {
                runApiJar(wc, commandLine, options.contains(OozieClientConstant.VERBOSE_OPTION));
            }
        }
        catch (final OozieClientException ex) {
            throw new OozieCLIException(ex.toString(), ex);
        }
    }

    private void checkApiJar(final XOozieClient wc, final CommandLine commandLine, final boolean verbose)
            throws OozieClientException {
        final String apiJarPath = commandLine.getOptionValue(OozieClientConstant.VALIDATE_JAR_OPTION);
        logIfVerbose(verbose, "Checking API jar: " + apiJarPath);

        final String generatedXml = loadApiJarAndGenerateXml(apiJarPath, verbose);

        final Path workflowXml;
        try {
            workflowXml = Files.createTempFile("workflow", ".xml");
            Files.write(workflowXml, generatedXml.getBytes(StandardCharsets.UTF_8));

            logIfVerbose(verbose, "API jar was written to " + workflowXml.toString());
        }
        catch (final IOException e) {
            throw new OozieClientException(e.getMessage(), e);
        }

        logIfVerbose(verbose, "Servlet response is: ");
        System.out.println(wc.validateXML(workflowXml.toString()));

        logIfVerbose(verbose, "API jar is valid.");
    }

    private void logIfVerbose(final boolean verbose, final String message) {
        if (verbose) {
            System.out.println(message);
        }
    }

    @SuppressFBWarnings(value = {"PATH_TRAVERSAL_IN", "WEAK_FILENAMEUTILS"},
            justification = "FilenameUtils is used to filter user input. JDK8+ is used.")
    private String loadApiJarAndGenerateXml(final String apiJarPath, final boolean verbose) throws OozieClientException {
        final String generatedXml;
        try {
            logIfVerbose(verbose, "Loading API jar " + apiJarPath);

            final Workflow generatedWorkflow = new ApiJarLoader(new File(
                    FilenameUtils.getFullPath(apiJarPath) + FilenameUtils.getName(apiJarPath)))
                    .loadAndGenerate();
            generatedXml = WorkflowMarshaller.marshal(generatedWorkflow);

            logIfVerbose(verbose, "Workflow job definition generated from API jar: \n" + generatedXml);
        }
        catch (final IOException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException |
                InvocationTargetException | InstantiationException | JAXBException e) {
            throw new OozieClientException(e.getMessage(), e);
        }

        return generatedXml;
    }

    private void submitApiJar(final XOozieClient wc, final CommandLine commandLine, final boolean verbose)
            throws OozieClientException {
        final String apiJarPath = commandLine.getOptionValue(OozieClientConstant.SUBMIT_JAR_OPTION);
        logIfVerbose(verbose, "Submitting a job based on API jar: " + apiJarPath);

        try {
            System.out.println(JOB_ID_PREFIX + wc.submit(getConfiguration(wc, commandLine),
                    loadApiJarAndGenerateXml(apiJarPath, verbose)));
        }
        catch (final IOException e) {
            throw new OozieClientException(e.getMessage(), e);
        }

        logIfVerbose(verbose, "Job based on API jar submitted successfully.");
    }

    private void runApiJar(final XOozieClient wc, final CommandLine commandLine, final boolean verbose)
            throws OozieClientException {
        final String apiJarPath = commandLine.getOptionValue(OozieClientConstant.RUN_JAR_OPTION);
        logIfVerbose(verbose, "Running a job based on API jar: " + apiJarPath);

        try {
            System.out.println(JOB_ID_PREFIX + wc.run(getConfiguration(wc, commandLine),
                    loadApiJarAndGenerateXml(apiJarPath, verbose)));
        }
        catch (final IOException e) {
            throw new OozieClientException(e.getMessage(), e);
        }

        logIfVerbose(verbose, "Job based on API jar run successfully.");
    }

    @VisibleForTesting
    void printCoordJob(CoordinatorJob coordJob, String timeZoneId, boolean verbose) {
        System.out.println("Job ID : " + coordJob.getId());

        System.out.println(RULER);

        List<CoordinatorAction> actions = coordJob.getActions();
        System.out.println("Job Name    : " + maskIfNull(coordJob.getAppName()));
        System.out.println("App Path    : " + maskIfNull(coordJob.getAppPath()));
        System.out.println("Status      : " + coordJob.getStatus());
        System.out.println("Start Time  : " + maskDate(coordJob.getStartTime(), timeZoneId, false));
        System.out.println("End Time    : " + maskDate(coordJob.getEndTime(), timeZoneId, false));
        System.out.println("Pause Time  : " + maskDate(coordJob.getPauseTime(), timeZoneId, false));
        System.out.println("Concurrency : " + coordJob.getConcurrency());
        System.out.println(RULER);

        if (verbose) {
            System.out.println("ID" + OozieClientConstant.VERBOSE_DELIMITER + "Action Number" + OozieClientConstant.VERBOSE_DELIMITER + "Console URL"
                    + OozieClientConstant.VERBOSE_DELIMITER + "Error Code" + OozieClientConstant.VERBOSE_DELIMITER + "Error Message" + OozieClientConstant.VERBOSE_DELIMITER
                    + "External ID" + OozieClientConstant.VERBOSE_DELIMITER + "External Status" + OozieClientConstant.VERBOSE_DELIMITER + "Job ID"
                    + OozieClientConstant.VERBOSE_DELIMITER + "Tracker URI" + OozieClientConstant.VERBOSE_DELIMITER + "Created" + OozieClientConstant.VERBOSE_DELIMITER
                    + "Nominal Time" + OozieClientConstant.VERBOSE_DELIMITER + "Status" + OozieClientConstant.VERBOSE_DELIMITER + "Last Modified"
                    + OozieClientConstant.VERBOSE_DELIMITER + "Missing Dependencies");
            System.out.println(RULER);

            for (CoordinatorAction action : actions) {
                System.out.println(maskIfNull(action.getId()) + OozieClientConstant.VERBOSE_DELIMITER + action.getActionNumber()
                        + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(action.getConsoleUrl()) + OozieClientConstant.VERBOSE_DELIMITER
                        + maskIfNull(action.getErrorCode()) + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(action.getErrorMessage())
                        + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(action.getExternalId()) + OozieClientConstant.VERBOSE_DELIMITER
                        + maskIfNull(action.getExternalStatus()) + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(action.getJobId())
                        + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(action.getTrackerUri()) + OozieClientConstant.VERBOSE_DELIMITER
                        + maskDate(action.getCreatedTime(), timeZoneId, verbose) + OozieClientConstant.VERBOSE_DELIMITER
                        + maskDate(action.getNominalTime(), timeZoneId, verbose) + OozieClientConstant.VERBOSE_DELIMITER + action.getStatus() + OozieClientConstant.VERBOSE_DELIMITER
                        + maskDate(action.getLastModifiedTime(), timeZoneId, verbose) + OozieClientConstant.VERBOSE_DELIMITER
                        + maskIfNull(getFirstMissingDependencies(action)));

                System.out.println(RULER);
            }
        }
        else {
            System.out.println(String.format(COORD_ACTION_FORMATTER, "ID", "Status", "Ext ID", "Err Code", "Created",
                    "Nominal Time", "Last Mod"));

            for (CoordinatorAction action : actions) {
                System.out.println(String.format(COORD_ACTION_FORMATTER, maskIfNull(action.getId()),
                        action.getStatus(), maskIfNull(action.getExternalId()), maskIfNull(action.getErrorCode()),
                        maskDate(action.getCreatedTime(), timeZoneId, verbose),
                        maskDate(action.getNominalTime(), timeZoneId, verbose),
                        maskDate(action.getLastModifiedTime(), timeZoneId, verbose)));

                System.out.println(RULER);
            }
        }
    }

    @VisibleForTesting
    void printBundleJob(BundleJob bundleJob, String timeZoneId, boolean verbose) {
        System.out.println("Job ID : " + bundleJob.getId());

        System.out.println(RULER);

        List<CoordinatorJob> coordinators = bundleJob.getCoordinators();
        System.out.println("Job Name : " + maskIfNull(bundleJob.getAppName()));
        System.out.println("App Path : " + maskIfNull(bundleJob.getAppPath()));
        System.out.println("Status   : " + bundleJob.getStatus());
        System.out.println("Kickoff time   : " + bundleJob.getKickoffTime());
        System.out.println(RULER);

        System.out.println(String.format(BUNDLE_COORD_JOBS_FORMATTER, "Job ID", "Status", "Freq", "Unit", "Started",
                "Next Materialized"));
        System.out.println(RULER);

        for (CoordinatorJob job : coordinators) {
            System.out.println(String.format(BUNDLE_COORD_JOBS_FORMATTER, maskIfNull(job.getId()), job.getStatus(),
                    job.getFrequency(), job.getTimeUnit(), maskDate(job.getStartTime(), timeZoneId, verbose),
                    maskDate(job.getNextMaterializedTime(), timeZoneId, verbose)));

            System.out.println(RULER);
        }
    }

    @VisibleForTesting
    void printCoordAction(CoordinatorAction coordAction, String timeZoneId) {
        System.out.println("ID : " + maskIfNull(coordAction.getId()));

        System.out.println(RULER);

        System.out.println("Action Number        : " + coordAction.getActionNumber());
        System.out.println("Console URL          : " + maskIfNull(coordAction.getConsoleUrl()));
        System.out.println("Error Code           : " + maskIfNull(coordAction.getErrorCode()));
        System.out.println("Error Message        : " + maskIfNull(coordAction.getErrorMessage()));
        System.out.println("External ID          : " + maskIfNull(coordAction.getExternalId()));
        System.out.println("External Status      : " + maskIfNull(coordAction.getExternalStatus()));
        System.out.println("Job ID               : " + maskIfNull(coordAction.getJobId()));
        System.out.println("Tracker URI          : " + maskIfNull(coordAction.getTrackerUri()));
        System.out.println("Created              : " + maskDate(coordAction.getCreatedTime(), timeZoneId, false));
        System.out.println("Nominal Time         : " + maskDate(coordAction.getNominalTime(), timeZoneId, false));
        System.out.println("Status               : " + coordAction.getStatus());
        System.out.println("Last Modified        : " + maskDate(coordAction.getLastModifiedTime(), timeZoneId, false));
        System.out.println("First Missing Dependency : " + maskIfNull(getFirstMissingDependencies(coordAction)));

        System.out.println(RULER);
    }

    private void printCoordActions(List<CoordinatorAction> actions) {
        if (actions != null && actions.size() > 0) {
            System.out.println("Action ID" + OozieClientConstant.VERBOSE_DELIMITER + "Nominal Time");
            System.out.println(RULER);
            for (CoordinatorAction action : actions) {
                System.out.println(maskIfNull(action.getId()) + OozieClientConstant.VERBOSE_DELIMITER
                        + maskDate(action.getNominalTime(), null,false));
            }
        }
        else {
            System.out.println("No Actions match your criteria!");
        }
    }

    private void printCoordActionsStatus(List<CoordinatorAction> actions) {
        if (actions != null && actions.size() > 0) {
            System.out.println("Action ID" + OozieClientConstant.VERBOSE_DELIMITER + "Nominal Time" + OozieClientConstant.VERBOSE_DELIMITER + "Status");
            System.out.println(RULER);
            for (CoordinatorAction action : actions) {
                System.out.println(maskIfNull(action.getId()) + OozieClientConstant.VERBOSE_DELIMITER
                        + maskDate(action.getNominalTime(), null, false) + OozieClientConstant.VERBOSE_DELIMITER
                        + maskIfNull(action.getStatus().name()));
            }
        }
    }

    @VisibleForTesting
    void printWorkflowAction(WorkflowAction action, String timeZoneId, boolean verbose) {

        System.out.println("ID : " + maskIfNull(action.getId()));

        System.out.println(RULER);

        System.out.println("Console URL       : " + maskIfNull(action.getConsoleUrl()));
        System.out.println("Error Code        : " + maskIfNull(action.getErrorCode()));
        System.out.println("Error Message     : " + maskIfNull(action.getErrorMessage()));
        System.out.println("External ID       : " + maskIfNull(action.getExternalId()));
        System.out.println("External Status   : " + maskIfNull(action.getExternalStatus()));
        System.out.println("Name              : " + maskIfNull(action.getName()));
        System.out.println("Retries           : " + action.getUserRetryCount());
        System.out.println("Tracker URI       : " + maskIfNull(action.getTrackerUri()));
        System.out.println("Type              : " + maskIfNull(action.getType()));
        System.out.println("Started           : " + maskDate(action.getStartTime(), timeZoneId, verbose));
        System.out.println("Status            : " + action.getStatus());
        System.out.println("Ended             : " + maskDate(action.getEndTime(), timeZoneId, verbose));

        if (verbose) {
            System.out.println("External Stats    : " + action.getStats());
            System.out.println("External ChildIDs : " + action.getExternalChildIDs());
        }

        System.out.println(RULER);
    }

    void printWorkflowActionRetries(List<Map<String, String>> retries, String actionId) {
        System.out.println("ID : " + maskIfNull(actionId));
        if (retries.isEmpty()) {
            System.out.println("No Retries");
        }
        for (Map<String, String> retry: retries) {
            System.out.println(RULER);
            System.out.println("Attempt        : " + retry.get(JsonTags.ACTION_ATTEMPT));
            System.out.println("Start Time     : " + retry.get(JsonTags.WORKFLOW_ACTION_START_TIME));
            System.out.println("End Time       : " + retry.get(JsonTags.WORKFLOW_ACTION_END_TIME));
            if (null != retry.get(JsonTags.WORKFLOW_ACTION_CONSOLE_URL)) {
                System.out.println("Console URL    : " + retry.get(JsonTags.WORKFLOW_ACTION_CONSOLE_URL));
            }
            if (null != retry.get(JsonTags.WORKFLOW_ACTION_EXTERNAL_CHILD_IDS)) {
                System.out.println("Child URL      : " + retry.get(JsonTags.WORKFLOW_ACTION_EXTERNAL_CHILD_IDS));
            }
        }
        System.out.println(RULER);
    }

    private static final String WORKFLOW_JOBS_FORMATTER = "%-41s%-13s%-10s%-10s%-10s%-24s%-24s";
    private static final String COORD_JOBS_FORMATTER = "%-41s%-15s%-10s%-5s%-13s%-24s%-24s";
    private static final String BUNDLE_JOBS_FORMATTER = "%-41s%-15s%-10s%-20s%-20s%-13s%-13s";
    private static final String BUNDLE_COORD_JOBS_FORMATTER = "%-41s%-15s%-5s%-13s%-24s%-24s";

    private static final String WORKFLOW_ACTION_FORMATTER = "%-78s%-10s%-23s%-11s%-10s";
    private static final String COORD_ACTION_FORMATTER = "%-43s%-10s%-37s%-10s%-21s%-21s";
    private static final String BULK_RESPONSE_FORMATTER = "%-13s%-38s%-13s%-41s%-10s%-38s%-21s%-38s";

    @VisibleForTesting
    void printJob(WorkflowJob job, String timeZoneId, boolean verbose) throws IOException {
        System.out.println("Job ID : " + maskIfNull(job.getId()));

        System.out.println(RULER);

        System.out.println("Workflow Name : " + maskIfNull(job.getAppName()));
        System.out.println("App Path      : " + maskIfNull(job.getAppPath()));
        System.out.println("Status        : " + job.getStatus());
        System.out.println("Run           : " + job.getRun());
        System.out.println("User          : " + maskIfNull(job.getUser()));
        System.out.println("Group         : " + maskIfNull(job.getGroup()));
        System.out.println("Created       : " + maskDate(job.getCreatedTime(), timeZoneId, verbose));
        System.out.println("Started       : " + maskDate(job.getStartTime(), timeZoneId, verbose));
        System.out.println("Last Modified : " + maskDate(job.getLastModifiedTime(), timeZoneId, verbose));
        System.out.println("Ended         : " + maskDate(job.getEndTime(), timeZoneId, verbose));
        System.out.println("CoordAction ID: " + maskIfNull(job.getParentId()));

        List<WorkflowAction> actions = job.getActions();

        if (actions != null && actions.size() > 0) {
            System.out.println();
            System.out.println("Actions");
            System.out.println(RULER);

            if (verbose) {
                System.out.println("ID" + OozieClientConstant.VERBOSE_DELIMITER + "Console URL" + OozieClientConstant.VERBOSE_DELIMITER + "Error Code"
                        + OozieClientConstant.VERBOSE_DELIMITER + "Error Message" + OozieClientConstant.VERBOSE_DELIMITER + "External ID" + OozieClientConstant.VERBOSE_DELIMITER
                        + "External Status" + OozieClientConstant.VERBOSE_DELIMITER + "Name" + OozieClientConstant.VERBOSE_DELIMITER + "Retries"
                        + OozieClientConstant.VERBOSE_DELIMITER + "Tracker URI" + OozieClientConstant.VERBOSE_DELIMITER + "Type" + OozieClientConstant.VERBOSE_DELIMITER
                        + "Started" + OozieClientConstant.VERBOSE_DELIMITER + "Status" + OozieClientConstant.VERBOSE_DELIMITER + "Ended");
                System.out.println(RULER);

                for (WorkflowAction action : job.getActions()) {
                    System.out.println(maskIfNull(action.getId()) + OozieClientConstant.VERBOSE_DELIMITER
                            + maskIfNull(action.getConsoleUrl()) + OozieClientConstant.VERBOSE_DELIMITER
                            + maskIfNull(action.getErrorCode()) + OozieClientConstant.VERBOSE_DELIMITER
                            + maskIfNull(action.getErrorMessage()) + OozieClientConstant.VERBOSE_DELIMITER
                            + maskIfNull(action.getExternalId()) + OozieClientConstant.VERBOSE_DELIMITER
                            + maskIfNull(action.getExternalStatus()) + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(action.getName())
                            + OozieClientConstant.VERBOSE_DELIMITER + action.getRetries() + OozieClientConstant.VERBOSE_DELIMITER
                            + maskIfNull(action.getTrackerUri()) + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(action.getType())
                            + OozieClientConstant.VERBOSE_DELIMITER + maskDate(action.getStartTime(), timeZoneId, verbose)
                            + OozieClientConstant.VERBOSE_DELIMITER + action.getStatus() + OozieClientConstant.VERBOSE_DELIMITER
                            + maskDate(action.getEndTime(), timeZoneId, verbose));

                    System.out.println(RULER);
                }
            }
            else {
                System.out.println(String.format(WORKFLOW_ACTION_FORMATTER, "ID", "Status", "Ext ID", "Ext Status",
                        "Err Code"));

                System.out.println(RULER);

                for (WorkflowAction action : job.getActions()) {
                    System.out.println(String.format(WORKFLOW_ACTION_FORMATTER, maskIfNull(action.getId()), action
                            .getStatus(), maskIfNull(action.getExternalId()), maskIfNull(action.getExternalStatus()),
                            maskIfNull(action.getErrorCode())));

                    System.out.println(RULER);
                }
            }
        }
        else {
            System.out.println(RULER);
        }

        System.out.println();
    }

    private void jobsCommand(CommandLine commandLine) throws IOException, OozieCLIException {
        XOozieClient wc = createXOozieClient(commandLine);

        List<String> options = new ArrayList<>();
        for (Option option : commandLine.getOptions()) {
            options.add(option.getOpt());
        }

        String filter = commandLine.getOptionValue(OozieClientConstant.FILTER_OPTION);
        String s = commandLine.getOptionValue(OozieClientConstant.OFFSET_OPTION);
        int start = Integer.parseInt((s != null) ? s : "0");
        s = commandLine.getOptionValue(OozieClientConstant.LEN_OPTION);
        String jobtype = commandLine.getOptionValue(OozieClientConstant.JOBTYPE_OPTION);
        String timeZoneId = getTimeZoneId(commandLine);
        jobtype = (jobtype != null) ? jobtype : "wf";
        int len = Integer.parseInt((s != null) ? s : "0");
        String bulkFilterString = commandLine.getOptionValue(OozieClientConstant.BULK_OPTION);

        try {
            if (options.contains(OozieClientConstant.KILL_OPTION)) {
                printBulkModifiedJobs(wc.killJobs(filter, jobtype, start, len), timeZoneId, "killed");
            }
            else if (options.contains(OozieClientConstant.SUSPEND_OPTION)) {
                printBulkModifiedJobs(wc.suspendJobs(filter, jobtype, start, len), timeZoneId, "suspended");
            }
            else if (options.contains(OozieClientConstant.RESUME_OPTION)) {
                printBulkModifiedJobs(wc.resumeJobs(filter, jobtype, start, len), timeZoneId, "resumed");
            }
            else if (bulkFilterString != null) {
                printBulkJobs(wc.getBulkInfo(bulkFilterString, start, len), timeZoneId, commandLine.hasOption(OozieClientConstant.VERBOSE_OPTION));
            }
            else if (jobtype.toLowerCase().contains("wf")) {
                printJobs(wc.getJobsInfo(filter, start, len), timeZoneId, commandLine.hasOption(OozieClientConstant.VERBOSE_OPTION));
            }
            else if (jobtype.toLowerCase().startsWith("coord")) {
                printCoordJobs(wc.getCoordJobsInfo(filter, start, len), timeZoneId, commandLine.hasOption(OozieClientConstant.VERBOSE_OPTION));
            }
            else if (jobtype.toLowerCase().startsWith("bundle")) {
                printBundleJobs(wc.getBundleJobsInfo(filter, start, len), timeZoneId, commandLine.hasOption(OozieClientConstant.VERBOSE_OPTION));
            }

        }
        catch (OozieClientException ex) {
            throw new OozieCLIException(ex.toString(), ex);
        }
    }

    @VisibleForTesting
    void printBulkModifiedJobs(JSONObject json, String timeZoneId, String action) throws IOException {
        if (json.containsKey(JsonTags.WORKFLOWS_JOBS)) {
            JSONArray workflows = (JSONArray) json.get(JsonTags.WORKFLOWS_JOBS);
            if (workflows == null) {
                workflows = new JSONArray();
            }
            List<WorkflowJob> wfs = JsonToBean.createWorkflowJobList(workflows);
            if (wfs.isEmpty()) {
                System.out.println("bulk modify command did not modify any jobs");
            }
            else {
                System.out.println("the following jobs have been " + action);
                printJobs(wfs, timeZoneId, false);
            }
        }
        else if (json.containsKey(JsonTags.COORDINATOR_JOBS)) {
            JSONArray coordinators = (JSONArray) json.get(JsonTags.COORDINATOR_JOBS);
            if (coordinators == null) {
                coordinators = new JSONArray();
            }
            List<CoordinatorJob> coords = JsonToBean.createCoordinatorJobList(coordinators);
            if (coords.isEmpty()) {
                System.out.println("bulk modify command did not modify any jobs");
            }
            else {
                System.out.println("the following jobs have been " + action);
                printCoordJobs(coords, timeZoneId, false);
            }
        }
        else {
            JSONArray bundles = (JSONArray) json.get(JsonTags.BUNDLE_JOBS);
            if (bundles == null) {
                bundles = new JSONArray();
            }
            List<BundleJob> bundleJobs = JsonToBean.createBundleJobList(bundles);
            if (bundleJobs.isEmpty()) {
                System.out.println("bulk modify command did not modify any jobs");
            }
            else {
                System.out.println("the following jobs have been " + action);
                printBundleJobs(bundleJobs, timeZoneId, false);
            }
        }
    }

    @VisibleForTesting
    void printCoordJobs(List<CoordinatorJob> jobs, String timeZoneId, boolean verbose) throws IOException {
        if (jobs != null && jobs.size() > 0) {
            if (verbose) {
                System.out.println("Job ID" + OozieClientConstant.VERBOSE_DELIMITER + "App Name" + OozieClientConstant.VERBOSE_DELIMITER + "App Path"
                        + OozieClientConstant.VERBOSE_DELIMITER + "Console URL" + OozieClientConstant.VERBOSE_DELIMITER + "User" + OozieClientConstant.VERBOSE_DELIMITER + "Group"
                        + OozieClientConstant.VERBOSE_DELIMITER + "Concurrency" + OozieClientConstant.VERBOSE_DELIMITER + "Frequency" + OozieClientConstant.VERBOSE_DELIMITER
                        + "Time Unit" + OozieClientConstant.VERBOSE_DELIMITER + "Time Zone" + OozieClientConstant.VERBOSE_DELIMITER + "Time Out"
                        + OozieClientConstant.VERBOSE_DELIMITER + "Started" + OozieClientConstant.VERBOSE_DELIMITER + "Next Materialize" + OozieClientConstant.VERBOSE_DELIMITER
                        + "Status" + OozieClientConstant.VERBOSE_DELIMITER + "Last Action" + OozieClientConstant.VERBOSE_DELIMITER + "Ended");
                System.out.println(RULER);

                for (CoordinatorJob job : jobs) {
                    System.out.println(maskIfNull(job.getId()) + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(job.getAppName())
                            + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(job.getAppPath()) + OozieClientConstant.VERBOSE_DELIMITER
                            + maskIfNull(job.getConsoleUrl()) + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(job.getUser())
                            + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(job.getGroup()) + OozieClientConstant.VERBOSE_DELIMITER + job.getConcurrency()
                            + OozieClientConstant.VERBOSE_DELIMITER + job.getFrequency() + OozieClientConstant.VERBOSE_DELIMITER + job.getTimeUnit()
                            + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(job.getTimeZone()) + OozieClientConstant.VERBOSE_DELIMITER + job.getTimeout()
                            + OozieClientConstant.VERBOSE_DELIMITER + maskDate(job.getStartTime(), timeZoneId, verbose) + OozieClientConstant.VERBOSE_DELIMITER
                            + maskDate(job.getNextMaterializedTime(), timeZoneId, verbose) + OozieClientConstant.VERBOSE_DELIMITER
                            + job.getStatus() + OozieClientConstant.VERBOSE_DELIMITER
                            + maskDate(job.getLastActionTime(), timeZoneId, verbose) + OozieClientConstant.VERBOSE_DELIMITER
                            + maskDate(job.getEndTime(), timeZoneId, verbose));

                    System.out.println(RULER);
                }
            }
            else {
                System.out.println(String.format(COORD_JOBS_FORMATTER, "Job ID", "App Name", "Status", "Freq", "Unit",
                        "Started", "Next Materialized"));
                System.out.println(RULER);

                for (CoordinatorJob job : jobs) {
                    System.out.println(String.format(COORD_JOBS_FORMATTER, maskIfNull(job.getId()), maskIfNull(job
                            .getAppName()), job.getStatus(), job.getFrequency(), job.getTimeUnit(), maskDate(job
                            .getStartTime(), timeZoneId, verbose), maskDate(job.getNextMaterializedTime(), timeZoneId, verbose)));

                    System.out.println(RULER);
                }
            }
        }
        else {
            System.out.println("No Jobs match your criteria!");
        }
    }

    @VisibleForTesting
    void printBulkJobs(List<BulkResponse> jobs, String timeZoneId, boolean verbose) throws IOException {
        if (jobs != null && jobs.size() > 0) {
            for (BulkResponse response : jobs) {
                BundleJob bundle = response.getBundle();
                CoordinatorJob coord = response.getCoordinator();
                CoordinatorAction action = response.getAction();
                if (verbose) {
                    System.out.println();
                    System.out.println("Bundle Name : " + maskIfNull(bundle.getAppName()));

                    System.out.println(RULER);

                    System.out.println("Bundle ID        : " + maskIfNull(bundle.getId()));
                    System.out.println("Coordinator Name : " + maskIfNull(coord.getAppName()));
                    System.out.println("Coord Action ID  : " + maskIfNull(action.getId()));
                    System.out.println("Action Status    : " + action.getStatus());
                    System.out.println("External ID      : " + maskIfNull(action.getExternalId()));
                    System.out.println("Created Time     : " + maskDate(action.getCreatedTime(), timeZoneId, false));
                    System.out.println("User             : " + maskIfNull(bundle.getUser()));
                    System.out.println("Error Message    : " + maskIfNull(action.getErrorMessage()));
                    System.out.println(RULER);
                }
                else {
                    System.out.println(String.format(BULK_RESPONSE_FORMATTER, "Bundle Name", "Bundle ID", "Coord Name",
                            "Coord Action ID", "Status", "External ID", "Created Time", "Error Message"));
                    System.out.println(RULER);
                    System.out
                            .println(String.format(BULK_RESPONSE_FORMATTER, maskIfNull(bundle.getAppName()),
                                    maskIfNull(bundle.getId()), maskIfNull(coord.getAppName()),
                                    maskIfNull(action.getId()), action.getStatus(), maskIfNull(action.getExternalId()),
                                    maskDate(action.getCreatedTime(), timeZoneId, false),
                                    maskIfNull(action.getErrorMessage())));
                    System.out.println(RULER);
                }
            }
        }
        else {
            System.out.println("Bulk request criteria did not match any coordinator actions");
        }
    }

    @VisibleForTesting
    void printBundleJobs(List<BundleJob> jobs, String timeZoneId, boolean verbose) throws IOException {
        if (jobs != null && jobs.size() > 0) {
            if (verbose) {
                System.out.println("Job ID" + OozieClientConstant.VERBOSE_DELIMITER + "Bundle Name" + OozieClientConstant.VERBOSE_DELIMITER + "Bundle Path"
                        + OozieClientConstant.VERBOSE_DELIMITER + "User" + OozieClientConstant.VERBOSE_DELIMITER + "Group" + OozieClientConstant.VERBOSE_DELIMITER + "Status"
                        + OozieClientConstant.VERBOSE_DELIMITER + "Kickoff" + OozieClientConstant.VERBOSE_DELIMITER + "Pause" + OozieClientConstant.VERBOSE_DELIMITER + "Created"
                        + OozieClientConstant.VERBOSE_DELIMITER + "Console URL");
                System.out.println(RULER);

                for (BundleJob job : jobs) {
                    System.out.println(maskIfNull(job.getId()) + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(job.getAppName())
                            + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(job.getAppPath()) + OozieClientConstant.VERBOSE_DELIMITER
                            + maskIfNull(job.getUser()) + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(job.getGroup())
                            + OozieClientConstant.VERBOSE_DELIMITER + job.getStatus() + OozieClientConstant.VERBOSE_DELIMITER
                            + maskDate(job.getKickoffTime(), timeZoneId, verbose) + OozieClientConstant.VERBOSE_DELIMITER
                            + maskDate(job.getPauseTime(), timeZoneId, verbose) + OozieClientConstant.VERBOSE_DELIMITER
                            + maskDate(job.getCreatedTime(), timeZoneId, verbose) + OozieClientConstant.VERBOSE_DELIMITER
                            + maskIfNull(job.getConsoleUrl()));

                    System.out.println(RULER);
                }
            }
            else {
                System.out.println(String.format(BUNDLE_JOBS_FORMATTER, "Job ID", "Bundle Name", "Status", "Kickoff",
                        "Created", "User", "Group"));
                System.out.println(RULER);

                for (BundleJob job : jobs) {
                    System.out.println(String.format(BUNDLE_JOBS_FORMATTER, maskIfNull(job.getId()),
                            maskIfNull(job.getAppName()), job.getStatus(),
                            maskDate(job.getKickoffTime(), timeZoneId, verbose),
                            maskDate(job.getCreatedTime(), timeZoneId, verbose), maskIfNull(job.getUser()),
                            maskIfNull(job.getGroup())));
                    System.out.println(RULER);
                }
            }
        }
        else {
            System.out.println("No Jobs match your criteria!");
        }
    }

    private void slaCommand(CommandLine commandLine) throws IOException, OozieCLIException {
        XOozieClient wc = createXOozieClient(commandLine);

        String s = commandLine.getOptionValue(OozieClientConstant.OFFSET_OPTION);
        int start = Integer.parseInt((s != null) ? s : "0");
        s = commandLine.getOptionValue(OozieClientConstant.LEN_OPTION);
        int len = Integer.parseInt((s != null) ? s : "100");
        String filter = commandLine.getOptionValue(OozieClientConstant.FILTER_OPTION);

        try {
            wc.getSlaInfo(start, len, filter);
        }
        catch (OozieClientException ex) {
            throw new OozieCLIException(ex.toString(), ex);
        }
    }

    private void adminCommand(CommandLine commandLine) throws OozieCLIException {
        XOozieClient wc = createXOozieClient(commandLine);

        List<String> options = new ArrayList<>();
        for (Option option : commandLine.getOptions()) {
            options.add(option.getOpt());
        }

        try {
            SYSTEM_MODE status;
            if (options.contains(OozieClientConstant.VERSION_OPTION)) {
                System.out.println("Oozie server build version: " + wc.getServerBuildVersion());
            }
            else if (options.contains(OozieClientConstant.SYSTEM_MODE_OPTION)) {
                String systemModeOption = commandLine.getOptionValue(OozieClientConstant.SYSTEM_MODE_OPTION).toUpperCase();
                try {
                    status = SYSTEM_MODE.valueOf(systemModeOption);
                }
                catch (Exception e) {
                    throw new OozieCLIException("Invalid input provided for option: " + OozieClientConstant.SYSTEM_MODE_OPTION
                            + " value given :" + systemModeOption
                            + " Expected values are: NORMAL/NOWEBSERVICE/SAFEMODE ");
                }
                wc.setSystemMode(status);
                System.out.println("System mode: " + status);
            }
            else if (options.contains(OozieClientConstant.STATUS_OPTION)) {
                status = wc.getSystemMode();
                System.out.println("System mode: " + status);
            }

            else if (options.contains(OozieClientConstant.UPDATE_SHARELIB_OPTION)) {
                System.out.println(wc.updateShareLib());
            }

            else if (options.contains(OozieClientConstant.LIST_SHARELIB_LIB_OPTION)) {
                String sharelibKey = null;
                if (commandLine.getArgList().size() > 0) {
                    sharelibKey = (String) commandLine.getArgList().get(0);
                }
                System.out.println(wc.listShareLib(sharelibKey));
            }

            else if (options.contains(OozieClientConstant.QUEUE_DUMP_OPTION)) {

                List<String> list = wc.getQueueDump();
                if (list != null && list.size() != 0) {
                    for (String str : list) {
                        System.out.println(str);
                    }
                }
                else {
                    System.out.println("QueueDump is null!");
                }
            }
            else if (options.contains(OozieClientConstant.AVAILABLE_SERVERS_OPTION)) {
                Map<String, String> availableOozieServers = new TreeMap<>(wc.getAvailableOozieServers());
                for (Map.Entry<String, String> ent : availableOozieServers.entrySet()) {
                    System.out.println(ent.getKey() + " : " + ent.getValue());
                }
            } else if (options.contains(OozieClientConstant.SERVER_CONFIGURATION_OPTION)) {
                Map<String, String> serverConfig = new TreeMap<>(wc.getServerConfiguration());
                for (Map.Entry<String, String> ent : serverConfig.entrySet()) {
                    System.out.println(ent.getKey() + " : " + ent.getValue());
                }
            } else if (options.contains(OozieClientConstant.SERVER_OS_ENV_OPTION)) {
                Map<String, String> osEnv = new TreeMap<>(wc.getOSEnv());
                for (Map.Entry<String, String> ent : osEnv.entrySet()) {
                    System.out.println(ent.getKey() + " : " + ent.getValue());
                }
            } else if (options.contains(OozieClientConstant.SERVER_JAVA_SYSTEM_PROPERTIES_OPTION)) {
                Map<String, String> javaSysProps = new TreeMap<>(wc.getJavaSystemProperties());
                for (Map.Entry<String, String> ent : javaSysProps.entrySet()) {
                    System.out.println(ent.getKey() + " : " + ent.getValue());
                }
            } else if (options.contains(OozieClientConstant.METRICS_OPTION)) {
                OozieClient.Metrics metrics = wc.getMetrics();
                if (metrics == null) {
                    System.out.println("Metrics are unavailable.  Try Instrumentation (-" + OozieClientConstant.INSTRUMENTATION_OPTION + ") instead");
                } else {
                    printMetrics(metrics);
                }
            } else if (options.contains(OozieClientConstant.INSTRUMENTATION_OPTION)) {
                OozieClient.Instrumentation instrumentation = wc.getInstrumentation();
                if (instrumentation == null) {
                    System.out.println("Instrumentation is unavailable.  Try Metrics (-" + OozieClientConstant.METRICS_OPTION + ") instead");
                } else {
                    printInstrumentation(instrumentation);
                }
            } else if (options.contains(OozieClientConstant.PURGE_OPTION)) {
                String purgeOptions = commandLine.getOptionValue(OozieClientConstant.PURGE_OPTION);
                System.out.println(wc.purgeCommand(purgeOptions));
            }
        }
        catch (OozieClientException ex) {
            throw new OozieCLIException(ex.toString(), ex);
        }
    }

    private void versionCommand() throws OozieCLIException {
        StringBuilder sb = new StringBuilder();
        sb.append("Oozie client build version: ")
            .append(BuildInfo.getBuildInfo().getProperty(BuildInfo.BUILD_VERSION))
            .append("\nSource code repository: ")
            .append(BuildInfo.getBuildInfo().getProperty(BuildInfo.BUILD_VC_URL))
            .append("\nCompiled by ")
            .append(BuildInfo.getBuildInfo().getProperty(BuildInfo.BUILD_USER_NAME))
            .append(" on ")
            .append(BuildInfo.getBuildInfo().getProperty(BuildInfo.BUILD_TIME))
            .append("\nFrom source with checksum: ")
            .append(BuildInfo.getBuildInfo().getProperty(BuildInfo.BUILD_VC_REVISION));
        System.out.println(sb.toString());
    }

    @VisibleForTesting
    void printJobs(List<WorkflowJob> jobs, String timeZoneId, boolean verbose) throws IOException {
        if (jobs != null && jobs.size() > 0) {
            if (verbose) {
                System.out.println("Job ID" + OozieClientConstant.VERBOSE_DELIMITER + "App Name" + OozieClientConstant.VERBOSE_DELIMITER + "App Path"
                        + OozieClientConstant.VERBOSE_DELIMITER + "Console URL" + OozieClientConstant.VERBOSE_DELIMITER + "User" + OozieClientConstant.VERBOSE_DELIMITER + "Group"
                        + OozieClientConstant.VERBOSE_DELIMITER + "Run" + OozieClientConstant.VERBOSE_DELIMITER + "Created" + OozieClientConstant.VERBOSE_DELIMITER + "Started"
                        + OozieClientConstant.VERBOSE_DELIMITER + "Status" + OozieClientConstant.VERBOSE_DELIMITER + "Last Modified" + OozieClientConstant.VERBOSE_DELIMITER
                        + "Ended");
                System.out.println(RULER);

                for (WorkflowJob job : jobs) {
                    System.out.println(maskIfNull(job.getId()) + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(job.getAppName())
                            + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(job.getAppPath()) + OozieClientConstant.VERBOSE_DELIMITER
                            + maskIfNull(job.getConsoleUrl()) + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(job.getUser())
                            + OozieClientConstant.VERBOSE_DELIMITER + maskIfNull(job.getGroup()) + OozieClientConstant.VERBOSE_DELIMITER + job.getRun()
                            + OozieClientConstant.VERBOSE_DELIMITER + maskDate(job.getCreatedTime(), timeZoneId, verbose)
                            + OozieClientConstant.VERBOSE_DELIMITER + maskDate(job.getStartTime(), timeZoneId, verbose) + OozieClientConstant.VERBOSE_DELIMITER
                            + job.getStatus() + OozieClientConstant.VERBOSE_DELIMITER
                            + maskDate(job.getLastModifiedTime(), timeZoneId, verbose) + OozieClientConstant.VERBOSE_DELIMITER
                            + maskDate(job.getEndTime(), timeZoneId, verbose));

                    System.out.println(RULER);
                }
            }
            else {
                System.out.println(String.format(WORKFLOW_JOBS_FORMATTER, "Job ID", "App Name", "Status", "User",
                        "Group", "Started", "Ended"));
                System.out.println(RULER);

                for (WorkflowJob job : jobs) {
                    System.out.println(String.format(WORKFLOW_JOBS_FORMATTER, maskIfNull(job.getId()),
                            maskIfNull(job.getAppName()), job.getStatus(), maskIfNull(job.getUser()),
                            maskIfNull(job.getGroup()), maskDate(job.getStartTime(), timeZoneId, verbose),
                            maskDate(job.getEndTime(), timeZoneId, verbose)));

                    System.out.println(RULER);
                }
            }
        }
        else {
            System.out.println("No Jobs match your criteria!");
        }
    }

    void printWfsForCoordAction(List<WorkflowJob> jobs, String timeZoneId) throws IOException {
        if (jobs != null && jobs.size() > 0) {
            System.out.println(String.format("%-41s%-10s%-24s%-24s", "Job ID", "Status", "Started", "Ended"));
            System.out.println(RULER);

            for (WorkflowJob job : jobs) {
                System.out
                        .println(String.format("%-41s%-10s%-24s%-24s", maskIfNull(job.getId()), job.getStatus(),
                                maskDate(job.getStartTime(), timeZoneId, false),
                                maskDate(job.getEndTime(), timeZoneId, false)));
                System.out.println(RULER);
            }
        }
    }

    private String maskIfNull(String value) {
        if (value != null && value.length() > 0) {
            return value;
        }
        return "-";
    }

    private String maskDate(Date date, String timeZoneId, boolean verbose) {
        if (date == null) {
            return "-";
        }

        SimpleDateFormat dateFormater;
        if (verbose) {
            dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz", Locale.US);
        }
        else {
            dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm zzz", Locale.US);
        }

        if (timeZoneId != null) {
            dateFormater.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        }
        String dateString = dateFormater.format(date);
        // Most TimeZones are 3 or 4 characters; GMT offsets (e.g. GMT-07:00) are 9, so lets remove the "GMT" part to make it 6
        // to fit better
        Matcher m = GMT_OFFSET_SHORTEN_PATTERN.matcher(dateString);
        if (m.matches() && m.groupCount() == 2) {
            dateString = m.group(1) + m.group(2);
        }
        return dateString;
    }

    private void validateCommand(CommandLine commandLine) throws OozieCLIException {
        String[] args = commandLine.getArgs();
        if (args.length != 1) {
            throw new OozieCLIException("One file must be specified");
        }
        try {
            XOozieClient wc = createXOozieClient(commandLine);
            String result = wc.validateXML(args[0]);
            if (result == null) {
                return;
            }
            System.out.println(result);
        } catch (OozieClientException e) {
            throw new OozieCLIException(e.getMessage(), e);
        }
    }

    private void scriptLanguageCommand(CommandLine commandLine, String jobType) throws IOException, OozieCLIException {
        List<String> args = commandLine.getArgList();
        if (args.size() > 0) {
            // checking if args starts with -X (because CLIParser cannot check this)
            if (!args.get(0).equals("-X")) {
                throw new OozieCLIException("Unrecognized option: " + args.get(0) + " Expecting -X");
            }
            args.remove(0);
        }

        if (!commandLine.hasOption(OozieClientConstant.SCRIPTFILE_OPTION)) {
            throw new OozieCLIException("Need to specify -file <scriptfile>");
        }

        if (!commandLine.hasOption(OozieClientConstant.CONFIG_OPTION)) {
            throw new OozieCLIException("Need to specify -config <configfile>");
        }

        try {
            XOozieClient wc = createXOozieClient(commandLine);
            Properties conf = getConfiguration(wc, commandLine);
            String script = commandLine.getOptionValue(OozieClientConstant.SCRIPTFILE_OPTION);
            List<String> paramsList = new ArrayList<>();
            if (commandLine.hasOption("P")) {
                Properties params = commandLine.getOptionProperties("P");
                for (String key : params.stringPropertyNames()) {
                    paramsList.add(key + "=" + params.getProperty(key));
                }
            }
            System.out.println(JOB_ID_PREFIX + wc.submitScriptLanguage(conf, script, args.toArray(new String[args.size()]),
                    paramsList.toArray(new String[paramsList.size()]), jobType));
        }
        catch (OozieClientException ex) {
            throw new OozieCLIException(ex.toString(), ex);
        }
    }

    private void sqoopCommand(CommandLine commandLine) throws IOException, OozieCLIException {
        List<String> args = commandLine.getArgList();
        if (args.size() > 0) {
            // checking if args starts with -X (because CLIParser cannot check this)
            if (!args.get(0).equals("-X")) {
                throw new OozieCLIException("Unrecognized option: " + args.get(0) + " Expecting -X");
            }
            args.remove(0);
        }

        if (!commandLine.hasOption(OozieClientConstant.SQOOP_COMMAND_OPTION)) {
            throw new OozieCLIException("Need to specify -command");
        }

        if (!commandLine.hasOption(OozieClientConstant.CONFIG_OPTION)) {
            throw new OozieCLIException("Need to specify -config <configfile>");
        }

        try {
            XOozieClient wc = createXOozieClient(commandLine);
            Properties conf = getConfiguration(wc, commandLine);
            String[] command = commandLine.getOptionValues(OozieClientConstant.SQOOP_COMMAND_OPTION);
            System.out.println(JOB_ID_PREFIX + wc.submitSqoop(conf, command, args.toArray(new String[args.size()])));
        }
        catch (OozieClientException ex) {
            throw new OozieCLIException(ex.toString(), ex);
        }
    }

    private void infoCommand(CommandLine commandLine) throws OozieCLIException {
        for (Option option : commandLine.getOptions()) {
            String opt = option.getOpt();
            if (opt.equals(OozieClientConstant.INFO_TIME_ZONES_OPTION)) {
                printAvailableTimeZones();
            }
        }
    }

    private void printAvailableTimeZones() {
        System.out.println("The format is \"SHORT_NAME (ID)\"\nGive the ID (GMT, UTC or Region/City)" +
        "to the -timezone argument");
        System.out.println("GMT offsets can also be used (e.g. GMT-07:00, GMT-0700, GMT+05:30, GMT+0530)");
        System.out.println("Available Time Zones:");
        for (String tzId : TimeZone.getAvailableIDs()) {
            // skip id's that are like "Etc/GMT+01:00" because their display names are like "GMT-01:00", which is confusing
            if (!tzId.startsWith("Etc/GMT")) {
                TimeZone tZone = TimeZone.getTimeZone(tzId);
                System.out.println("      " + tZone.getDisplayName(false, TimeZone.SHORT) + " (" + tzId + ")");
            }
        }
    }


    private void mrCommand(CommandLine commandLine) throws IOException, OozieCLIException {
        try {
            XOozieClient wc = createXOozieClient(commandLine);
            Properties conf = getConfiguration(wc, commandLine);

            String mapper = conf.getProperty(MAPRED_MAPPER, conf.getProperty(MAPRED_MAPPER_2));
            if (mapper == null) {
                throw new OozieCLIException("mapper (" + MAPRED_MAPPER + " or " + MAPRED_MAPPER_2 + ") must be specified in conf");
            }

            String reducer = conf.getProperty(MAPRED_REDUCER, conf.getProperty(MAPRED_REDUCER_2));
            if (reducer == null) {
                throw new OozieCLIException("reducer (" + MAPRED_REDUCER + " or " + MAPRED_REDUCER_2
                        + ") must be specified in conf");
            }

            String inputDir = conf.getProperty(MAPRED_INPUT);
            if (inputDir == null) {
                throw new OozieCLIException("input dir (" + MAPRED_INPUT +") must be specified in conf");
            }

            String outputDir = conf.getProperty(MAPRED_OUTPUT);
            if (outputDir == null) {
                throw new OozieCLIException("output dir (" + MAPRED_OUTPUT +") must be specified in conf");
            }

            System.out.println(JOB_ID_PREFIX + wc.submitMapReduce(conf));
        }
        catch (OozieClientException ex) {
            throw new OozieCLIException(ex.toString(), ex);
        }
    }

    private String getFirstMissingDependencies(CoordinatorAction action) {
        StringBuilder allDeps = new StringBuilder();
        String missingDep = action.getMissingDependencies();
        boolean depExists = false;
        if (missingDep != null && !missingDep.isEmpty()) {
            allDeps.append(missingDep.split(INSTANCE_SEPARATOR)[0]);
            depExists = true;
        }
        String pushDeps = action.getPushMissingDependencies();
        if (pushDeps != null && !pushDeps.isEmpty()) {
            if(depExists) {
                allDeps.append(INSTANCE_SEPARATOR);
            }
            allDeps.append(pushDeps.split(INSTANCE_SEPARATOR)[0]);
        }
        return allDeps.toString();
    }

    private void slaAlertCommand(String jobIds, OozieClient wc, CommandLine commandLine, List<String> options)
            throws OozieCLIException, OozieClientException {
        String actions = null, coordinators = null, dates = null;

        if (options.contains(OozieClientConstant.ACTION_OPTION)) {
            actions = commandLine.getOptionValue(OozieClientConstant.ACTION_OPTION);
        }

        if (options.contains(OozieClientConstant.DATE_OPTION)) {
            dates = commandLine.getOptionValue(OozieClientConstant.DATE_OPTION);
        }

        if (options.contains(OozieClientConstant.COORD_OPTION)) {
            coordinators = commandLine.getOptionValue(OozieClientConstant.COORD_OPTION);
            if (coordinators == null) {
                throw new OozieCLIException("No value specified for -coordinator option");
            }
        }

        if (options.contains(OozieClientConstant.SLA_ENABLE_ALERT)) {
            wc.slaEnableAlert(jobIds, actions, dates, coordinators);
        }
        else if (options.contains(OozieClientConstant.SLA_DISABLE_ALERT)) {
            wc.slaDisableAlert(jobIds, actions, dates, coordinators);
        }
        else if (options.contains(OozieClientConstant.SLA_CHANGE)) {
            String newSlaParams = commandLine.getOptionValue(OozieClientConstant.CHANGE_VALUE_OPTION);
            wc.slaChange(jobIds, actions, dates, coordinators, newSlaParams);
        }
    }

    private void printMetrics(OozieClient.Metrics metrics) {
        System.out.println("COUNTERS");
        System.out.println("--------");
        Map<String, Long> counters = new TreeMap<>(metrics.getCounters());
        for (Map.Entry<String, Long> ent : counters.entrySet()) {
            System.out.println(ent.getKey() + " : " + ent.getValue());
        }
        System.out.println("\nGAUGES");
        System.out.println("------");
        Map<String, Object> gauges = new TreeMap<>(metrics.getGauges());
        for (Map.Entry<String, Object> ent : gauges.entrySet()) {
            System.out.println(ent.getKey() + " : " + ent.getValue());
        }
        System.out.println("\nTIMERS");
        System.out.println("------");
        Map<String, OozieClient.Metrics.Timer> timers = new TreeMap<>(metrics.getTimers());
        for (Map.Entry<String, OozieClient.Metrics.Timer> ent : timers.entrySet()) {
            System.out.println(ent.getKey());
            System.out.println(ent.getValue());
        }
        System.out.println("\nHISTOGRAMS");
        System.out.println("----------");
        Map<String, OozieClient.Metrics.Histogram> histograms = new TreeMap<>(metrics.getHistograms());
        for (Map.Entry<String, OozieClient.Metrics.Histogram> ent : histograms.entrySet()) {
            System.out.println(ent.getKey());
            System.out.println(ent.getValue());
        }
    }

    private void printInstrumentation(OozieClient.Instrumentation instrumentation) {
        System.out.println("COUNTERS");
        System.out.println("--------");
        Map<String, Long> counters = new TreeMap<>(instrumentation.getCounters());
        for (Map.Entry<String, Long> ent : counters.entrySet()) {
            System.out.println(ent.getKey() + " : " + ent.getValue());
        }
        System.out.println("\nVARIABLES");
        System.out.println("---------");
        Map<String, Object> variables = new TreeMap<>(instrumentation.getVariables());
        for (Map.Entry<String, Object> ent : variables.entrySet()) {
            System.out.println(ent.getKey() + " : " + ent.getValue());
        }
        System.out.println("\nSAMPLERS");
        System.out.println("---------");
        Map<String, Double> samplers = new TreeMap<>(instrumentation.getSamplers());
        for (Map.Entry<String, Double> ent : samplers.entrySet()) {
            System.out.println(ent.getKey() + " : " + ent.getValue());
        }
        System.out.println("\nTIMERS");
        System.out.println("---------");
        Map<String, OozieClient.Instrumentation.Timer> timers = new TreeMap<>(instrumentation.getTimers());
        for (Map.Entry<String, OozieClient.Instrumentation.Timer> ent : timers.entrySet()) {
            System.out.println(ent.getKey());
            System.out.println(ent.getValue());
        }
    }
}
