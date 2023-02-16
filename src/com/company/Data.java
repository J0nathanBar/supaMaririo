package com.company;

import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable {
    private static final long serialVersionUID = 1L;

  private Integer marioX,marioY,levelX;
   private Byte hp,playerIndex;
   private ArrayList<Integer> deadMonsters;
    public Data(Byte playerIndex) {
        this.playerIndex = playerIndex;
    }

    public Data(Integer marioX, Integer marioY, Integer levelX, Byte hp, Byte playerIndex,ArrayList<Integer> deadMonsters) {
        this.marioX = marioX;
        this.marioY = marioY;
        this.levelX = levelX;
        this.deadMonsters = deadMonsters;
        this.hp = hp;
        this.playerIndex = playerIndex;
    }

    public Data(Data data) {
        this.playerIndex = data.playerIndex;
    }

    public Integer getMarioX() {
        return marioX;
    }

    public void setMarioX(Integer marioX) {
        this.marioX = marioX;
    }

    public Integer getMarioY() {
        return marioY;
    }

    public void setMarioY(Integer marioY) {
        this.marioY = marioY;
    }

    public Integer getLevelX() {
        return levelX;
    }

    public void setLevelX(Integer levelX) {
        this.levelX = levelX;
    }

    public Byte getHp() {
        return hp;
    }

    public void setHp(Byte hp) {
        this.hp = hp;
    }

    public Byte getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(Byte playerIndex) {
        this.playerIndex = playerIndex;
    }

    public ArrayList<Integer> getDeadMonsters() {
        return deadMonsters;
    }

    public void setDeadMonsters(ArrayList<Integer> deadMonsters) {
        this.deadMonsters = deadMonsters;
    }

    @Override
    public String toString() {
        return "Data{" +
                "marioX=" + marioX +
                ", marioY=" + marioY +
                ", levelX=" + levelX +
                ", hp=" + hp +
                ", playerIndex=" + playerIndex +
                ", deadMonsters=" + deadMonsters +
                '}';
    }
}

