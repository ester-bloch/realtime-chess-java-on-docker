package events.soundEvents;

import pieces.EPieceType;
import pieces.Position;
import interfaces.EState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JUnit 5 test suite for EventBus class
 * Tests singleton pattern, thread safety, event publishing/subscribing
 */
class EventBusTest {
    
    private EventBus eventBus;
    private TestMovementListener testListener;
    
    @BeforeEach
    void setUp() {
        eventBus = EventBus.getInstance();
        testListener = new TestMovementListener();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up any remaining listeners
        if (testListener != null) {
            eventBus.unsubscribe(testListener);
        }
    }
    
    @Test
    @DisplayName("Should return same instance (Singleton pattern)")
    void testSingletonPattern() {
        EventBus instance1 = EventBus.getInstance();
        EventBus instance2 = EventBus.getInstance();
        
        assertSame(instance1, instance2, "EventBus should follow singleton pattern");
        assertNotNull(instance1, "EventBus instance should not be null");
    }
    
    @Test
    @DisplayName("Should subscribe and publish events correctly")
    void testBasicSubscriptionAndPublishing() throws InterruptedException {
        eventBus.subscribe(testListener);
        
        MovementEvent event = new MovementEvent(
            EPieceType.P, 
            new Position(1, 1), 
            new Position(2, 2), 
            EState.MOVE,
            MovementEvent.MovementPhase.STARTED
        );
        
        eventBus.publish(event);
        Thread.sleep(100); // Give time for event processing
        
        assertEquals(1, testListener.getReceivedEvents().size(), "Should receive exactly one event");
        
        MovementEvent receivedEvent = testListener.getReceivedEvents().get(0);
        assertEquals(EPieceType.P, receivedEvent.getPieceType(), "Event piece type should match");
        assertEquals(EState.MOVE, receivedEvent.getMovementType(), "Event movement type should match");
    }
    
    @Test
    @DisplayName("Should handle multiple listeners correctly")
    void testMultipleListeners() throws InterruptedException {
        TestMovementListener listener1 = new TestMovementListener();
        TestMovementListener listener2 = new TestMovementListener();
        TestMovementListener listener3 = new TestMovementListener();
        
        try {
            eventBus.subscribe(listener1);
            eventBus.subscribe(listener2);
            eventBus.subscribe(listener3);
            
            MovementEvent event = new MovementEvent(
                EPieceType.Q, 
                new Position(3, 3), 
                new Position(4, 4), 
                EState.JUMP,
                MovementEvent.MovementPhase.STARTED
            );
            
            eventBus.publish(event);
            Thread.sleep(100);
            
            assertEquals(1, listener1.getReceivedEvents().size(), "Listener 1 should receive event");
            assertEquals(1, listener2.getReceivedEvents().size(), "Listener 2 should receive event");
            assertEquals(1, listener3.getReceivedEvents().size(), "Listener 3 should receive event");
            
        } finally {
            eventBus.unsubscribe(listener1);
            eventBus.unsubscribe(listener2);
            eventBus.unsubscribe(listener3);
        }
    }
    
    @Test
    @DisplayName("Should unsubscribe listeners correctly")
    void testUnsubscribe() throws InterruptedException {
        eventBus.subscribe(testListener);
        
        // Publish first event (should be received)
        MovementEvent event1 = new MovementEvent(
            EPieceType.R, 
            new Position(1, 1), 
            new Position(1, 2), 
            EState.MOVE,
            MovementEvent.MovementPhase.STARTED
        );
        eventBus.publish(event1);
        
        // Unsubscribe
        eventBus.unsubscribe(testListener);
        
        // Publish second event (should NOT be received)
        MovementEvent event2 = new MovementEvent(
            EPieceType.B, 
            new Position(2, 2), 
            new Position(2, 3), 
            EState.MOVE,
            MovementEvent.MovementPhase.STARTED
        );
        eventBus.publish(event2);
        
        Thread.sleep(100);
        
        assertEquals(1, testListener.getReceivedEvents().size(), 
                   "Should only receive event published before unsubscribe");
    }
    
    @Test
    @DisplayName("Should be thread-safe")
    void testThreadSafety() throws InterruptedException {
        eventBus.subscribe(testListener);
        
        final int threadCount = 10;
        final int eventsPerThread = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < eventsPerThread; j++) {
                        MovementEvent event = new MovementEvent(
                            EPieceType.N, 
                            new Position(threadId, j), 
                            new Position(threadId + 1, j + 1), 
                            EState.MOVE,
                            MovementEvent.MovementPhase.STARTED
                        );
                        eventBus.publish(event);
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "All threads should complete within timeout");
        Thread.sleep(200); // Give time for event processing
        
        int expectedEvents = threadCount * eventsPerThread;
        assertEquals(expectedEvents, testListener.getReceivedEvents().size(), 
                   "Should receive all events from all threads");
    }
    
    @Test
    @DisplayName("Should track listener count correctly")
    void testListenerCount() {
        int initialCount = eventBus.getListenerCount(MovementEvent.class);
        
        TestMovementListener listener1 = new TestMovementListener();
        TestMovementListener listener2 = new TestMovementListener();
        
        try {
            eventBus.subscribe(listener1);
            assertEquals(initialCount + 1, eventBus.getListenerCount(MovementEvent.class), 
                       "Count should increase after adding first listener");
            
            eventBus.subscribe(listener2);
            assertEquals(initialCount + 2, eventBus.getListenerCount(MovementEvent.class), 
                       "Count should increase after adding second listener");
            
            eventBus.unsubscribe(listener1);
            assertEquals(initialCount + 1, eventBus.getListenerCount(MovementEvent.class), 
                       "Count should decrease after removing listener");
            
        } finally {
            eventBus.unsubscribe(listener1);
            eventBus.unsubscribe(listener2);
        }
    }
    
    @Test
    @DisplayName("Should handle errors in listeners gracefully")
    void testErrorHandling() throws InterruptedException {
        ErrorThrowingListener errorListener = new ErrorThrowingListener();
        
        try {
            eventBus.subscribe(errorListener);
            eventBus.subscribe(testListener);
            
            MovementEvent event = new MovementEvent(
                EPieceType.K, 
                new Position(5, 5), 
                new Position(6, 6), 
                EState.IDLE,
                MovementEvent.MovementPhase.STARTED
            );
            
            assertDoesNotThrow(() -> eventBus.publish(event), 
                             "Publishing should not throw exception even if listener fails");
            
            Thread.sleep(100);
            
            assertEquals(1, testListener.getReceivedEvents().size(), 
                       "Normal listener should still receive event despite error in other listener");
            
        } finally {
            eventBus.unsubscribe(errorListener);
        }
    }
    
    @Nested
    @DisplayName("Multiple Event Types")
    class MultipleEventTypesTest {
        
        private TestCollisionListener collisionListener;
        
        @BeforeEach
        void setUp() {
            collisionListener = new TestCollisionListener();
        }
        
        @AfterEach
        void tearDown() {
            if (collisionListener != null) {
                eventBus.unsubscribe(collisionListener);
            }
        }
        
        @Test
        @DisplayName("Should handle different event types correctly")
        void testMultipleEventTypes() throws InterruptedException {
            eventBus.subscribe(testListener);
            eventBus.subscribe(collisionListener);
            
            // Publish movement event
            MovementEvent movementEvent = new MovementEvent(
                EPieceType.P, 
                new Position(1, 1), 
                new Position(2, 2), 
                EState.MOVE,
                MovementEvent.MovementPhase.STARTED
            );
            eventBus.publish(movementEvent);
            
            // Publish collision event
            CollisionEvent collisionEvent = new CollisionEvent(
                EPieceType.R, 
                EPieceType.N, 
                new Position(3, 3), 
                CollisionEvent.CollisionType.PIECE_TO_PIECE
            );
            eventBus.publish(collisionEvent);
            
            Thread.sleep(100);
            
            assertEquals(1, testListener.getReceivedEvents().size(), 
                       "Movement listener should receive movement event");
            assertEquals(1, collisionListener.getReceivedEvents().size(), 
                       "Collision listener should receive collision event");
        }
    }
    
    @RepeatedTest(5)
    @DisplayName("Should consistently handle concurrent operations")
    void testRepeatedConcurrency() throws InterruptedException {
        AtomicInteger eventCount = new AtomicInteger(0);
        IEventListener2<MovementEvent> countingListener = new IEventListener2<MovementEvent>() {
            @Override
            public void onEvent(MovementEvent event) {
                eventCount.incrementAndGet();
            }
            
            @Override
            public Class<MovementEvent> getEventType() {
                return MovementEvent.class;
            }
        };
        
        try {
            eventBus.subscribe(countingListener);
            
            final int eventCount_expected = 20;
            CountDownLatch publishLatch = new CountDownLatch(eventCount_expected);
            
            for (int i = 0; i < eventCount_expected; i++) {
                final int eventId = i;
                new Thread(() -> {
                    try {
                        MovementEvent event = new MovementEvent(
                            EPieceType.P, 
                            new Position(eventId, 0), 
                            new Position(eventId, 1), 
                            EState.MOVE,
                            MovementEvent.MovementPhase.STARTED
                        );
                        eventBus.publish(event);
                    } finally {
                        publishLatch.countDown();
                    }
                }).start();
            }
            
            assertTrue(publishLatch.await(5, TimeUnit.SECONDS), "All events should be published");
            Thread.sleep(200); // Give time for processing
            
            assertEquals(eventCount_expected, eventCount.get(), 
                       "Should process all events correctly");
            
        } finally {
            eventBus.unsubscribe(countingListener);
        }
    }
    
    // Helper test classes
    private static class TestMovementListener implements IEventListener2<MovementEvent> {
        private final List<MovementEvent> receivedEvents = new ArrayList<>();
        
        @Override
        public void onEvent(MovementEvent event) {
            synchronized (receivedEvents) {
                receivedEvents.add(event);
            }
        }
        
        @Override
        public Class<MovementEvent> getEventType() {
            return MovementEvent.class;
        }
        
        public List<MovementEvent> getReceivedEvents() {
            synchronized (receivedEvents) {
                return new ArrayList<>(receivedEvents);
            }
        }
    }
    
    private static class TestCollisionListener implements IEventListener2<CollisionEvent> {
        private final List<CollisionEvent> receivedEvents = new ArrayList<>();
        
        @Override
        public void onEvent(CollisionEvent event) {
            synchronized (receivedEvents) {
                receivedEvents.add(event);
            }
        }
        
        @Override
        public Class<CollisionEvent> getEventType() {
            return CollisionEvent.class;
        }
        
        public List<CollisionEvent> getReceivedEvents() {
            synchronized (receivedEvents) {
                return new ArrayList<>(receivedEvents);
            }
        }
    }
    
    private static class ErrorThrowingListener implements IEventListener2<MovementEvent> {
        @Override
        public void onEvent(MovementEvent event) {
            throw new RuntimeException("Test exception from listener");
        }
        
        @Override
        public Class<MovementEvent> getEventType() {
            return MovementEvent.class;
        }
    }
}
