package events.soundEvents;

import pieces.EPieceType;
import pieces.Position;

public class CollisionEvent implements IEvent {
   private final long timestamp;
    private final EPieceType piece1Type;
    private final EPieceType piece2Type;
    private final Position collisionPosition;
    private final CollisionType collisionType;
    
    public enum CollisionType {
        PIECE_TO_PIECE,
        PIECE_TO_BOARD_EDGE,
        JUMP_LANDING
    }
    
    public CollisionEvent(EPieceType piece1Type, EPieceType piece2Type, 
                         Position collisionPosition, CollisionType collisionType) {
        this.timestamp = System.nanoTime();
        this.piece1Type = piece1Type;
      this.piece2Type = piece2Type;
        this.collisionPosition = collisionPosition;
        this.collisionType = collisionType;
    }
    
    @Override
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String getEventType() {
        return "COLLISION";
    }
    
    public EPieceType getPiece1Type() {
        return piece1Type;
    }
    
    public EPieceType getPiece2Type() {
        return piece2Type;
         }
    
    public Position getCollisionPosition() {
        return collisionPosition;
    }
    
    public CollisionType getCollisionType() {
        return collisionType;
    }
}
