package com.company;

import org.w3c.dom.css.Rect;

import java.awt.*;

public class Platform extends Thread {
    private int x, y, dx, width, height;
    private Rectangle rect;
    private boolean standingThis;
    private GamePanel panel;
    private Mario mario;


    public Platform(int x, int y, int width, int height, GamePanel panel, Mario mario) {
        this.x = x;
        this.y = y;
        this.dx = -10;
        this.width = width;
        this.height = height;
        this.panel = panel;
        this.standingThis = false;
        rect = new Rectangle(x, y, width, height);
        this.mario = mario;

    }

    public Platform() {
    }

    public void drawp(Graphics g) {
        g.drawRect(x, y, width, height);
        g.setColor(Color.red);
        // if (height!= floorH)
        // g.fillRect(x,y,width,height);

    }

    public int getX() {

        return x;
    }

    public void moveX() {
        synchronized (this) {
            x += dx;
            updateRect();
        }
    }

    public void updateRect() {
        rect = new Rectangle(x, y, width, height);
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Rectangle getRect() {
        return rect;
    }

    public void setRect(Rectangle rect) {
        this.rect = rect;
    }

    public GamePanel getPanel() {
        return panel;
    }

    public void setPanel(GamePanel panel) {
        this.panel = panel;
    }

    public Mario getMario() {
        return mario;
    }

    public void setMario(Mario mario) {
        this.mario = mario;
    }

    public void standsOn() {

        double distance = rect.getMinY() - mario.getRect().getMaxY();
        if (rect.intersects(mario.getRect()) && distance > -11 && distance <= 0) {
            // System.out.println(distance);

            standingThis = true;

        } else if (standingThis && !rect.intersects(mario.getRect())) {
            standingThis = false;
        }

    }



    public boolean runsTo(int d) {//right: d =1, left: d=-1
        if (standingThis)
            return false;
        Rectangle marioHitbox = mario.getRect();
        Rectangle platformHitbox = rect;

        if (d == 1) {
            // Check for collision on the right side of Mario
            return marioHitbox.getMaxX() >= platformHitbox.getMinX() &&
                    marioHitbox.getMinX() < platformHitbox.getMinX() &&
                    marioHitbox.getMinY() < platformHitbox.getMaxY() &&
                    marioHitbox.getMaxY() > platformHitbox.getMinY();
        } else {
            // Check for collision on the left side of Mario
            return marioHitbox.getMinX() <= platformHitbox.getMaxX() &&
                    marioHitbox.getMaxX() > platformHitbox.getMaxX() &&
                    marioHitbox.getMinY() < platformHitbox.getMaxY() &&
                    marioHitbox.getMaxY() > platformHitbox.getMinY();
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            if (panel.isPause()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        super.run();

        while (true) {
            updateRect();
            standsOn();

            if (bumpsIntoPlatform()) {
                mario.setJumping(false);
            }
            panel.repaint();
        }
    }

    public boolean isStandingThis() {
        return standingThis;
    }

    public void setStandingThis(boolean standingThis) {
        this.standingThis = standingThis;
    }


    public boolean bumpsIntoPlatform() {
        int rectBottom = rect.y + rect.height;
        int marioTop = mario.getRect().y;
        int overlapY = rectBottom - marioTop;
        boolean isOnTop = overlapY >= 0 && overlapY < rect.height;
        if (!isOnTop) {
            return false; // rect is not on top of mario
        }
        int overlapX = Math.min(rect.x + rect.width, mario.getRect().x + mario.getRect().width) - Math.max(rect.x, mario.getRect().x);
        boolean isOnSameColumn = overlapX >= 0 && overlapX < rect.width;
        if (!isOnSameColumn) {
            return false; //checking the x values
        }
        boolean intersects = rect.intersects(mario.getRect());
        boolean isVeryClose = Math.abs(rectBottom - marioTop) < 5; // adjust the threshold as needed
        return intersects || isVeryClose;
    }
}
