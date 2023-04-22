package com.company;

public final class Constants {

    public static final byte pauseGame = -1;
    public static final byte resumeGame = -2;
    public static final byte noChange = 0;
    public static final int floorY = 425;
    public static final int floorH = 100;
    public static final int pipeWidth = 30;
    public static final int MarioHalfWay = 300;
    public static final int blockHeight = 30;
    public static final int columnWidth = 16;




    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
