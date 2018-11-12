package aimgame;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

class Bullet extends AbstractCanvas {
    
    private static final int WIDTH  = 20;
    private static final int HEIGHT = 20;
    private int x;
    private int y;
    private int angle;
    LinkedList bulletList = new LinkedList();
    
    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }
    
     public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    public Rectangle getBounds() { //add 400 to x b/c of its initial position (400,0)
        return new Rectangle(x+400, y, WIDTH, HEIGHT);
    }  
}