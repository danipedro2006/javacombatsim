package me.combatsim.java;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

public class CombatSimulator extends JPanel {

    private final MapContext ctx;
    private final UnitManager unitManager;
    private final UnitFactory unitFactory;
    private final UnitBootstrap unitBootstrap;
    private final OverlayManager overlayManager;

    private int mouseX = -1, mouseY = -1;

    public CombatSimulator() throws Exception {

        // ---- Map / CRS / DEM ----
        ctx = new MapContext();

        // ---- Core simulation objects ----
        unitManager = new UnitManager(ctx.utmToWgs, ctx.dem);
        unitFactory = new UnitFactory(ctx.dem, ctx.wgsToUtm, ctx.utmToWgs);
        unitBootstrap = new UnitBootstrap(unitFactory, unitManager);

        // ---- Overlays ----
        overlayManager = OverlayBootstrap.create(ctx, unitManager);

        // ---- Input ----
        new InputController(this, overlayManager);

        // ---- Mouse tracking ----
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Base map
        g.drawImage(ctx.map, 0, 0, null);

        // Units
        unitManager.updateRenderPositions();
        unitManager.draw(g);

        // Overlays
        overlayManager.drawOverlays((Graphics2D) g);

        // Mouse UTM display
        if (mouseX >= 0 && mouseY >= 0) {
            try {
                var utm = MapUtils.pixelToUTM(mouseX, mouseY, ctx.wgsToUtm);
                double z = MapUtils.getElevationAtPixel(ctx.dem, mouseX, mouseY);

                g.setColor(new Color(0, 0, 0, 170));
                g.fillRect(5, 5, 420, 25);

                g.setColor(Color.WHITE);
                g.drawString(
                        String.format("UTM X %.1f  Y %.1f  Z %.1f m",
                                utm.x, utm.y, z),
                        10, 22
                );
            } catch (Exception ignored) {}
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ctx.map.getWidth(), ctx.map.getHeight());
    }

    // Expose for menus / UI
    public UnitBootstrap getUnitBootstrap() {
        return unitBootstrap;
    }

    public UnitManager getUnitManager() {
        return unitManager;
    }

    public void toggleLOS() {
        overlayManager.toggle(LOSOverlay.class);
        repaint();
    }

    public void toggleOperations() {
        overlayManager.toggle(BitmapOverlay.class);
        repaint();
    }

}
