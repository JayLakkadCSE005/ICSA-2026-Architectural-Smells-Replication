package org.apache.activemq.network.bridge.demand;

import org.apache.activemq.command.BrokerInfo;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * Used to allow for async tasks to await receipt of the BrokerInfo from the local and
 * remote sides of the network bridge.
 */
class FutureBrokerInfo implements Future<BrokerInfo> {

    private final CountDownLatch slot = new CountDownLatch(1);
    private final AtomicBoolean disposed;
    private volatile BrokerInfo info = null;

    public FutureBrokerInfo(BrokerInfo info, AtomicBoolean disposed) {
        this.info = info;
        this.disposed = disposed;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        slot.countDown();
        return true;
    }

    @Override
    public boolean isCancelled() {
        return slot.getCount() == 0 && info == null;
    }

    @Override
    public boolean isDone() {
        return info != null;
    }

    @Override
    public BrokerInfo get() throws InterruptedException, ExecutionException {
        try {
            if (info == null) {
                while (!disposed.get()) {
                    if (slot.await(1, TimeUnit.SECONDS)) {
                        break;
                    }
                }
            }
            return info;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            DemandForwardingBridgeSupport.LOG.debug("Operation interrupted: {}", e, e);
            throw new InterruptedException("Interrupted.");
        }
    }

    @Override
    public BrokerInfo get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            if (info == null) {
                long deadline = System.currentTimeMillis() + unit.toMillis(timeout);

                while (!disposed.get() || System.currentTimeMillis() - deadline < 0) {
                    if (slot.await(1, TimeUnit.MILLISECONDS)) {
                        break;
                    }
                }
                if (info == null) {
                    throw new TimeoutException();
                }
            }
            return info;
        } catch (InterruptedException e) {
            throw new InterruptedException("Interrupted.");
        }
    }

    public void set(BrokerInfo info) {
        this.info = info;
        this.slot.countDown();
    }
}
