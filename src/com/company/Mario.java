package com.company;

import org.w3c.dom.css.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Mario extends Creature {

    private boolean jumping, canJump,controlled;
    private int jumpcount;

    public Mario(GamePanel panel) {
        defaultValues();
        this.panel = panel;
        x = 0;
        y = 295;
        size = 32;
        width = 600;
        height = 600;


    }


    public Mario(int x, int y, Byte hp, GamePanel panel){
        defaultValues();
        this.x= x;
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
        dy = -10;
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


//   public void drawMario(Graphics g){
//       g.drawRect((int)rect.getX(),(int)rect.getY(),size,size);
//        Image img =images.get(index);
//        if (dir)
//            g.drawImage(img,x,y,size,size,null);
//        else g.drawImage(img,x+size/2,y,-size,size,null);
//
//   }

    @Override
    public void moveX() {

        if ((dir && x < width / 2) || (!dir && x > 0)) {
            super.moveX();
        }
        rect = new Rectangle(x, y, width, height);
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
    public void checkJumpStatus(){
        if (!jumping && !standing) {
            y += 10;
            jumpcount = 0;
        }
        if (standing) {
            setCanJump(true);
        }
        if (jumping && jumpcount < 10) {
            moveY();
            jumpcount++;
        }
        if (jumpcount == 10) {
            jumpcount = 0;
            jumping = false;
        }
        if (y == 495) {
            canJump = true;
        }

    }

    public boolean isControlled() {
        return controlled;
    }

    public void setControlled(boolean controlled) {
        this.controlled = controlled;
    }
}
