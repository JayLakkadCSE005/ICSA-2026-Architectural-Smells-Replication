package org.apache.oozie.util.db.operation;

class ExhaustingAttempt implements Runnable {
    private final TestRetryAttemptState testRetryAttemptState;

    public ExhaustingAttempt(TestRetryAttemptState testRetryAttemptState) {
        this.testRetryAttemptState = testRetryAttemptState;
    }

    @Override
    public void run() {
        testRetryAttemptState.state.signalStart();

        testRetryAttemptState.state.signalExhausted();
    }
}
