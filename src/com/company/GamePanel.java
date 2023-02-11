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
    private Mario mario;
    private Image background;
    private int height, width, levelX;
    private ArrayList<Platform> platforms;
    private ArrayList<Mario> players;
    private ArrayList<Creature> monsters;
    private static final int floorY = 425, floorH = 100;
    private static int pauseflag;
    private Byte playerIndex;

    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;


    //   Graphics g;
    public GamePanel(int height, int width,Byte playerIndex) throws IOException {
        levelX = 0;
        this.height = height;
        this.width = width;
        System.out.println("eggies");
        this.playerIndex = playerIndex;
       // mario = new Mario(this);
        players = new ArrayList<>();
        //players.set(playerIndex,new Mario(this));
        if(playerIndex ==0)
            players.add(new Mario(this));
        else {
            players.add(null);
            players.add(new Mario(this));
        }

        //players.set(playerIndex,new Mario(this));
        Timer t = new Timer(5, this);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();
        background = new ImageIcon("level1.png").getImage();
      //  mario.start();
        setPlatforms();
        setMonsters();

     //   connectToServer(port);
    }

    public void connectToServer(int port) throws IOException {
        socket = new Socket("localhost", port);

        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        objectInputStream = new ObjectInputStream(inputStream);
        objectOutputStream = new ObjectOutputStream(outputStream);

    }

    public ArrayList<Creature> getMonsters() {
        return monsters;
    }

    public void setMonsters() {
        monsters = new ArrayList<>();
        monsters.add(new Goomba(this, 200, 200));
        for (Creature c : monsters) {
            c.start();

        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, levelX, 0, width * 4, height * 2, null);
        for (Platform p : platforms) {
            p.drawp(g);
        }
        for (Creature c : monsters) {
            c.drawCreature(g);
        }


        for (Mario mario:players
             ) {
            if(mario != null)
                mario.drawCreature(g);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_RIGHT)
            players.get(playerIndex).moveX();
        repaint();

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_RIGHT) {
            players.get(playerIndex).setDir(true);
            if (canMove(1)) {
                players.get(playerIndex).moveX();


                if (players.get(playerIndex).getX() >= 300 && levelX > -1800) {
                 //   System.out.println("X: " + levelX);
                    levelX -= players.get(playerIndex).dx;
                    players.get(playerIndex).updateRect();
                    for (Platform p : platforms) {
                        p.moveX();
                        p.updateRect();
                    }
                    for (Creature c:monsters){
                         c.moveX();
                        c.updateRect();
                        if (c.getX() < 0)
                            monsters.remove(c);
                    }
                }
            }
        } else if (code == KeyEvent.VK_LEFT) {
            if (canMove(-1)) {
                players.get(playerIndex).setDir(false);
                players.get(playerIndex).moveX();
            }
        } else if (code == KeyEvent.VK_UP) {
            if (players.get(playerIndex).isCanJump()) {
                players.get(playerIndex).setJumping(true);
                players.get(playerIndex).moveY();
                players.get(playerIndex).setCanJump(false);
            }
        } else if (code == KeyEvent.VK_DOWN) {
            players.get(playerIndex).moveControl(-1);
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
        platforms.add(new Platform(-10, floorY, 1000, floorH, this, players.get(playerIndex)));
        platforms.add(new Platform(300 + 140, 365, 30, 525 - 440, this, players.get(playerIndex)));


        for (Platform platform : platforms) {
            platform.start();
        }
    }

    boolean canMove(int dir) {//right:: dir =1, left: dir = -1
        for (Platform p : platforms) {
         //   System.out.println("platfrom:: " + p.getRect().getMinX());
        //    System.out.println("mario:: " + mario.getRect().getMaxX());
            if (p.runsTo(dir))
                return false;
        }
        return true;
    }


    @Override
    public void run() {

        Data d1, d2;
        ;

        try {
            // mind the gap  MS 29-7-2022
            // update 11-8-2022
            // Must use a copy of p1
            // To send each time a new created object
            // instead of the "same" object p1


//            d1 = new Data(
//            objectOutputStream.writeObject(d1);

            d2 = (Data) objectInputStream.readObject();

            //System.out.println(d2);

        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            System.exit(0);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Mario getMario() {
        return players.get(playerIndex);
    }

//    //public void setMario(Mario mario) {
//        this.mario = mario;
//    }


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

    public void setPlatforms(ArrayList<Platform> platforms) {
        this.platforms = platforms;
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
        // Start players array at 1 instead of 0
        if (players.size() == 0) {
            players.add(0, null);
        }
        // Adding amount of players connected (Recieved from server)
        for (int i = 1; i <= playerCount; i++) {
            players.add(new Mario(this));

        }
        //Set the player which belongs to this client as being controlled by the keyboard
        //	(and not by data from the server)
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

    public static int getPauseflag() {
        return pauseflag;
    }

    public static void setPauseflag(int pauseflag) {
        GamePanel.pauseflag = pauseflag;
    }

    public Byte getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(Byte playerIndex) {
        this.playerIndex = playerIndex;
    }
}
