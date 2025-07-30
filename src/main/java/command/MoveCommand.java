package command;

import events.EEventType;
import events.EventPublisher;
import events.GameEvent;
import events.soundEvents.EventBus;
import events.soundEvents.MovementEvent;
import events.soundEvents.MovementEvent.MovementPhase;
import interfaces.*;
import pieces.EPieceType;
import pieces.Position;
import utils.LogUtils;

/**
 * Command for moving a piece from one position to another on the board.
 */
public class MoveCommand implements ICommand {
    /** The starting position of the move. */
    private final Position from;
    /** The target position of the move. */
    private final Position to;
    /** The board on which the move is performed. */
    private final IBoard board;

    /**
     * Constructs a MoveCommand for moving a piece from one position to another.
     *
     * @param from  The starting position
     * @param to    The target position
     * @param board The board instance
     */
    public MoveCommand(Position from, Position to, IBoard board) {
        this.from = from;
        this.to = to;
        this.board = board;
    }

    /**
     * Executes the move command, moving the piece if the move is legal.
     * Logs the action and handles illegal moves.
     */
    @Override
    public void execute() {
        if (!board.isMoveLegal(from, to)) {
            String mes = "Illegal move from " + from + " to " + to;
            EventPublisher.getInstance()
                    .publish(EEventType.PIECE_MOVED,
                            new GameEvent(EEventType.PIECE_MOVED, mes));
            LogUtils.logDebug(mes);
            return;
        }
        String mes = "Moving from " + from + " to " + to;
        EventPublisher.getInstance()
                .publish(EEventType.PIECE_MOVED,
                        new GameEvent(EEventType.PIECE_MOVED, mes));
        LogUtils.logDebug(mes);
        // public MovementEvent(EPieceType pieceType, Position fromPosition, Position
        // toPosition,
        // EState movementType, MovementPhase phase
        EventBus.getInstance().publish(new MovementEvent(EPieceType.B, from, to, EState.MOVE, MovementPhase.STARTED));
        MovementEvent event = new MovementEvent(
                EPieceType.P,  from,  to, EState.MOVE,
                MovementEvent.MovementPhase.STARTED);
        board.move(from, to);
    }
}
