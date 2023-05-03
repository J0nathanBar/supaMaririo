package com.company;

import org.w3c.dom.css.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Mario extends Creature {

    private boolean jumping, canJump, controlled;
    private int jumpcount;


    public Mario(GamePanel panel) {
        defaultValues();
        this.panel = panel;
        x = 0;
        y = 295;
        size = 32;
        width = 600;
        height = 600;
        alive = true;


    }


    public Mario(int x, int y, Byte hp, GamePanel panel) {
        defaultValues();
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.panel = panel;
        size = 32;
        width = 600;
        height = 600;
    }


    protected void defaultValues() {
        initArr();
        rect = new Rectangle(x, y, size, size);
        jumping = false;
        canJump = true;
        jumpcount = 0;
        hp = 3;
        dx = 10;
        dy = -20;
        dir = true;
        standing = false;
    }


    @Override
    protected void animate(int start, int end) {
        index++;
        if (index > end)
            index = start;
    }

    protected void initArr() {
        index = 0;
        images = new ArrayList<>();
        images.add(new ImageIcon("mario_stand.png").getImage());
        images.add(new ImageIcon("mario_run.png").getImage());
    }

    @Override
    public void moveX() {


        if ((dir && (x < width / 2 || (panel.getLevelX() < -2470 && x < 640))) || (!dir && x > 0)) {
            super.moveX();
        }


        animate(0, 1);

    }

    @Override
    public boolean interacts(Creature c) {
        return false;
    }

    @Override
    public boolean touches(Platform p) {
        return rect.intersects(p.getRect());
    }

    @Override
    public void run() {
        while (true) {
            super.run();
            setStanding();
            checkJumpStatus();
            try {
                sleep(60);
            } catch (Exception e) {
            }
        }
    }

    public boolean isJumping() {
        return jumping;
    }

    public void moveControl(int i) {
        y += dy * i;
    }

    public void setJumping(boolean jumping) {
        this.standing = false;
        this.jumping = jumping;
    }


    public boolean isCanJump() {
        return canJump;
    }

    public void setCanJump(boolean canJump) {
        if (canJump) {
            jumpcount = 0;
            setJumping(false);
        }
        this.canJump = canJump;
    }

    public int getJumpcount() {
        return jumpcount;
    }

    public void setJumpcount(int jumpcount) {
        this.jumpcount = jumpcount;
    }

    @Override
    public void setStanding(boolean standing) {
        super.setStanding(standing);
    }

    public void setStanding() {
        for (Platform p : panel.getPlatforms()) {
            if (p.isStandingThis()) {
                setStanding(true);
                return;
            }
        }
        setStanding(false);
    }

    public void checkJumpStatus() {
        if (!jumping && !standing) {
            y += 10;
            jumpcount = 0;
        }
        if (standing) {
            setCanJump(true);
        }
        if (jumping && jumpcount < 20) {
            moveY();
            jumpcount++;
        }
        if (jumpcount == 10) {
            jumpcount = 0;
            jumping = false;
        }


    }

    public boolean isControlled() {
        return controlled;
    }

    public void setControlled(boolean controlled) {
        this.controlled = controlled;
    }

    public synchronized void marioDie() {
        if (hp > 0) {
            hp--;
            x -= 50;
            y = 295;

        }
        else panel.marioDead();
    }
}
