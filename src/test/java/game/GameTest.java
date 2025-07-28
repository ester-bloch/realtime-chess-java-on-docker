package game;

import board.BoardConfig;
import interfaces.ICommand;
import interfaces.IPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private IPlayer player1;
    private IPlayer player2;
    private BoardConfig boardConfig;
    private Game game;

    @BeforeEach
    void setup() {
        player1 = mock(IPlayer.class);
        player2 = mock(IPlayer.class);

        boardConfig = new BoardConfig(new board.Dimension(8), new board.Dimension(512));

        game = new Game(boardConfig, player1, player2);
    }

    @Test
    void testAddCommandAndUpdateExecutesCommand() {
        ICommand command = mock(ICommand.class);

        game.addCommand(command);
        game.update();

        verify(command, times(1)).execute();
    }

    @Test
    void testHandleSelectionAddsCommandIfNotNull() {
        ICommand cmd = mock(ICommand.class);
        when(player1.handleSelection(game.getBoard())).thenReturn(cmd);

        game.handleSelection(player1);

        game.update();

        verify(player1, times(1)).handleSelection(game.getBoard());
        verify(cmd, times(1)).execute();
    }

    @Test
    void testHandleSelectionDoesNothingIfNull() {
        when(player2.handleSelection(game.getBoard())).thenReturn(null);

        game.handleSelection(player2);

        game.update();

        verify(player2, times(1)).handleSelection(game.getBoard());
    }

    @Test
    void testWinReturnsCorrectPlayer() {
        when(player1.isFailed()).thenReturn(true);
        when(player2.isFailed()).thenReturn(false);
        assertEquals(1, game.win(), "If player1 failed, player2 wins");

        when(player1.isFailed()).thenReturn(false);
        when(player2.isFailed()).thenReturn(true);
        assertEquals(0, game.win(), "If player2 failed, player1 wins");

        when(player1.isFailed()).thenReturn(false);
        when(player2.isFailed()).thenReturn(false);
        assertEquals(-1, game.win(), "No player failed, no winner");
    }

    @Test
    void testGetters() {
        assertSame(player1, game.getPlayer1());
        assertSame(player2, game.getPlayer2());
        assertNotNull(game.getBoard());
    }
}
