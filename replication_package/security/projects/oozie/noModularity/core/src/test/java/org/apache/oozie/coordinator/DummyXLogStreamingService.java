package org.apache.oozie.coordinator;

import org.apache.oozie.service.XLogStreamingService;
import org.apache.oozie.util.XLogFilter;
import org.apache.oozie.util.XLogStreamer;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

class DummyXLogStreamingService extends XLogStreamingService {
    XLogFilter filter;
    Date startTime;
    Date endTime;

    @Override
    public void streamLog(XLogStreamer logStreamer, Date startTime, Date endTime, Writer writer)
            throws IOException {
        filter = logStreamer.getXLogFilter();
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
