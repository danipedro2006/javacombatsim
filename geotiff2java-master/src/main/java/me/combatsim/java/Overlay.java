package me.combatsim.java;

 
 

import java.awt.Graphics2D;

public interface Overlay {
    void draw(Graphics2D g);
    boolean isVisible();
    void setVisible(boolean visible);
}

