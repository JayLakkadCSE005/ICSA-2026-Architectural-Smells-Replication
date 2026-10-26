/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.oozie.util.db.operation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestRetryAttemptState {
    final RetryAttemptState state = new RetryAttemptState();

    @Test
    public void testOneAttemptIsInProgressAndAttemptsAreNotExhausted() {
        state.signalStart();

        assertFalse("retry attempts exhausted", state.isExhausted());
        assertEquals("retry attempt count", 1, state.getInProgressCount());
    }

    @Test
    public void testNoAttemptIsInProgressAndAttemptsAreNotExhausted() {
        state.signalStart();
        state.signalEnd();

        assertFalse("retry attempts exhausted", state.isExhausted());
        assertEquals("retry attempt count", 0, state.getInProgressCount());
    }

    @Test
    public void testExhaustedFromOneThreadRemainsNotStartedFromAnotherThread() throws InterruptedException {
        final Runnable exhaustingAttempt = new ExhaustingAttempt(this);

        final Thread exhaustingThread = new Thread(exhaustingAttempt);

        exhaustingThread.start();

        exhaustingThread.join();

        assertFalse("retry attempts exhausted", state.isExhausted());
        assertEquals("retry attempt count", 0, state.getInProgressCount());
    }

    @Test
    public void testNestedAttemptsFinishSuccessfullyWhenNotExhausted() throws InterruptedException {
        final NestedAttempt inner = new NestedAttempt(this, false);
        final NestedAttempt outer = new NestedAttempt(this, inner, false);

        outer.run();

        assertFalse("retry attempts exhausted", state.isExhausted());
        assertEquals("retry attempt count", 0, state.getInProgressCount());
    }

    @Test
    public void testOuterAttemptRemainsUnfinishedWhenInnerExhausted() throws InterruptedException {
        final NestedAttempt inner = new NestedAttempt(this, true);
        final NestedAttempt outer = new NestedAttempt(this, inner, false);

        outer.run();

        assertTrue("retry attempts exhausted", state.isExhausted());
        assertEquals("retry attempt count", 1, state.getInProgressCount());
    }

    @Test
    public void testInnerAttemptRemainsUnfinishedWhenOuterExhausted() throws InterruptedException {
        final NestedAttempt inner = new NestedAttempt(this, false);
        final NestedAttempt outer = new NestedAttempt(this, inner, true);

        outer.run();

        assertTrue("retry attempts exhausted", state.isExhausted());
        assertEquals("retry attempt count", 1, state.getInProgressCount());
    }

}