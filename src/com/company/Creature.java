package com.company;


import org.w3c.dom.css.Rect;

import java.awt.*;
import java.util.ArrayList;

public abstract class Creature extends Thread {
    protected int x, y, dx, dy, size, index, width, height;
    protected boolean alive,newDeath;
    Byte hp;
    protected final double gravity = 0.6;
    protected boolean standing;
    protected Rectangle rect;
    protected GamePanel panel;


    public Rectangle getRect() {
        return rect;
    }

    public boolean isStanding() {
        return standing;
    }

    public void setStanding(boolean standing) {
        this.standing = standing;
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


    public Byte getHp() {
        return hp;
    }

    public void setHp(Byte hp) {
        this.hp = hp;
    }

    protected boolean dir;//true = right, false = left
    protected ArrayList<Image> images;


    public abstract boolean interacts(Creature c);

    public abstract boolean touches(Platform p);

    protected abstract void initArr();

    protected abstract void defaultValues();

    protected abstract void animate(int start, int end);

    public int getX() {
        return x;
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

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public boolean isDir() {
        return dir;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public void moveX() {
        int mod = 1;
        if (!dir)
            mod = -1;

        x += (dx * mod);
    }

    public void moveY() {
        synchronized (this){
        y += dy;
        rect = new Rectangle(x, y, width, height);}
    }

    public void setDir(boolean dir) {
        this.dir = dir;
    }

    public void updateRect() {
        if (rect != null) {
            if (dir)
                rect.setBounds(x, y, size, size);
            else
                rect.setBounds(x - size / 2, y, size, size);
        } else createRect();
    }

    public void createRect() {
        if (dir)
            rect = new Rectangle(x, y, size, size);
        else rect = new Rectangle(x - size / 2, y, size, size);
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
        updateRect();

        panel.repaint();

    }

    public void drawCreature(Graphics g) {
        g.drawRect((int) rect.getX(), (int) rect.getY(), size, size);
        Image img = images.get(index);
        if (dir)
            g.drawImage(img, x, y, size, size, null);
        else g.drawImage(img, x + size / 2, y, -size, size, null);

    }

    public boolean isGAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isNewDeath() {
        return newDeath;
    }

    public void setNewDeath(boolean newDeath) {
        this.newDeath = newDeath;
    }
}
