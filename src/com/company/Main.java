package com.company;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void init() throws IOException {
        JFrame frame = new JFrame();
        GamePanel panel = new GamePanel(500,828,8888);
        frame.add(panel);
        frame.setSize(828,500);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
	// write your code her
        init();






    }
}
