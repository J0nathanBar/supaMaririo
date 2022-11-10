package com.company;

import org.w3c.dom.css.Rect;

import java.awt.*;

public class Platform extends Thread {
    private int x,y,dx,width,height;
   private Rectangle rect;
   private static final int floorY = 515,floorH = 100;
   private boolean standingThis;
   private GamePanel panel;
   private Mario mario;


    public Platform(int x, int y, int width, int height,GamePanel panel,Mario mario) {
        this.x = x;
        this.y = y;
        this.dx = -10;
        this.width = width;
        this.height = height;
        this.panel = panel;
        this.standingThis = false;
        rect = new Rectangle(x,y,width,height);
        this.mario = mario;
        if (y == floorY && height== floorH){
            this.width+=300;
        }
    }

    public Platform() {
    }
    public void drawp(Graphics g){
        g.drawRect(x,y,width,height);
        g.setColor(Color.red);
       // if (height!= floorH)
           // g.fillRect(x,y,width,height);

    }

    public int getX() {

        return x;
    }
    public void moveX(){
        x+=dx;
        updateRect();
    }

    public void updateRect(){
        rect = new Rectangle(x,y,width,height);
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

    public void standsOn(){
        if (rect.intersects(mario.getRect())&& rect.getMaxY() >= mario.getRect().getMinY()){
            mario.setStanding(true);
            standingThis = true;
        }
        else if (standingThis && !rect.intersects(mario.getRect())){
            standingThis = false;
            mario.setStanding(false);
        }

    }
    public boolean runsTo(int d){//right: d =1, left: d=-1
     //  Rectangle temp = new Rectangle(x+(d*10),y,width,height);
       if (height!= floorH)
            System.out.println("stands on: " +standingThis);
       if (d==1)
        return rect.intersects(mario.rect)&&Math.abs(rect.getMinX() - mario.getRect().getMaxX()) <=15 && !standingThis;
       else return rect.intersects(mario.rect)&&Math.abs(rect.getMaxX() - mario.getRect().getMinX())<=15&&!standingThis;
    }

    @Override
    public void run() {
        super.run();
        while (true){
            updateRect();
            standsOn();
            panel.repaint();


        }
    }
}
