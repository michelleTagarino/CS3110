package aimgame;

import java. awt.*;
import java.awt.event.*;

class Targets extends AbstractCanvas{
    
    private int x;
    private int y;
    private int width  = 30;
    private int height = 30;  
    
    public Targets(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(this.x, this.y, width, height);
    }
    
    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }  
}