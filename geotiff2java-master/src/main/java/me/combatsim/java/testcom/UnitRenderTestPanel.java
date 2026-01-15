package me.combatsim.java.testcom;
 

import javax.swing.JPanel;

import me.combatsim.java.UnitManager;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

public class UnitRenderTestPanel extends JPanel {

    private final UnitManager unitManager;

    public UnitRenderTestPanel(UnitManager unitManager) {
        this.unitManager = unitManager;
        setBackground(Color.DARK_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // IMPORTANT: do NOT call detection, battle, overlays here
        unitManager.draw(g2);
    }
}
