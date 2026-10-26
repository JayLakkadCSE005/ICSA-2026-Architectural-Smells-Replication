package org.apache.oozie.sla.service;

public final class SLAServiceConstants {
	
	private SLAServiceConstants() {
        // Prevent instantiation
    }
	public static final String CONF_PREFIX = "oozie.sla.service.SLAService.";
	public static final String CONF_CALCULATOR_IMPL = CONF_PREFIX + "calculator.impl";
	public static final String CONF_CAPACITY = CONF_PREFIX + "capacity";
	public static final String CONF_ALERT_EVENTS = CONF_PREFIX + "alert.events";
	public static final String CONF_EVENTS_MODIFIED_AFTER = CONF_PREFIX + "events.modified.after";
	public static final String CONF_JOB_EVENT_LATENCY = CONF_PREFIX + "job.event.latency";
	//Time interval, in seconds, at which SLA Worker will be scheduled to run
	public static final String CONF_SLA_CHECK_INTERVAL = CONF_PREFIX + "check.interval";
	public static final String CONF_SLA_CHECK_INITIAL_DELAY = CONF_PREFIX + "check.initial.delay";
	public static final String CONF_SLA_CALC_LOCK_TIMEOUT = CONF_PREFIX + "oozie.sla.calc.default.lock.timeout";
	public static final String CONF_SLA_HISTORY_PURGE_INTERVAL = CONF_PREFIX + "history.purge.interval";
	public static final String CONF_MAXIMUM_RETRY_COUNT = CONF_PREFIX + "maximum.retry.count";

}
