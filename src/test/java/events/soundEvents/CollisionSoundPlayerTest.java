package events.soundEvents;

import pieces.EPieceType;
import pieces.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

/**
 * JUnit 5 test suite for CollisionSoundPlayer class
 * Tests sound file selection, event handling, and async sound playing
 */
class CollisionSoundPlayerTest {
    
    private CollisionSoundPlayer soundPlayer;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        soundPlayer = new CollisionSoundPlayer();
    }
    
    @Test
    @DisplayName("Should return correct event type")
    void testEventTypeVerification() {
        Class<CollisionEvent> eventType = soundPlayer.getEventType();
        assertEquals(CollisionEvent.class, eventType, "Event type should be CollisionEvent.class");
    }
    
    @Test
    @DisplayName("Should implement IEventListener interface")
    void testInterfaceImplementation() {
        assertInstanceOf(IEventListener2.class, soundPlayer, "Should implement IEventListener interface");
        assertNotNull(soundPlayer.getEventType(), "Should have non-null event type");
    }
    
    @Test
    @DisplayName("Should handle piece-to-piece collision events")
    void testPieceToPieceCollisionSound() {
        CollisionEvent event = new CollisionEvent(
            EPieceType.R,
            EPieceType.N,
            new Position(4, 4),
            CollisionEvent.CollisionType.PIECE_TO_PIECE
        );
        
        assertDoesNotThrow(() -> soundPlayer.onEvent(event), 
                         "Should handle piece-to-piece collision without throwing");
    }
    
    @Test
    @DisplayName("Should handle piece-to-edge collision events")
    void testPieceToEdgeCollisionSound() {
        CollisionEvent event = new CollisionEvent(
            EPieceType.Q,
            null, // No second piece for edge collision
            new Position(0, 0),
            CollisionEvent.CollisionType.PIECE_TO_BOARD_EDGE
        );
        
        assertDoesNotThrow(() -> soundPlayer.onEvent(event), 
                         "Should handle piece-to-edge collision without throwing");
    }
    
    @Test
    @DisplayName("Should handle jump landing events")
    void testJumpLandingSound() {
        CollisionEvent event = new CollisionEvent(
            EPieceType.N, // Knight landing from jump
            null,
            new Position(3, 5),
            CollisionEvent.CollisionType.JUMP_LANDING
        );
        
        assertDoesNotThrow(() -> soundPlayer.onEvent(event), 
                         "Should handle jump landing without throwing");
    }
    
    @Test
    @DisplayName("Should handle null collision type gracefully")
    void testDefaultCollisionSound() {
        CollisionEvent event = new CollisionEvent(
            EPieceType.P,
            EPieceType.B,
            new Position(2, 2),
            null // This should trigger default case
        );
        
        assertDoesNotThrow(() -> soundPlayer.onEvent(event), 
                         "Should handle null collision type without throwing");
    }
    
    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    @DisplayName("Should return quickly (async sound playing)")
    void testAsyncSoundPlaying() {
        CollisionEvent event = new CollisionEvent(
            EPieceType.K,
            EPieceType.Q,
            new Position(4, 4),
            CollisionEvent.CollisionType.PIECE_TO_PIECE
        );
        
        long startTime = System.currentTimeMillis();
        soundPlayer.onEvent(event);
        long endTime = System.currentTimeMillis();
        
        long duration = endTime - startTime;
        assertTrue(duration < 100, "Should return quickly (async), took: " + duration + "ms");
    }
    
    @Test
    @DisplayName("Should handle multiple rapid events")
    void testMultipleRapidEvents() {
        CollisionEvent[] events = {
            new CollisionEvent(EPieceType.R, EPieceType.N, new Position(1, 1), 
                             CollisionEvent.CollisionType.PIECE_TO_PIECE),
            new CollisionEvent(EPieceType.Q, null, new Position(0, 0), 
                             CollisionEvent.CollisionType.PIECE_TO_BOARD_EDGE),
            new CollisionEvent(EPieceType.N, null, new Position(3, 3), 
                             CollisionEvent.CollisionType.JUMP_LANDING)
        };
        
        assertDoesNotThrow(() -> {
            for (CollisionEvent event : events) {
                soundPlayer.onEvent(event);
            }
        }, "Should handle multiple rapid events without throwing");
    }
    
    @Nested
    @DisplayName("Collision Type Tests")
    class CollisionTypeTests {
        
        @Test
        @DisplayName("Should handle all collision types")
        void testAllCollisionTypes() {
            CollisionEvent.CollisionType[] allTypes = CollisionEvent.CollisionType.values();
            
            for (CollisionEvent.CollisionType type : allTypes) {
                CollisionEvent event = new CollisionEvent(
                    EPieceType.P,
                    EPieceType.R,
                    new Position(1, 1),
                    type
                );
                
                assertDoesNotThrow(() -> soundPlayer.onEvent(event), 
                                 "Should handle collision type: " + type);
            }
        }
        
        @Test
        @DisplayName("Should handle piece-to-piece with different piece combinations")
        void testDifferentPieceCombinations() {
            EPieceType[] pieceTypes = EPieceType.values();
            
            for (EPieceType piece1 : pieceTypes) {
                for (EPieceType piece2 : pieceTypes) {
                    CollisionEvent event = new CollisionEvent(
                        piece1,
                        piece2,
                        new Position(4, 4),
                        CollisionEvent.CollisionType.PIECE_TO_PIECE
                    );
                    
                    assertDoesNotThrow(() -> soundPlayer.onEvent(event), 
                                     "Should handle collision between " + piece1 + " and " + piece2);
                }
            }
        }
    }
    
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        
        @Test
        @DisplayName("Should handle events with null positions")
        void testNullPositions() {
            CollisionEvent event = new CollisionEvent(
                EPieceType.K,
                EPieceType.Q,
                null, // null position
                CollisionEvent.CollisionType.PIECE_TO_PIECE
            );
            
            assertDoesNotThrow(() -> soundPlayer.onEvent(event), 
                             "Should handle null position without throwing");
        }
        
        @Test
        @DisplayName("Should handle events with null piece types")
        void testNullPieceTypes() {
            CollisionEvent event = new CollisionEvent(
                null, // null piece type
                EPieceType.Q,
                new Position(2, 2),
                CollisionEvent.CollisionType.PIECE_TO_PIECE
            );
            
            assertDoesNotThrow(() -> soundPlayer.onEvent(event), 
                             "Should handle null piece type without throwing");
        }
        
        @Test
        @DisplayName("Should handle completely null event data")
        void testNullEventData() {
            CollisionEvent event = new CollisionEvent(
                null,
                null,
                null,
                null
            );
            
            assertDoesNotThrow(() -> soundPlayer.onEvent(event), 
                             "Should handle completely null event data without throwing");
        }
    }
    
    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {
        
        @Test
        @DisplayName("Should handle rapid successive events efficiently")
        void testRapidEvents() {
            final int eventCount = 100;
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < eventCount; i++) {
                CollisionEvent event = new CollisionEvent(
                    EPieceType.P,
                    EPieceType.R,
                    new Position(i % 8, i % 8),
                    CollisionEvent.CollisionType.PIECE_TO_PIECE
                );
                soundPlayer.onEvent(event);
            }
            
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            
            assertTrue(totalTime < 1000, 
                     "Should handle " + eventCount + " events quickly, took: " + totalTime + "ms");
        }
        
        @Test
        @Timeout(value = 1, unit = TimeUnit.SECONDS)
        @DisplayName("Individual event should complete quickly")
        void testIndividualEventPerformance() {
            CollisionEvent event = new CollisionEvent(
                EPieceType.N,
                EPieceType.B,
                new Position(5, 5),
                CollisionEvent.CollisionType.JUMP_LANDING
            );
            
            // This test will fail if it takes longer than 1 second
            soundPlayer.onEvent(event);
            
            // If we reach here, the test passed
            assertTrue(true, "Event processing completed within timeout");
        }
    }
}
