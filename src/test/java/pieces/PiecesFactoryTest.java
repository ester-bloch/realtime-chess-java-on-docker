package pieces;

import board.BoardConfig;
import board.Dimension;
import interfaces.PiecesFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class PiecesFactoryTest {


    @Test
    void testCreatePieceByCode_ValidPiece() {
        BoardConfig config = new BoardConfig(new Dimension(8), new Dimension(64*8));

        Position inputPos = new Position(2, 3);
        //PiecesFactory.createPieceByCode(EPieceType code, int playerId, Position pos, BoardConfig config) : Piece
        Piece piece = PiecesFactory.createPieceByCode(EPieceType.B, 1, inputPos, config);

        assertNotNull(piece);
    }


    @Test
    public void testCreatePieceByCode_InvalidCodeReturnsNull() {
        BoardConfig config = new BoardConfig(new Dimension(8), new Dimension(64 * 8));
        Piece piece = PiecesFactory.createPieceByCode(EPieceType.B, 1, new Position(0, 0), config);
        assertNull(piece, "Expected null for non-existent piece resources");
    }
}
