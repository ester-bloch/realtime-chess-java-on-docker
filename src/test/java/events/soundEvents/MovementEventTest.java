package events.soundEvents;

import pieces.EPieceType;
import pieces.Position;
import interfaces.EState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite for MovementEvent class
 * Tests event data, timestamp, and all getter methods
 */
class MovementEventTest {
    
    private Position fromPosition;
    private Position toPosition;
    private EPieceType pieceType;
    private EState movementType;
    private MovementEvent.MovementPhase phase;
    
    @BeforeEach
    void setUp() {
        fromPosition = new Position(1, 1);
        toPosition = new Position(2, 2);
        pieceType = EPieceType.P;
        movementType = EState.MOVE;
        phase = MovementEvent.MovementPhase.STARTED;
    }
    
    @Test
    @DisplayName("Should create MovementEvent with all required fields")
    void testBasicEventCreation() {
        MovementEvent event = new MovementEvent(
            pieceType,
            fromPosition,
            toPosition,
            movementType,
            phase
        );
        
        assertNotNull(event, "MovementEvent should not be null");
        assertEquals(pieceType, event.getPieceType(), "Piece type should match");
        assertEquals(fromPosition, event.getFromPosition(), "From position should match");
        assertEquals(toPosition, event.getToPosition(), "To position should match");
        assertEquals(movementType, event.getMovementType(), "Movement type should match");
        assertEquals(phase, event.getPhase(), "Phase should match");
    }
    
    @Test
    @DisplayName("Should generate valid timestamp")
    void testTimestampGeneration() {
        long beforeCreation = System.nanoTime();
        
        MovementEvent event = new MovementEvent(
            EPieceType.R,
            new Position(3, 3),
            new Position(4, 4),
            EState.JUMP,
            MovementEvent.MovementPhase.STARTED
        );
        
        long afterCreation = System.nanoTime();
        long eventTimestamp = event.getTimestamp();
        
        assertTrue(eventTimestamp >= beforeCreation, "Timestamp should be after creation start");
        assertTrue(eventTimestamp <= afterCreation, "Timestamp should be before creation end");
        assertTrue(eventTimestamp > 0, "Timestamp should be positive");
    }
    
    @Test
    @DisplayName("Should return correct event type")
    void testEventType() {
        MovementEvent event = new MovementEvent(
            EPieceType.Q,
            new Position(0, 0),
            new Position(7, 7),
            EState.MOVE,
            MovementEvent.MovementPhase.COMPLETED
        );
        
        assertEquals("MOVEMENT", event.getEventType(), "Event type should be 'MOVEMENT'");
    }
    
    @Test
    @DisplayName("Should implement IEvent interface correctly")
    void testIEventInterface() {
        MovementEvent event = new MovementEvent(
            EPieceType.Q,
            new Position(3, 0),
            new Position(3, 7),
            EState.MOVE,
            MovementEvent.MovementPhase.STARTED
        );
        
        assertInstanceOf(IEvent.class, event, "Should implement IEvent interface");
        assertTrue(event.getTimestamp() > 0, "Should have valid timestamp");
        assertNotNull(event.getEventType(), "Should have non-null event type");
    }
    
    @Test
    @DisplayName("Should handle multiple events with different timestamps")
    void testMultipleEventsTimestamps() throws InterruptedException {
        MovementEvent event1 = new MovementEvent(
            EPieceType.P,
            new Position(1, 1),
            new Position(1, 2),
            EState.MOVE,
            MovementEvent.MovementPhase.STARTED
        );
        
        Thread.sleep(1); // Ensure different timestamps
        
        MovementEvent event2 = new MovementEvent(
            EPieceType.P,
            new Position(1, 2),
            new Position(1, 3),
            EState.MOVE,
            MovementEvent.MovementPhase.STARTED
        );
        
        assertTrue(event2.getTimestamp() > event1.getTimestamp(), 
                  "Second event should have later timestamp");
    }
    
    @Nested
    @DisplayName("Position Tests")
    class PositionTests {
        
        @Test
        @DisplayName("Should store from and to positions correctly")
        void testPositionStorage() {
            Position from = new Position(2, 3);
            Position to = new Position(5, 6);
            
            MovementEvent event = new MovementEvent(
                EPieceType.N,
                from,
                to,
                EState.JUMP,
                MovementEvent.MovementPhase.STARTED
            );
            
            assertEquals(from, event.getFromPosition(), "From position should match");
            assertEquals(to, event.getToPosition(), "To position should match");
        }
        
        @Test
        @DisplayName("Should handle same from and to positions")
        void testSamePositions() {
            Position position = new Position(4, 4);
            
            MovementEvent event = new MovementEvent(
                EPieceType.K,
                position,
                position,
                EState.IDLE,
                MovementEvent.MovementPhase.STARTED
            );
            
            assertEquals(position, event.getFromPosition(), "From position should match");
            assertEquals(position, event.getToPosition(), "To position should match");
        }
    }
    
    @Nested
    @DisplayName("Piece Type Tests")
    class PieceTypeTests {
        
        @Test
        @DisplayName("Should work with all piece types")
        void testAllPieceTypes() {
            EPieceType[] allPieceTypes = EPieceType.values();
            
            for (EPieceType pieceType : allPieceTypes) {
                MovementEvent event = new MovementEvent(
                    pieceType,
                    new Position(0, 0),
                    new Position(1, 1),
                    EState.MOVE,
                    MovementEvent.MovementPhase.STARTED
                );
                
                assertEquals(pieceType, event.getPieceType(), 
                           "Should work correctly for piece type: " + pieceType);
            }
        }
    }
    
    @Nested
    @DisplayName("Movement Type Tests")
    class MovementTypeTests {
        
        @Test
        @DisplayName("Should work with all movement types")
        void testAllMovementTypes() {
            EState[] allMovementTypes = EState.values();
            
            for (EState movementType : allMovementTypes) {
                MovementEvent event = new MovementEvent(
                    EPieceType.P,
                    new Position(0, 0),
                    new Position(1, 1),
                    movementType,
                    MovementEvent.MovementPhase.STARTED
                );
                
                assertEquals(movementType, event.getMovementType(), 
                           "Should work correctly for movement type: " + movementType);
            }
        }
    }
    
    @Nested
    @DisplayName("Movement Phase Tests")
    class MovementPhaseTests {
        
        @Test
        @DisplayName("Should work with all movement phases")
        void testAllMovementPhases() {
            MovementEvent.MovementPhase[] allPhases = MovementEvent.MovementPhase.values();
            
            for (MovementEvent.MovementPhase phase : allPhases) {
                MovementEvent event = new MovementEvent(
                    EPieceType.K,
                    new Position(4, 0),
                    new Position(4, 1),
                    EState.MOVE,
                    phase
                );
                
                assertEquals(phase, event.getPhase(), 
                           "Should work correctly for movement phase: " + phase);
            }
        }
        
        @Test
        @DisplayName("Should distinguish between STARTED and COMPLETED phases")
        void testPhaseDistinction() {
            MovementEvent startedEvent = new MovementEvent(
                EPieceType.R,
                new Position(0, 0),
                new Position(0, 7),
                EState.MOVE,
                MovementEvent.MovementPhase.STARTED
            );
            
            MovementEvent completedEvent = new MovementEvent(
                EPieceType.R,
                new Position(0, 0),
                new Position(0, 7),
                EState.MOVE,
                MovementEvent.MovementPhase.COMPLETED
            );
            
            assertEquals(MovementEvent.MovementPhase.STARTED, startedEvent.getPhase());
            assertEquals(MovementEvent.MovementPhase.COMPLETED, completedEvent.getPhase());
            assertNotEquals(startedEvent.getPhase(), completedEvent.getPhase());
        }
    }
}
