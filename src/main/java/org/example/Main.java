package org.example;

import board.BoardConfig;
import board.Dimension;
import events.soundEvents.EventBus;
import events.soundEvents.MovementEvent;
import events.soundEvents.MovementSoundPlayer;
import game.Game;
import interfaces.IGame;
import interfaces.IPlayer;
import pieces.Position;
import player.Player;
import player.PlayerCursor;
import view.GamePanel;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            //ToDo: here is an initial trying to play a sound
            MovementSoundPlayer movementSoundPlayer = new MovementSoundPlayer();
            EventBus.getInstance().subscribe(movementSoundPlayer);
           
            JFrame frame = new JFrame("KFChess");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            BoardConfig boardConfig = new BoardConfig(new Dimension(8),new Dimension(64*8));

            IPlayer p1 = new Player("aaa",new PlayerCursor(new Position(0,0), Color.RED), boardConfig);
            IPlayer p2 = new Player("bbb",new PlayerCursor(new Position(7,7),Color.BLUE), boardConfig);

            IGame game = new Game(boardConfig ,p1, p2);
            GamePanel gameView = new GamePanel(game);

            // Add debug prints
            System.out.println("Debug: Initial game state setup");

            gameView.run();

            frame.setContentPane(gameView); 
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
