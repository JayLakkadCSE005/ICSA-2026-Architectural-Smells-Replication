package org.apache.oozie.command.bundle.operation.dummy;

import org.apache.oozie.service.UUIDService;

public class DummyUUIDService extends UUIDService {
    boolean firstTime = true;

    @Override
    public String generateId(ApplicationType type) {
        if (type.equals(ApplicationType.COORDINATOR) && firstTime) {
            firstTime = false;
            return "dummy-coord-id";
        } else {
            return super.generateId(type);
        }
    }
}
