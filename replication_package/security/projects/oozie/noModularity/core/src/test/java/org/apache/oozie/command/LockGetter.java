package org.apache.oozie.command;

import org.apache.oozie.lock.LockToken;
import org.apache.oozie.service.MemoryLocksService;
import org.apache.oozie.service.Services;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class LockGetter extends Thread {
    private CountDownLatch lockAcquired = new CountDownLatch(1);
    private volatile boolean lockSuccessful = false;

    @Override
    public void run() {
        try {
            LockToken lock = Services.get().get(MemoryLocksService.class).getWriteLock("key", 1);
            if (lock != null) {
                lockSuccessful = true;
            }
            lockAcquired.countDown();
        } catch (InterruptedException ex) {
            // NOP
        } catch (Exception ex) {
            lockSuccessful = false;
        }
    }

    public void awaitLockAcquired() throws InterruptedException {
        lockAcquired.await(10, TimeUnit.SECONDS);
    }

    public boolean isLockSuccessful() {
        return lockSuccessful;
    }
}
