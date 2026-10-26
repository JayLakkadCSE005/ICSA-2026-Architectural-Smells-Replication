package org.apache.oozie.event;

import org.apache.oozie.client.event.core.Event;

import java.io.Serializable;

public class EventQueueElement implements Serializable {

    private static final long serialVersionUID = 1L;
    Event event;

    public EventQueueElement(Event e) {
        event = e;
    }
}
