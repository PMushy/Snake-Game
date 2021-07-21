package com.pl.logic;

import javax.swing.*;

public class GameFrame extends JFrame {
    private static final String GAME_NAME = "Snake Game";

    public GameFrame() {
        this.add(new GamePanel());
        this.setTitle(GAME_NAME);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

}
