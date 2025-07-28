package player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pieces.Position;
import player.PlayerCursor;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class PlayerCursorTest {

    private PlayerCursor cursor;

    @BeforeEach
    void setUp() {
        cursor = new PlayerCursor(new Position(4, 4), Color.BLACK);
    }

    @Test
    void testInitialPosition() {
        assertEquals(4, cursor.getRow());
        assertEquals(4, cursor.getCol());
    }

    @Test
    void testMoveUp() {
        cursor.moveUp();
        assertEquals(3, cursor.getRow());
        cursor.moveUp();
        cursor.moveUp();
        cursor.moveUp(); // reach 0
        assertEquals(0, cursor.getRow());

        cursor.moveUp(); // does not go out of bounds
        assertEquals(0, cursor.getRow());
    }

    @Test
    void testMoveDown() {
        cursor.moveDown();
        assertEquals(5, cursor.getRow());
        for (int i = 0; i < 10; i++) {
            cursor.moveDown();
        }
        assertEquals(7, cursor.getRow()); // does not exceed
    }
}