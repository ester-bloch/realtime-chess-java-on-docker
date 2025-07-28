package events.soundEvents;

import interfaces.EState;
import pieces.EPieceType;
import pieces.Position;

public class MovementEvent implements IEvent {
     private final long timestamp;
    private final EPieceType pieceType;
    private final Position fromPosition;
    private final Position toPosition;
    private final EState movementType;
    private final MovementPhase phase;
    
    public enum MovementPhase {
        STARTED,
        COMPLETED
    }
    
    public MovementEvent(EPieceType pieceType, Position fromPosition, Position toPosition, 
                        EState movementType, MovementPhase phase) {
        this.timestamp = System.nanoTime();
        this.pieceType = pieceType;
         this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.movementType = movementType;
        this.phase = phase;
    }
    
    @Override
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String getEventType() {
        return "MOVEMENT";
    }
    
    public EPieceType getPieceType() {
        return pieceType;
    }
    
    public Position getFromPosition() {
        return fromPosition;
    }
    public Position getToPosition() {
        return toPosition;
    }
    
    public EState getMovementType() {
        return movementType;
    }
    
    public MovementPhase getPhase() {
        return phase;
    }
}
