package snakegame;

import javax.swing.*;

// TO RUN GAME - java -cp src snakegame.SnakeGame

public class SnakeGame extends JFrame {
    
    SnakeGame() {
        super("Snake Game");    //super should be the first statement inside a constructor
        add(new Board());
        pack();
        
        setLocationRelativeTo(null);    //it will simply check your screen and move the box to center
        setVisible(false);
    }

    public static void main(String[] args) {
        new SnakeGame().setVisible(true);
    }
}