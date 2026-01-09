package me.callsen.taylor.geotiff2java;

 

import java.awt.Graphics2D;

public interface Overlay {
    /** Draw the overlay on top of the map */
    void draw(Graphics2D g);

	boolean isVisible();
}
