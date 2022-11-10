package com.company;

import java.io.Serializable;

public class Data implements Serializable {
    private static final long serialVersionUID = 1L;

    int hp,marioX,marioY,levelX;

    public Data(int hp, int marioX, int marioY, int levelX) {
        this.hp = hp;
        this.marioX = marioX;
        this.marioY = marioY;
        this.levelX = levelX;
    }
}

