package com.company;


import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class ClientEnd extends JPanel implements Runnable {

    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    // Map m;
    GamePanel panel;
    Thread t;
    byte playerIndex;
    boolean connected;

    public static boolean isReqPause() {
        return reqPause;
    }

    public static void setReqPause(boolean reqPause) {
        ClientEnd.reqPause = reqPause;
    }

    static boolean reqPause = false;

    public ClientEnd() throws IOException {
        t = new Thread(this);
        int port = 8083;
        connected = connectToServer(port);
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean connectToServer(int port) throws IOException {
        try {
            socket = new Socket("localhost", port);

            inputStream = socket.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);

            outputStream = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);

            Data d = null;
            // receive player index from handler
            try {
                Object o = objectInputStream.readObject();
                if (o instanceof Data) {
                    d = (Data) o;
                    //System.out.println(d);
                }


            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.playerIndex = d.getPlayerIndex();
        } catch (IOException e) {
            System.out.println("couldn't connect to the server");
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Constants.sleep(1);
                sendData();
            } catch (IOException e) {

                e.printStackTrace();
                break;
            }
            Constants.sleep(1);
            Data dRecieved;
            dRecieved = getData();
            if (dRecieved != null) {

                if (dRecieved.getVictory() != null && dRecieved.getVictory() && !panel.isVictory()) {
                    panel.setVictory();
                    if (dRecieved.getGameStatus() == Constants.pauseGame) {
                        System.out.println("pause");
                        panel.setPause(true);
                    } else if (dRecieved.getGameStatus() == Constants.resumeGame) {
                        System.out.println("resume");
                        panel.setPause(false);
                        panel.resumeGame();
                    } else if (dRecieved.getLevel() != null && dRecieved.getLevel() != (byte) panel.getLevel()) {
                        panel.level2();

                    }

                } else {
                    ArrayList<Mario> arr = panel.getPlayers();

                    Byte index = dRecieved.getPlayerIndex();
                    int x = dRecieved.getMarioX() - dRecieved.getLevelX();
                    int y = dRecieved.getMarioY();
                    Byte hp = dRecieved.getHp();
                    if (arr.get(index) == null) {
                        arr.set(index, new Mario(x, y, hp, panel));
                    } else {
                        Mario m = panel.getPlayers().get(dRecieved.getPlayerIndex());
                        m.setX(x + panel.getLevelX());
                        m.setY(y);
                        m.setHp(hp);
                    }
                    ArrayList<Integer> dead = dRecieved.getDeadMonsters();
                    for (int i : dead) {
                        Creature c = panel.getMonsters().get(i);
                        if (c instanceof Goomba && c.isGAlive()) {
                            ((Goomba) c).killGoomba();
                        }
                    }
                }
            }
        }
    }


    private Data getData() {
        Data d = null;
        Object o;
        try {
            o = objectInputStream.readObject();
            d = (Data) o;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return d;

    }

    private void sendData() throws IOException {
        Data d = null;
        if (reqPause) {
            System.out.println("changing status");
            if (panel.isPause()) {

                d = new Data(playerIndex, Constants.pauseGame);
                objectOutputStream.writeObject(d);
            } else {
                d = new Data(playerIndex, Constants.resumeGame);
                objectOutputStream.writeObject(d);
            }
            reqPause = false;
        } else {
            Mario thisMario = panel.getPlayers().get(playerIndex);
            ArrayList<Integer> monsters = new ArrayList<>();
            for (int i = 0; i < panel.getMonsters().size(); i++) {
                if (!panel.getMonsters().get(i).isGAlive() && panel.getMonsters().get(i).isNewDeath()) {
                    monsters.add(i);
                    panel.getMonsters().get(i).setNewDeath(false);
                }
            }
            d = new Data(thisMario.getX(), thisMario.getY(), panel.getLevelX(), thisMario.getHp(), playerIndex, monsters, (byte) panel.getLevel(), panel.isVictory());

            if (d != null) {
                objectOutputStream.writeObject(d);
            }
        }

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ClientEnd e = new ClientEnd();
        if (e.isConnected()) {
            JFrame f = new JFrame("mario");
            Object obj;

            // Reads amount of players connected from server
            try {
                obj = e.objectInputStream.readObject();
                if (obj instanceof Integer) {
                    int playerCount = (Integer) obj;
                    e.panel = new GamePanel(500, 828, e.playerIndex, e);
                    f.add(e.panel);
                    f.setSize(828, 500);
                    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    f.setResizable(false);
                    f.setVisible(true);
                    f.setFocusable(false);
                    e.t.start();
                }
            } catch (IOException ex) {
                System.out.println("disconnected from the server");
                return;
            }
        }

    }
}
