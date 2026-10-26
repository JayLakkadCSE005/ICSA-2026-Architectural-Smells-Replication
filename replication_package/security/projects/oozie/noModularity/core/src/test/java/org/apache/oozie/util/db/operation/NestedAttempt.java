package org.apache.oozie.util.db.operation;

class NestedAttempt implements Runnable {
    private final TestRetryAttemptState testRetryAttemptState;
    private final NestedAttempt nestedAttempt;
    private final boolean shouldExhaust;

    NestedAttempt(TestRetryAttemptState testRetryAttemptState, final boolean shouldExhaust) {
        this(testRetryAttemptState, null, shouldExhaust);
    }

    NestedAttempt(TestRetryAttemptState testRetryAttemptState, final NestedAttempt nestedAttempt, final boolean shouldExhaust) {
        this.testRetryAttemptState = testRetryAttemptState;
        this.nestedAttempt = nestedAttempt;
        this.shouldExhaust = shouldExhaust;
    }

    @Override
    public void run() {
        testRetryAttemptState.state.signalStart();

        if (nestedAttempt != null) {
            nestedAttempt.run();
        }

        if (shouldExhaust) {
            testRetryAttemptState.state.signalExhausted();
        } else {
            testRetryAttemptState.state.signalEnd();
        }
    }
}
