import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        // Create the main game window
        JFrame frame = new JFrame("Snake Game");
        
        try {
            // Initialize the game panel
            GamePanel gamePanel = new GamePanel();
            frame.add(gamePanel);
            
            // Set frame properties
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null); // Center the window on the screen
            frame.setVisible(true);
            
            // Start the game (if not already started in the GamePanel constructor)
            // gamePanel.startGame();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while starting the game.");
        }
    }
}
