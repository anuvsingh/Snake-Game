package snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Board extends JPanel implements ActionListener {

    private Image mouse;
    private Image dot;
    private Image head;

    private final int BOARD_SIZE = 500;
    private final int DOT_SIZE = 20; // Increased for better visibility
    private final int ALL_DOTS = 900; // Maximum dots (BOARD_SIZE / DOT_SIZE)^2
    private final int RANDOM_POSITION = BOARD_SIZE / DOT_SIZE;

    private int mouse_x;
    private int mouse_y;

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;

    private boolean inGame = true;

    private int dots;
    private Timer timer;

    private Image headUp;
    private Image headDown;
    private Image headLeft;
    private Image headRight;

    private final int GAP_FACTOR = 5; // Controls spacing between snake segments

    Board() {
        addKeyListener(new TAdapter());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        setFocusable(true);

        loadImages();
        initGame();
    }

    private void loadImages() {
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("snakegame/icons/mouse.png"));
        mouse = i1.getImage();

        ImageIcon i2 = new ImageIcon(ClassLoader.getSystemResource("snakegame/icons/shape.png"));
        dot = i2.getImage();

        ImageIcon i3 = new ImageIcon(ClassLoader.getSystemResource("snakegame/icons/snake_right.png"));
        head = i3.getImage();

        headUp = new ImageIcon(ClassLoader.getSystemResource("snakegame/icons/snake_up.png")).getImage();
        headDown = new ImageIcon(ClassLoader.getSystemResource("snakegame/icons/snake_down.png")).getImage();
        headLeft = new ImageIcon(ClassLoader.getSystemResource("snakegame/icons/snake_left.png")).getImage();
        headRight = new ImageIcon(ClassLoader.getSystemResource("snakegame/icons/snake_right.png")).getImage();

        head = headRight; // Initial direction
    }

    private void initGame() {
        dots = 3; // Initial snake length

        // Initialize the snake in a straight line, perfectly aligned to the grid
        for (int i = 0; i < dots; i++) {
            x[i] = 50 - i * DOT_SIZE * GAP_FACTOR; // Horizontally aligned
            y[i] = 50; // Same row
        }

        locateApple(); // Place the apple randomly

        timer = new Timer(140, this);
        timer.start();
    }

    private void locateApple() {
        mouse_x = (int) (Math.random() * RANDOM_POSITION) * DOT_SIZE;
        mouse_y = (int) (Math.random() * RANDOM_POSITION) * DOT_SIZE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (inGame) {
            g.drawImage(mouse, mouse_x, mouse_y, DOT_SIZE, DOT_SIZE, this); // Apple
            for (int i = 0; i < dots; i++) {
                if (i == 0) {
                    g.drawImage(head, x[i], y[i], DOT_SIZE, DOT_SIZE, this); // Adjust head size
                } else {
                    g.drawImage(dot, x[i], y[i], DOT_SIZE, DOT_SIZE, this); // Adjust body size
                }
            }
            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
        }
    }

    private void gameOver(Graphics g) {
        String msg = "Game Over!";
        Font font = new Font("SAN_SERIF", Font.BOLD, 14);
        FontMetrics metrics = getFontMetrics(font);

        g.setColor(Color.BLACK);
        g.setFont(font);
        g.drawString(msg, (BOARD_SIZE - metrics.stringWidth(msg)) / 2, BOARD_SIZE / 2);
    }

    private void move() {
        // Start from the tail and move each segment to the position of the previous one
        for (int i = dots - 1; i > 0; i--) {
            // Add a gap factor to create more space between segments
            x[i] = x[i - 1] - (x[i - 1] - x[i]) / GAP_FACTOR;
            y[i] = y[i - 1] - (y[i - 1] - y[i]) / GAP_FACTOR;
        }

        // Move the head in the current direction
        if (leftDirection) {
            x[0] -= DOT_SIZE;
            head = headLeft;
        } else if (rightDirection) {
            x[0] += DOT_SIZE;
            head = headRight;
        } else if (upDirection) {
            y[0] -= DOT_SIZE;
            head = headUp;
        } else if (downDirection) {
            y[0] += DOT_SIZE;
            head = headDown;
        }
    }

    private void checkApple() {
        if ((x[0] == mouse_x) && (y[0] == mouse_y)) {
            dots++;
            locateApple();
        }
    }

    private void checkCollision() {
        for (int i = dots; i > 0; i--) {
            if ((i > 4) && (x[0] == x[i] && y[0] == y[i])) { // Colliding with itself
                inGame = false;
            }
        }

        if (x[0] >= BOARD_SIZE || x[0] < 0 || y[0] >= BOARD_SIZE || y[0] < 0) {
            inGame = false; // Out of bounds
        }

        if (!inGame) {
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkApple();
            checkCollision();
            move();
        }
        repaint();
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT && !rightDirection) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }
            if (key == KeyEvent.VK_RIGHT && !leftDirection) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }
            if (key == KeyEvent.VK_UP && !downDirection) {
                upDirection = true;
                leftDirection = false;
                rightDirection = false;
            }
            if (key == KeyEvent.VK_DOWN && !upDirection) {
                downDirection = true;
                leftDirection = false;
                rightDirection = false;
            }
        }
    }
}