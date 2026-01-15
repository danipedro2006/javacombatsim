package me.combatsim.java.guieditor;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import me.combatsim.java.map.MapContext;

public abstract class MapPanel extends JPanel {

    protected final MapContext ctx;

    protected MapPanel( MapContext ctx) throws Exception {
    	 
   
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(ctx.map, 0, 0, null);
        drawContent((Graphics2D) g);
    }

    protected abstract void drawContent(Graphics2D g);
}
