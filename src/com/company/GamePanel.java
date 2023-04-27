package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.sql.Time;
import java.util.ArrayList;

public class GamePanel extends JPanel implements KeyListener, ActionListener, Runnable {
    private Image background;
    private JLabel hpLabel, scoreLabel;
    private int height, width, levelX;
    private ArrayList<Platform> platforms;
    private ArrayList<Mario> players;
    private ArrayList<Creature> monsters;
    private int score;
    private Byte playerIndex;
    private boolean pause;
    ClientEnd client;

    int level;
    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;


    //   Graphics g;
    public GamePanel(int height, int width, Byte playerIndex, ClientEnd client) throws IOException {
        levelX = 0;
        score = 0;
        this.height = height;
        this.width = width;
        this.client = client;
        level = 1;
        pause = false;
        this.playerIndex = playerIndex;
        // mario = new Mario(this);
        players = new ArrayList<>();
        //players.set(playerIndex,new Mario(this));
        if (playerIndex == 0) {
            players.add(new Mario(this));
            players.add(null);
        } else {
            players.add(null);
            players.add(new Mario(this));
        }

        //players.set(playerIndex,new Mario(this));
        Timer t = new Timer(5, this);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();
        background = new ImageIcon("marioLevel.png").getImage();

        setPlatforms();
        setMonsters();
        players.get(playerIndex).start();

        hpLabel = new JLabel("HP: 3");
        hpLabel.setBounds(10, 10, 100, 20);
        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setBounds(130, 10, 100, 20);
        add(scoreLabel);
        add(hpLabel);
    }

    public ArrayList<Creature> getMonsters() {
        return monsters;
    }

    public void setMonsters() {
        monsters = new ArrayList<>();
        if (level == 1) {
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), 200, 180));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 340, 180));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 460, 180));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 560, 180));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 1030, 135));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 1340, 180));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 1440, 180));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 1540, 180));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 2420, 180));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 2460, 180));

        } else if (level == 2) {
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), 200, 180));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 340, 180));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 460, 180));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 560, 180));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 1030, 135));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 1340, 180));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 1440, 180));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 1540, 180));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 2420, 180));
            monsters.add(new Goomba(this, getPlayers().get(playerIndex), Constants.MarioHalfWay + 2460, 180));

        }
        for (Creature c : monsters) {
            c.start();

        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, levelX, 0, width * 4, height * 2, null);
        String s = "hp: " + players.get(playerIndex).getHp();
        hpLabel.setText(s);
        for (Platform p : platforms) {
            p.drawp(g);
        }
        for (Creature c : monsters) {
            c.drawCreature(g);
        }
        for (Mario mario : players
        ) {
            if (mario != null)
                mario.drawCreature(g);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        int code = e.getKeyCode();

        repaint();

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_RIGHT && !pause) {
            players.get(playerIndex).setDir(true);

            if (canMove(1)) {
                players.get(playerIndex).moveX();
                if (players.get(playerIndex).getX() - levelX >= 3120) {
                    if (level == 1) {
                        level2();

                    } else if (level == 2) {

                    }
                } else if (players.get(playerIndex).getX() < 300 || levelX < -2470) {
                    players.get(playerIndex).updateRect();
                } else {
                    //   System.out.println("X: " + levelX);
                    levelX -= players.get(playerIndex).dx;
                    //  players.get(playerIndex).updateRect();

                    for (Platform p : platforms) {
                        synchronized (p) {
                            p.moveX();
                            p.updateRect();
                        }
                    }
                    for (Creature c : monsters) {
                        c.moveX();
                        if (c instanceof Goomba)
                            ((Goomba) c).checkCollision();


                    }
                    for (Mario m : players) {
                        if (m != null && m != players.get(playerIndex))
                            m.setX(m.getX() - m.dx);
                    }
                }
            }
        } else if (code == KeyEvent.VK_LEFT && !pause) {
            if (canMove(-1)) {
                players.get(playerIndex).setDir(false);
                players.get(playerIndex).moveX();
            }
        } else if (code == KeyEvent.VK_UP) {
//            if (players.get(playerIndex).isCanJump()) {
//                players.get(playerIndex).setJumping(true);
//                players.get(playerIndex).moveY();
//                players.get(playerIndex).setCanJump(false);
//            }
            players.get(playerIndex).moveControl(1);
        } else if (code == KeyEvent.VK_DOWN && !pause) {
            players.get(playerIndex).moveControl(-1);
        } else if (code == KeyEvent.VK_P) {
            pause = !pause;
            client.setReqPause(pause);
            if (!pause)
                resumeGame();
        } else if (code == KeyEvent.VK_C) {
            System.out.println("mario x: " + players.get(playerIndex).x + " mario y: " + players.get(playerIndex).y);
            System.out.println("level x: " + levelX);
        }

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public int getLevelX() {
        return levelX;
    }

    public void setLevelX(int levelX) {
        this.levelX = levelX;
    }

    public void setPlatforms() {
        platforms = new ArrayList<>();
        if (level == 1) {
            platforms.add(new Platform(-10, Constants.floorY, 1100 - 20, Constants.floorH, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 140, 365, Constants.pipeWidth, 525 - 440, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 15, 300, 80, Constants.blockHeight, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 295, 335, Constants.pipeWidth, Constants.floorY + 20 - 335, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 420, 300, Constants.pipeWidth, Constants.floorY + 20 - 300, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 595, 300, Constants.pipeWidth, Constants.floorY + 20 - 300, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 810, Constants.floorY, 1050 - 810, Constants.floorH, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1095, Constants.floorY, 2100 - 1095 + 5, Constants.floorH, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 2130, Constants.floorY, 1000, Constants.floorH, this, players.get(playerIndex)));

            platforms.add(new Platform(Constants.MarioHalfWay + 905, 300, 55, Constants.blockHeight, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 950, 165, 130, Constants.blockHeight, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1130, 165, 45, Constants.blockHeight, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1173, 300, 20, Constants.blockHeight, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1270, 300, 30, Constants.blockHeight, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1554, 300, 20, Constants.blockHeight, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1600, 165, 45, Constants.blockHeight, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1710, 165, 62, Constants.blockHeight, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1725, 300, 29, Constants.blockHeight, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 2260, 365, Constants.pipeWidth, Constants.floorY + 20 - 365, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 2335, 300, 63, Constants.blockHeight, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 2480 + 30, 365, Constants.pipeWidth, Constants.floorY + 20 - 365, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1801, 405, Constants.columnWidth, Constants.floorY - 405 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1801 + 3 + Constants.columnWidth, 365, Constants.columnWidth, Constants.floorY - 365 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1801 + 4 + 2 * Constants.columnWidth, 335, Constants.columnWidth, Constants.floorY - 335 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1801 + 5 + 3 * Constants.columnWidth, 300, Constants.columnWidth, Constants.floorY - 300 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1895, 300, Constants.columnWidth, Constants.floorY - 300 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1895 + 3 + Constants.columnWidth, 335, Constants.columnWidth, Constants.floorY - 335 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1895 + 4 + 2 * Constants.columnWidth, 365, Constants.columnWidth, Constants.floorY - 365 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1895 + 5 + 3 * Constants.columnWidth, 405, Constants.columnWidth, Constants.floorY - 405 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 2020, 405, Constants.columnWidth, Constants.floorY - 405 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 2020 + 3 + Constants.columnWidth, 365, Constants.columnWidth, Constants.floorY - 365 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 2020 + 4 + 2 * Constants.columnWidth, 335, Constants.columnWidth, Constants.floorY - 335 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 2020 + 5 + 3 * Constants.columnWidth, 300, Constants.columnWidth, Constants.floorY - 300 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 2020 + 5 + 4 * Constants.columnWidth, 300, Constants.columnWidth, Constants.floorY - 300 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 2130, 300, Constants.columnWidth, Constants.floorY - 300 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 2130 + 3 + Constants.columnWidth, 335, Constants.columnWidth, Constants.floorY - 335 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 2130 + 4 + 2 * Constants.columnWidth, 365, Constants.columnWidth, Constants.floorY - 365 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 2130 + 5 + 3 * Constants.columnWidth, 405, Constants.columnWidth, Constants.floorY - 405 + 10, this, players.get(playerIndex)));
            int sp = 2480 + 360;
            platforms.add(new Platform(sp + 3 + Constants.columnWidth, 365, Constants.columnWidth, Constants.floorY - 365 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(sp + 4 + 2 * Constants.columnWidth, 335, Constants.columnWidth, Constants.floorY - 335 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(sp + 5 + 3 * Constants.columnWidth, 300, Constants.columnWidth, Constants.floorY - 300 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(sp + 5 + 4 * Constants.columnWidth, 265, Constants.columnWidth, Constants.floorY - 300 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(sp + 5 + 5 * Constants.columnWidth, 230, Constants.columnWidth, Constants.floorY - 300 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(sp + 6 * Constants.columnWidth, 200, Constants.columnWidth, Constants.floorY - 300 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(sp + 7 * Constants.columnWidth, 165, 32, Constants.floorY - 300 + 10, this, players.get(playerIndex)));
        } else if (level == 2) {
            platforms.add(new Platform(-10, Constants.floorY, Constants.MarioHalfWay + 150, Constants.floorH, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 175, Constants.floorY, 785 - 175, Constants.floorH, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 840, Constants.floorY, 1770 - 840, Constants.floorH, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1800, Constants.floorY, 2120 - 1800, Constants.floorH, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 2165, Constants.floorY, 2205 - 2165, Constants.floorH, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 2230, Constants.floorY, 2340 - 2230, Constants.floorH, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 2370, Constants.floorY, 1000, Constants.floorH, this, players.get(playerIndex)));

            platforms.add(new Platform(290, 335, Constants.pipeWidth, Constants.floorY + 20 - 335, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1315, 295, Constants.pipeWidth, Constants.floorY + 20 - 295, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1535, 295, Constants.pipeWidth, Constants.floorY + 20 - 295, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 1970, 360, Constants.pipeWidth, Constants.floorY + 20 - 360, this, players.get(playerIndex)));

            platforms.add(new Platform(Constants.MarioHalfWay + 1130, 335, Constants.columnWidth, Constants.floorY - 335 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(Constants.MarioHalfWay + 2330, 335, Constants.columnWidth, Constants.floorY - 335 + 10, this, players.get(playerIndex)));

            platforms.add(new Platform(Constants.MarioHalfWay + 1770, 300, 30, Constants.blockHeight, this, players.get(playerIndex)));
            platforms.add(new Platform(580 + 2480, 295, 20, Constants.blockHeight + 5, this, players.get(playerIndex)));

            platforms.add(new Platform(410 + 2480, 395, Constants.columnWidth, Constants.floorY - 395 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(430 + 2480, 360, Constants.columnWidth, Constants.floorY - 360 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(440 + 2480, 335, Constants.columnWidth, Constants.floorY - 335 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(455 + 2480, 295, Constants.columnWidth, Constants.floorY - 295 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(470 + 2480, 255, Constants.columnWidth, Constants.floorY - 255 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(480 + 2480, 235, Constants.columnWidth, Constants.floorY - 235 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(500 + 2480, 195, Constants.columnWidth, Constants.floorY - 195 + 10, this, players.get(playerIndex)));
            platforms.add(new Platform(510 + 2480, 165, 2 * Constants.columnWidth, Constants.floorY - 155 + 10, this, players.get(playerIndex)));

        }

        for (Platform platform : platforms) {
            platform.start();
        }
    }

    boolean canMove(int dir) {//right:: dir =1, left: dir = -1
        for (Platform p : platforms) {
            if (p.runsTo(dir))
                return false;
        }
        return true;
    }


    @Override
    public void run() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    public Mario getMario() {
        return players.get(playerIndex);
    }

    public void setBackground(Image background) {
        this.background = background;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public ArrayList<Platform> getPlatforms() {
        return platforms;
    }


    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public void setObjectInputStream(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    private void initPlayers(int playerCount) {

        if (players.size() == 0) {
            players.add(0, null);
        }
        for (int i = 1; i <= playerCount; i++) {
            players.add(new Mario(this));

        }
        //set the player be controlled locally
        players.get(playerIndex).setControlled(true);
        System.out.println(players);
    }

    private void startPlayers() {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i) != null) {
                players.get(i).start();
            }
        }

    }

    public ArrayList<Mario> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Mario> players) {
        this.players = players;
    }

    public void setMonsters(ArrayList<Creature> monsters) {
        this.monsters = monsters;
    }


    public Byte getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(Byte playerIndex) {
        this.playerIndex = playerIndex;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public void resumeGame() {
        synchronized (players.get(playerIndex)) {
            players.get(playerIndex).notify();
        }

        for (Creature c : monsters) {
            synchronized (c) {
                c.notify();
            }
        }

        for (Platform p : platforms) {
            synchronized (p) {
                p.notify();
            }
        }
    }


    public int getScore() {
        return score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void level2() {
        level = 2;
        levelX = 0;
        background = new ImageIcon("level2Mario.png").getImage();
        setPlatforms();
        setMonsters();
        players.set(playerIndex, new Mario(this));
        players.get(playerIndex).start();
    }
}

