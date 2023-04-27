package com.company;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Goomba extends Creature {
    private int ogX, ogY;
    private Mario mario;
    private Mario notMario;

    public Goomba(GamePanel panel, Mario mario, int x, int y) {
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
        this.mario = mario;
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


    public void chase(Mario closest) {
        if (closest.getX() < x) {
            x += dx / 2;
        } else x -= dx / 2;
    }

    public synchronized void checkCollision() {
       // updateRect();
        Rectangle topOfGoomba = new Rectangle(rect.x, rect.y, rect.width, rect.height / 2);
        boolean collidesWithTop = topOfGoomba.intersects(mario.getRect());

        // Check if the other rectangle is above this rectangle
        boolean otherIsAbove = mario.getRect().y + mario.getRect().height < rect.y + rect.height / 2;
        if (collidesWithTop && otherIsAbove) {
            killGoomba();
        } else if (rect.intersects(mario.getRect())) {

            System.out.println("intersects: " + rect.intersects(mario.getRect()));
            System.out.println("mario dead");
            System.out.println(rect.toString());
            System.out.println("mario at: " + mario.getRect().toString());
            //   mario.marioDie();
        }
    }


    public synchronized void killGoomba() {
        panel.addScore(100);
        if (!alive)
            return;

            alive = false;
            newDeath = true;
            System.out.println("goomba dead");


    }

    @Override
    public void run() {

        while (alive || x > 0) {
            super.run();
            monsterStands();
            fall();
            if (alive) {
                animate(0, 1);
               // checkCollision();
                if (panel.getLevel() == 2) {
                    Mario m = locateMario();
                    if (m != null){
                        chase(m);
                        checkCollision();
                    }
                }
            } else animate(2, 2);
            //   walk(ogX - 40, ogX + 40);

            try {
                sleep(60);
            } catch (Exception e) {

            }
        }
    }

    public void fall() {
        if (!standing) {
            y += 10;
        }
        if (y > Constants.floorY + 20)
            killGoomba();
    }

    public Mario locateMario() {
        Mario closest = mario;
        int minRange = Math.abs(closest.getX() - x);
        for (Mario m : panel.getPlayers()
        ) {
            if (m != null) {
                int range = Math.abs(m.getX() - x);
                if (range < minRange) {
                    minRange = range;
                    closest = m;
                }

            }

        }
        //  System.out.println("min range: " + minRange);
        if (minRange > 300 || minRange < Math.abs(dx / 2))
            return null;
        return closest;

    }

    public void monsterStands() {
        boolean flag = false;
        for (Platform p : panel.getPlatforms()) {
            if (p.getRect().intersects(rect)) {
                standing = true;
                flag = true;
            }
        }
        if (!flag)
            standing = false;
    }


}