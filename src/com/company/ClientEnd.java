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
        connectToServer(port);
    }

    public void connectToServer(int port) throws IOException {
        socket = new Socket("localhost", port);

        inputStream = socket.getInputStream();
        objectInputStream = new ObjectInputStream(inputStream);

        outputStream = socket.getOutputStream();
        objectOutputStream = new ObjectOutputStream(outputStream);

        Data d = null;
        // recieve player index from handler
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
    }

    @Override
    public void run() {
        System.out.println("running");
        while (true) {
            try {
                Constants.sleep(1);
                sendData(); // sel1nd data to everyone else except the one who sent
            } catch (IOException e) {

                e.printStackTrace();
            }
            Constants.sleep(1);
            Data dRecieved;
            dRecieved = getData(); // Reliading data from input stream
            if (dRecieved != null) {


                if (dRecieved.getGameStatus() == Constants.pauseGame) {
                    System.out.println("pause");
                    panel.setPause(true);
                } else if (dRecieved.getGameStatus()==Constants.resumeGame){
                    System.out.println("resume");
                    panel.setPause(false);
                    panel.resumeGame();
                }
            } else if (dRecieved.getMarioX() != null) {
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
                    if (c instanceof Goomba) {
                        ((Goomba) c).killGoomba();
                    }
                }
            }
        }
    }


    private Data getData() {
        Data d = null;
        Object o;
        try {
            // reads info from handlers (might be Data or String)
            o = objectInputStream.readObject();

            if (o instanceof Data) {
                d = (Data) o;
                //   System.out.println("recieved: " + d);
            } else {
                if (o instanceof String) {
                    if (o.equals("Add Player")) {
                        //      m.players.add(new Bomberman(m));
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generatelid catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
                if (!panel.getMonsters().get(i).isGAlive()) {
                    System.out.println("goomba " + i + " dead");
                    monsters.add(i);
                }
            }
            d = new Data(thisMario.getX(), thisMario.getY(), panel.getLevelX(), thisMario.getHp(), playerIndex, monsters
            );

            if (d != null) {
                objectOutputStream.writeObject(d);
            }
        }

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ClientEnd e = new ClientEnd();
        JFrame f = new JFrame("SuperMario");
        Object obj;

        // Reads amount of players connected from server
        obj = e.objectInputStream.readObject();
        if (obj instanceof Integer) {
            int playerCount = (Integer) obj;
            System.out.println("istg");
            e.panel = new GamePanel(500, 828, e.playerIndex, e);
            f.add(e.panel);
            f.setSize(828, 500);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setResizable(false);
            f.setVisible(true);
            f.setFocusable(false);
            System.out.println("got to here");
            e.t.start();
        }
    }

}
