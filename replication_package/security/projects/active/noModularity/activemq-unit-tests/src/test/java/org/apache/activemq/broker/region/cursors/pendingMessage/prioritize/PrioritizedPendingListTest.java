/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.broker.region.cursors.pendingMessage.prioritize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.region.cursors.pendinglist.PrioritizedPendingList;
import org.junit.Test;

public class PrioritizedPendingListTest {

    @Test
    public void testAddMessageFirst() {
        PrioritizedPendingList list = new PrioritizedPendingList();

        list.addMessageFirst(new TestMessageReference1(1));
        list.addMessageFirst(new TestMessageReference1(2));
        list.addMessageFirst(new TestMessageReference1(3));
        list.addMessageFirst(new TestMessageReference1(4));
        list.addMessageFirst(new TestMessageReference1(5));

        assertTrue(list.size() == 5);

        Iterator<MessageReference> iter = list.iterator();
        int lastId = list.size();
        while (iter.hasNext()) {
            assertEquals(lastId--, iter.next().getMessageId().getProducerSequenceId());
        }
    }

    @Test
    public void testAddMessageLast() {

        PrioritizedPendingList list = new PrioritizedPendingList();

        list.addMessageLast(new TestMessageReference1(1));
        list.addMessageLast(new TestMessageReference1(2));
        list.addMessageLast(new TestMessageReference1(3));
        list.addMessageLast(new TestMessageReference1(4));
        list.addMessageLast(new TestMessageReference1(5));

        assertTrue(list.size() == 5);

        Iterator<MessageReference> iter = list.iterator();
        int lastId = 1;
        while (iter.hasNext()) {
            assertEquals(lastId++, iter.next().getMessageId().getProducerSequenceId());
        }
    }

    @Test
    public void testClear() {
        PrioritizedPendingList list = new PrioritizedPendingList();

        list.addMessageFirst(new TestMessageReference1(1));
        list.addMessageFirst(new TestMessageReference1(2));
        list.addMessageFirst(new TestMessageReference1(3));
        list.addMessageFirst(new TestMessageReference1(4));
        list.addMessageFirst(new TestMessageReference1(5));

        assertFalse(list.isEmpty());
        assertTrue(list.size() == 5);

        list.clear();

        assertTrue(list.isEmpty());
        assertTrue(list.size() == 0);

        list.addMessageFirst(new TestMessageReference1(1));
        list.addMessageLast(new TestMessageReference1(2));
        list.addMessageLast(new TestMessageReference1(3));
        list.addMessageFirst(new TestMessageReference1(4));
        list.addMessageLast(new TestMessageReference1(5));

        assertFalse(list.isEmpty());
        assertTrue(list.size() == 5);
    }

    @Test
    public void testIsEmpty() {
        PrioritizedPendingList list = new PrioritizedPendingList();
        assertTrue(list.isEmpty());

        list.addMessageFirst(new TestMessageReference1(1));
        list.addMessageFirst(new TestMessageReference1(2));
        list.addMessageFirst(new TestMessageReference1(3));
        list.addMessageFirst(new TestMessageReference1(4));
        list.addMessageFirst(new TestMessageReference1(5));

        assertFalse(list.isEmpty());
        list.clear();
        assertTrue(list.isEmpty());
    }

    @Test
    public void testRemove() {
        PrioritizedPendingList list = new PrioritizedPendingList();

        TestMessageReference1 toRemove = new TestMessageReference1(6);

        list.addMessageFirst(new TestMessageReference1(1));
        list.addMessageFirst(new TestMessageReference1(2));
        list.addMessageFirst(new TestMessageReference1(3));
        list.addMessageFirst(new TestMessageReference1(4));
        list.addMessageFirst(new TestMessageReference1(5));

        assertTrue(list.size() == 5);

        list.addMessageLast(toRemove);
        list.remove(toRemove);

        assertTrue(list.size() == 5);

        list.remove(toRemove);

        assertTrue(list.size() == 5);

        Iterator<MessageReference> iter = list.iterator();
        int lastId = list.size();
        while (iter.hasNext()) {
            assertEquals(lastId--, iter.next().getMessageId().getProducerSequenceId());
        }

        list.remove(null);
    }

    @Test
    public void testSize() {
        PrioritizedPendingList list = new PrioritizedPendingList();
        assertTrue(list.isEmpty());

        assertTrue(list.size() == 0);
        list.addMessageFirst(new TestMessageReference1(1));
        assertTrue(list.size() == 1);
        list.addMessageLast(new TestMessageReference1(2));
        assertTrue(list.size() == 2);
        list.addMessageFirst(new TestMessageReference1(3));
        assertTrue(list.size() == 3);
        list.addMessageLast(new TestMessageReference1(4));
        assertTrue(list.size() == 4);
        list.addMessageFirst(new TestMessageReference1(5));
        assertTrue(list.size() == 5);

        assertFalse(list.isEmpty());
        list.clear();
        assertTrue(list.isEmpty());
        assertTrue(list.size() == 0);
    }

    @Test
    public void testPrioritization() {
        PrioritizedPendingList list = new PrioritizedPendingList();

        list.addMessageFirst(new TestMessageReference1(1, 5));
        list.addMessageFirst(new TestMessageReference1(2, 4));
        list.addMessageFirst(new TestMessageReference1(3, 3));
        list.addMessageFirst(new TestMessageReference1(4, 2));
        list.addMessageFirst(new TestMessageReference1(5, 1));

        assertTrue(list.size() == 5);

        Iterator<MessageReference> iter = list.iterator();
        int lastId = list.size();
        while (iter.hasNext()) {
            MessageReference nextMessage = iter.next();
            assertNotNull(nextMessage);
            assertEquals(lastId--, nextMessage.getMessage().getPriority());
        }
    }

    @Test
    public void testValuesPriority() {
        PrioritizedPendingList list = new PrioritizedPendingList();

        list.addMessageFirst(new TestMessageReference1(1, 2));
        list.addMessageFirst(new TestMessageReference1(2, 1));
        list.addMessageFirst(new TestMessageReference1(3, 3));
        list.addMessageFirst(new TestMessageReference1(4, 5));
        list.addMessageFirst(new TestMessageReference1(5, 4));

        assertTrue(list.size() == 5);

        Iterator<MessageReference> iter = list.iterator();
        int lastId = list.size();
        while (iter.hasNext()) {
            assertEquals(lastId--, iter.next().getMessage().getPriority());
        }

        lastId = list.size();
        for (MessageReference messageReference : list.values()) {
            assertEquals(lastId--, messageReference.getMessage().getPriority());
        }
    }

    @Test
    public void testFullRangeIteration() {
        PrioritizedPendingList list = new PrioritizedPendingList();

        int totalElements = 0;

        for (int i = 0; i < 10; ++i) {
            list.addMessageFirst(new TestMessageReference1(totalElements++, i));
            list.addMessageFirst(new TestMessageReference1(totalElements++, i));
        }

        assertTrue(list.size() == totalElements);

        int totalIterated = 0;
        Iterator<MessageReference> iter = list.iterator();
        while (iter.hasNext()) {
            MessageReference nextMessage = iter.next();
            assertNotNull(nextMessage);
            totalIterated++;
        }

        assertEquals(totalElements, totalIterated);
    }

}
