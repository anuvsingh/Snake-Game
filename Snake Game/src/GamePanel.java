import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 600;
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int START_DELAY = 150; // Slow initial speed
    private static final int HEADER_HEIGHT = 50; // Height for score and title header

    private final int[] x = new int[GAME_UNITS]; // Snake's x-coordinates
    private final int[] y = new int[GAME_UNITS]; // Snake's y-coordinates
    private int bodyParts = 3; // Initial length of the snake
    private int applesEaten = 0;
    private int appleX;
    private int appleY;
    private char direction = 'R'; // 'R' = right, 'L' = left, 'U' = up, 'D' = down
    private boolean running = false;
    private Timer timer;
    private Random random;

    private JButton restartButton;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(245, 245, 220));
        this.setFocusable(true);
        this.setLayout(null); // Use null layout for absolute positioning
        this.addKeyListener(new MyKeyAdapter());

        // Create Restart Button
        restartButton = new JButton("Restart");
        restartButton.setBounds(SCREEN_WIDTH / 2 - 75, SCREEN_HEIGHT - 100, 150, 40);
        restartButton.setBackground(new Color(34, 139, 34));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFont(new Font("Arial", Font.BOLD, 20));
        restartButton.setFocusPainted(false);
        restartButton.setVisible(false); // Initially hidden
        restartButton.addActionListener(e -> startGame());
        this.add(restartButton);

        startGame();
    }

    public void startGame() {
        bodyParts = 3;
        applesEaten = 0;
        direction = 'R';
        x[0] = SCREEN_WIDTH / 2; // Center starting position
        y[0] = HEADER_HEIGHT + UNIT_SIZE; // Start below the header
        newApple();
        running = true;
        timer = new Timer(START_DELAY, this);
        timer.start();
        restartButton.setVisible(false); // Hide restart button
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (running) {
            // Draw header background
            g.setColor(new Color(34, 34, 34));
            g.fillRect(0, 0, SCREEN_WIDTH, HEADER_HEIGHT);

            // Draw title
            g.setColor(new Color(255, 215, 0));
            g.setFont(new Font("Impact", Font.BOLD, 30));
            g.drawString("Snake Game", 20, 35);

            // Draw score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Impact", Font.BOLD, 30));
            g.drawString("Score: " + applesEaten, SCREEN_WIDTH - 150, 35);

            // Draw apple
            g.setColor(new Color(255, 80, 80));
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) { // Head
                    g.setColor(new Color(0, 200, 0));
                } else { // Body
                    g.setColor(new Color(0, 150, 0));
                }
                g.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 10, 10);
            }
        } else {
            gameOver(g);
        }
    }

    private void newApple() {
        do {
            appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            appleY = random.nextInt((int) ((SCREEN_HEIGHT - HEADER_HEIGHT) / UNIT_SIZE)) * UNIT_SIZE + HEADER_HEIGHT;
        } while (appleY < HEADER_HEIGHT);
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] -= UNIT_SIZE;
                break;
            case 'D':
                y[0] += UNIT_SIZE;
                break;
            case 'L':
                x[0] -= UNIT_SIZE;
                break;
            case 'R':
                x[0] += UNIT_SIZE;
                break;
        }
    }

    private void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
            updateSpeed(); // Adjust speed based on the new score
        }
    }

    private void updateSpeed() {
        int newDelay = START_DELAY - (applesEaten * 5);
        newDelay = Math.max(newDelay, 50); // Ensure minimum delay
        timer.setDelay(newDelay);
    }

    private void checkCollisions() {
        // Check if the head collides with the body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        // Check if the head touches the borders
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < HEADER_HEIGHT || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over",
                (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 3);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        g.drawString("Score: " + applesEaten,
                (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, SCREEN_HEIGHT / 2);

        restartButton.setVisible(true); // Show restart button
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R') direction = 'L';
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L') direction = 'R';
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D') direction = 'U';
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U') direction = 'D';
                }
            }
        }
    }
}
