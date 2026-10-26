package org.apache.activemq.broker.jmx.util.factory;

import org.apache.activemq.broker.scheduler.Job;

import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.SimpleType;
import java.util.Map;

public class JobOpenTypeFactory extends AbstractOpenTypeFactory {

    @Override
    protected String getTypeName() {
        return Job.class.getName();
    }

    @Override
    protected void init() throws OpenDataException {
        super.init();
        addItem("jobId", "jobId", SimpleType.STRING);
        addItem("cronEntry", "Cron entry", SimpleType.STRING);
        addItem("start", "start time", SimpleType.STRING);
        addItem("delay", "initial delay", SimpleType.LONG);
        addItem("next", "next time", SimpleType.STRING);
        addItem("period", "period between jobs", SimpleType.LONG);
        addItem("repeat", "number of times to repeat", SimpleType.INTEGER);
        addItem("destinationName", "destination name", SimpleType.STRING);
    }

    @Override
    public Map<String, Object> getFields(Object o) throws OpenDataException {
        Job job = (Job) o;
        Map<String, Object> rc = super.getFields(o);
        rc.put("jobId", job.getJobId());
        rc.put("cronEntry", "" + job.getCronEntry());
        rc.put("start", job.getStartTime());
        rc.put("delay", job.getDelay());
        rc.put("next", job.getNextExecutionTime());
        rc.put("period", job.getPeriod());
        rc.put("repeat", job.getRepeat());
        rc.put("destinationName", job.getDestinationName());
        return rc;
    }
}
