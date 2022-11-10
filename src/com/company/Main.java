package com.company;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void init() throws IOException {
        JFrame frame = new JFrame();
        GamePanel panel = new GamePanel(600,600,8888);
        frame.add(panel);
        frame.setSize(600,600);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
	// write your code her
        init();






    }
}
