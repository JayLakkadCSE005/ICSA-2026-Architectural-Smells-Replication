package org.apache.oozie.service;

import org.apache.oozie.command.SkipCommitFaultInjection;

public final class JPAServiceConstants {
	
	private JPAServiceConstants() {
		
	}
	
	public static final long DEFAULT_INITIAL_WAIT_TIME = 100;
	public static final long DEFAULT_MAX_WAIT_TIME = 30_000;
	public static final int DEFAULT_MAX_RETRY_COUNT = 1;
	public static final String CONF_DB_SCHEMA = "oozie.db.schema.name";
	public static final String CONF_PREFIX = Service.CONF_PREFIX + "JPAService.";
	public static final String CONF_URL = CONF_PREFIX + "jdbc.url";
	public static final String CONF_DRIVER = CONF_PREFIX + "jdbc.driver";
	public static final String CONF_USERNAME = CONF_PREFIX + "jdbc.username";
	public static final String CONF_PASSWORD = CONF_PREFIX + "jdbc.password";
	public static final String CONF_CONN_DATA_SOURCE = CONF_PREFIX + "connection.data.source";
	public static final String CONF_CONN_PROPERTIES = CONF_PREFIX + "connection.properties";
	public static final String CONF_MAX_ACTIVE_CONN = CONF_PREFIX + "pool.max.active.conn";
	public static final String CONF_CREATE_DB_SCHEMA = CONF_PREFIX + "create.db.schema";
	public static final String CONF_VALIDATE_DB_CONN = CONF_PREFIX + "validate.db.connection";
	public static final String CONF_VALIDATE_DB_CONN_EVICTION_INTERVAL = CONF_PREFIX + "validate.db.connection.eviction.interval";
	public static final String CONF_VALIDATE_DB_CONN_EVICTION_NUM = CONF_PREFIX + "validate.db.connection.eviction.num";
	public static final String CONF_OPENJPA_BROKER_IMPL = CONF_PREFIX + "openjpa.BrokerImpl";
	public static final String INITIAL_WAIT_TIME = CONF_PREFIX + "retry.initial-wait-time.ms";
	public static final String MAX_WAIT_TIME = CONF_PREFIX + "retry.maximum-wait-time.ms";
	public static final String MAX_WAIT_TIME_DEPRECATED = CONF_PREFIX + "maximum-wait-time.ms";
	public static final String MAX_RETRY_COUNT = CONF_PREFIX + "retry.max-retries";
	public static final String SKIP_COMMIT_FAULT_INJECTION_CLASS = SkipCommitFaultInjection.class.getName();

}
