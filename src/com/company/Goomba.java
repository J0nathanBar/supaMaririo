package com.company;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Goomba extends Creature {
    private int ogX, ogY;
    boolean alive;

    public Goomba(GamePanel panel, int x, int y) {
        this.x = x;
        this.y = y;
        this.size = 24;
        this.panel = panel;
        dir = true;
        dx = -10;
        ogX = x;
        ogY = y;
        updateRect();
        initArr();
        alive = true;


    }


    @Override
    public boolean interacts(Creature c) {
        return false;
    }

    @Override
    public boolean touches(Platform p) {
        return false;
    }

    @Override
    protected void initArr() {
        index = 0;
        images = new ArrayList<>();
        images.add(new ImageIcon("goomba1.png").getImage());
        images.add(new ImageIcon("goomba2.png").getImage());
        images.add(new ImageIcon("goomba3.png").getImage());


    }

    @Override
    protected void defaultValues() {

    }

    @Override
    protected void animate(int start, int end) {
        index++;
        if (index > end)
            index = start;
    }

    public void walk(int minX, int maxX) {
        if (this.x <= minX) {
            this.dx = Math.abs(this.dx);
            dir = !dir;
        } else if (this.x >= maxX) {
            this.dx = -Math.abs(this.dx);
            dir = !dir;
        }
        this.x += this.dx;
        //
        // System.out.println("goomba X: " + this.x);
    }

    public void checkCollision(Mario mario) {
        if (mario.getRect().intersects(this.rect)) {
            if (mario.getRect().y + mario.getRect().height < this.rect.y + this.rect.height/2) {
                // Mario has jumped on the Goomba
                System.out.println("goombdead");
            } else {
                killGoomba();
            }
        }
    }





    void killGoomba() {
        alive = false;
    }

    @Override
    public void run() {
        while (alive || x >0) {
            super.run();
            fall();
          //  System.out.println("here");
            if (alive)
                animate(0, 1);
            else animate(2,2);
         //   walk(ogX - 40, ogX + 40);
           checkCollision(panel.getMario());
            try {
                sleep(60);
            } catch (Exception e) {

            }
        }
    }
public void fall(){
    if (!standing) {
        y += 10;
    }
}

}