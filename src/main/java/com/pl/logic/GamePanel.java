package com.pl.logic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 600;
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    private static final int DELAY = 122;
    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];
    private int bodyParts;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction;
    private boolean running = false;
    private static boolean gameOn;
    private boolean isButtonPressed;
    private Timer timer;
    private final Random random;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        direction = 'R';
        bodyParts = 6;
        applesEaten = 0;
        x[0] = UNIT_SIZE;
        y[0] = SCREEN_HEIGHT / 2;
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            //draw grid
            /*
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            */
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);

            }
            g.setColor(Color.RED);
            g.setFont(new Font("Lato", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());

        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;

        //checks if apple collides with body
        for (int i = bodyParts; i > 0; i--) {
            if (appleX == x[i] && appleY == y[i]) {
                newApple();
            }
        }
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        //checks if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        //check if head touches left border
        if (x[0] < 0) running = false;
        //check if head touches right border
        if (x[0] > SCREEN_WIDTH - 1) running = false;
        //check if head touches top border
        if (y[0] < 0) running = false;
        //check if head touches bottom border
        if (y[0] > SCREEN_HEIGHT - 1) running = false;

        if (!running) timer.stop();
    }

    public void gamePause() {
        GamePanel.gameOn = true;
        timer.stop();
    }

    public void gameResume() {
        GamePanel.gameOn = false;
        timer.start();
    }

    public void gameOver(Graphics g) {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = -UNIT_SIZE;
            y[i] = -UNIT_SIZE;
        }
        //Game Over text
        g.setColor(Color.RED);
        g.setFont(new Font("Lato", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("GameOver")) / 2, SCREEN_HEIGHT / 2);
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

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {

            /**
             buggy code below (with this you can go backward and lose game by eating body behind you)
             **/
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R' && !isButtonPressed) {
                        isButtonPressed = true;
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L' && !isButtonPressed) {
                        isButtonPressed = true;
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D' && !isButtonPressed) {
                        isButtonPressed = true;
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U' && !isButtonPressed) {
                        isButtonPressed = true;
                        direction = 'D';
                    }
                    break;
                //new game
                case KeyEvent.VK_ENTER:
                    if (!running) startGame();
                    break;
                //pause game
                case KeyEvent.VK_ESCAPE:
                    if (GamePanel.gameOn) {
                        gameResume();
                    } else {
                        gamePause();
                    }
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

            /**
             prevent going backward but the game loses smooth controls
             **/
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_UP:
                case KeyEvent.VK_DOWN:
                    isButtonPressed = false;
                    break;
            }
        }
    }
}
