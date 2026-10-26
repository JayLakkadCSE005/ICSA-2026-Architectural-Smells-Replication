package org.apache.oozie.sla;

import org.apache.hadoop.conf.Configuration;
import org.apache.oozie.client.event.core.SLAEvent;
import org.apache.oozie.sla.listener.SLAEventListener;

public class DummySLAEventListener extends SLAEventListener {

    @Override
    public void onStartMet(SLAEvent sla) {
        TestSLAService.output.append(sla.getId() + " Sla START - MET!!!");
    }

    @Override
    public void onStartMiss(SLAEvent sla) {
        TestSLAService.output.append(sla.getId() + " Sla START - MISS!!!");
    }

    @Override
    public void onEndMet(SLAEvent sla) {
        TestSLAService.output.append(sla.getId() + " Sla END - MET!!!");
    }

    @Override
    public void onEndMiss(SLAEvent sla) {
        TestSLAService.output.append(sla.getId() + " Sla END - MISS!!!");
    }

    @Override
    public void onDurationMet(SLAEvent sla) {
        TestSLAService.output.append(sla.getId() + " Sla DURATION - MET!!!");
    }

    @Override
    public void onDurationMiss(SLAEvent sla) {
        TestSLAService.output.append(sla.getId() + " Sla DURATION - MISS!!!");
    }

    @Override
    public void init(Configuration conf) {
    }

    @Override
    public void destroy() {
    }

}
