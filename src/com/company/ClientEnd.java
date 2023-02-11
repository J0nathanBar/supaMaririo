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
            Data dRecieved = null;
            dRecieved = getData(); // Reliading data from input stream



        }
    }

    // Sends to all clients to summon a bomb in the current player location of
    // playerIndex
    // Sends everyone but himself.


    private Data getData() {
        Data d = null;
        Object o;
        try {
            // reads info from handlers (might be Data or String)
            o = objectInputStream.readObject();

            if (o instanceof Data) {
                d = (Data) o;
                System.out.println("recieved: " + d);
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
        System.out.println("called send data");

//        if (reqPause) {
//            if (Map.pauseFlag == 1) {
//                d ;//= new Data(playerIndex, Constants.CODE_PAUSE);
//                objectOutputStream.writeObject(d);
//            } else {
//                d = new Data(playerIndex, Constants.CODE_NOTIFY);
//                objectOutputStream.writeObject(d);
//            }
//            reqPause = false;
//        } else {
//            // Send current direction to all other clients

            Mario thisMario = panel.getPlayers().get(playerIndex);
            d = new Data(thisMario.getX(),thisMario.getY(),panel.getLevelX(),thisMario.getHp(),playerIndex
            );
            if (d != null) {
              objectOutputStream.writeObject(d);
                System.out.println(d);
         }
     //   }
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
            e.panel = new GamePanel(500,828,e.playerIndex);
            f.add(e.panel);
            f.setSize(828,500);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setResizable(false);
            f.setVisible(true);
            f.setFocusable(false);
            System.out.println("got to here");
            e.t.start();
        }
    }

}
