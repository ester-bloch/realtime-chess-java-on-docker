package events.soundEvents;

import pieces.EPieceType;
import pieces.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite for CollisionEvent class
 * Tests event data, timestamp, and all getter methods
 */
class CollisionEventTest {
    
    private Position collisionPosition;
    private EPieceType piece1Type;
    private EPieceType piece2Type;
    private CollisionEvent.CollisionType collisionType;
    
    @BeforeEach
    void setUp() {
        collisionPosition = new Position(3, 3);
        piece1Type = EPieceType.R;
        piece2Type = EPieceType.N;
        collisionType = CollisionEvent.CollisionType.PIECE_TO_PIECE;
    }
    
    @Test
    @DisplayName("Should create CollisionEvent with all required fields")
    void testBasicEventCreation() {
        CollisionEvent event = new CollisionEvent(
            piece1Type,
            piece2Type,
            collisionPosition,
            collisionType
        );
        
        assertNotNull(event, "CollisionEvent should not be null");
        assertEquals(piece1Type, event.getPiece1Type(), "Piece1 type should match");
        assertEquals(piece2Type, event.getPiece2Type(), "Piece2 type should match");
        assertEquals(collisionPosition, event.getCollisionPosition(), "Collision position should match");
        assertEquals(collisionType, event.getCollisionType(), "Collision type should match");
    }
    
    @Test
    @DisplayName("Should generate valid timestamp")
    void testTimestampGeneration() {
        long beforeCreation = System.nanoTime();
        
        CollisionEvent event = new CollisionEvent(
            EPieceType.Q,
            EPieceType.K,
            new Position(4, 4),
            CollisionEvent.CollisionType.PIECE_TO_PIECE
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
        CollisionEvent event = new CollisionEvent(
            EPieceType.P,
            EPieceType.B,
            new Position(2, 2),
            CollisionEvent.CollisionType.PIECE_TO_PIECE
        );
        
        assertEquals("COLLISION", event.getEventType(), "Event type should be 'COLLISION'");
    }
    
    @Test
    @DisplayName("Should implement IEvent interface correctly")
    void testIEventInterface() {
        CollisionEvent event = new CollisionEvent(
            EPieceType.N,
            null,
            new Position(1, 1),
            CollisionEvent.CollisionType.JUMP_LANDING
        );
        
        assertInstanceOf(IEvent.class, event, "Should implement IEvent interface");
        assertTrue(event.getTimestamp() > 0, "Should have valid timestamp");
        assertNotNull(event.getEventType(), "Should have non-null event type");
    }
    
    @Nested
    @DisplayName("Position Tests")
    class PositionTests {
        
        @Test
        @DisplayName("Should store collision position correctly")
        void testPositionStorage() {
            Position position = new Position(5, 7);
            
            CollisionEvent event = new CollisionEvent(
                EPieceType.Q,
                EPieceType.R,
                position,
                CollisionEvent.CollisionType.PIECE_TO_PIECE
            );
            
            assertEquals(position, event.getCollisionPosition(), "Collision position should match");
        }
        
        @Test
        @DisplayName("Should handle null position")
        void testNullPosition() {
            CollisionEvent event = new CollisionEvent(
                EPieceType.K,
                EPieceType.P,
                null, // null position
                CollisionEvent.CollisionType.PIECE_TO_PIECE
            );
            
            assertNull(event.getCollisionPosition(), "Should handle null position");
            assertNotNull(event, "Event should still be created");
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
                CollisionEvent event = new CollisionEvent(
                    pieceType,
                    EPieceType.P,
                    new Position(1, 1),
                    CollisionEvent.CollisionType.PIECE_TO_PIECE
                );
                
                assertEquals(pieceType, event.getPiece1Type(), 
                           "Should work correctly for piece type: " + pieceType);
            }
        }
        
        @Test
        @DisplayName("Should handle null piece types")
        void testNullPieceTypes() {
            CollisionEvent event = new CollisionEvent(
                EPieceType.R,
                null,
                new Position(0, 4),
                CollisionEvent.CollisionType.PIECE_TO_BOARD_EDGE
            );
            
            assertEquals(EPieceType.R, event.getPiece1Type(), "Piece1 should still be set");
            assertNull(event.getPiece2Type(), "Should handle null piece2");
        }
    }
    
    @Nested
    @DisplayName("Collision Type Tests")
    class CollisionTypeTests {
        
        @Test
        @DisplayName("Should work with all collision types")
        void testAllCollisionTypes() {
            CollisionEvent.CollisionType[] allTypes = CollisionEvent.CollisionType.values();
            
            for (CollisionEvent.CollisionType type : allTypes) {
                CollisionEvent event = new CollisionEvent(
                    EPieceType.N,
                    EPieceType.B,
                    new Position(3, 3),
                    type
                );
                
                assertEquals(type, event.getCollisionType(), 
                           "Should work correctly for collision type: " + type);
            }
        }
    }
}
