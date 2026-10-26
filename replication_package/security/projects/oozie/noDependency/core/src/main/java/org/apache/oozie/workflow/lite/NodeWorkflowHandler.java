package org.apache.oozie.workflow.lite;

import org.apache.oozie.service.ConfigurationService;
import org.apache.oozie.service.Service;
import org.apache.oozie.workflow.WorkflowException;

public class NodeWorkflowHandler {
	private NodeWorkflowHandler() {
		
	}
	public static final String NODE_DEF_VERSION_0 = "_oozie_inst_v_0";
	public static final String NODE_DEF_VERSION_1 = "_oozie_inst_v_1";
	public static final String NODE_DEF_VERSION_2 = "_oozie_inst_v_2";
	public static final String CONF_PREFIX = Service.CONF_PREFIX + "LiteWorkflowStoreService.";
	public static final String CONF_NODE_DEF_VERSION = CONF_PREFIX + "node.def.version";
	/**
	 * Get NodeDef default version, _oozie_inst_v_0, _oozie_inst_v_1 or
	 * _oozie_inst_v_2
	 *
	 * @return nodedef default version
	 * @throws WorkflowException thrown if there was an error parsing the action
	 *         configuration.
	 */
	public static String getNodeDefDefaultVersion() {
	    String ret = ConfigurationService.get(CONF_NODE_DEF_VERSION);
	    if (ret == null) {
	        ret = NODE_DEF_VERSION_2;
	    }
	    return ret;
	}

}
