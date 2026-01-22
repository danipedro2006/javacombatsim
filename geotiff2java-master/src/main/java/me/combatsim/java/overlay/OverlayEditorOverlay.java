package me.combatsim.java.overlay;

import java.awt.*;
import java.awt.event.MouseEvent;

public class OverlayEditorOverlay implements Overlay {

    private final OverlayEditorCore core;
    private boolean visible = false;

    public OverlayEditorOverlay(
            Object dem
    ) throws Exception {
        this.core = new OverlayEditorCore("C:/Users/danie/Downloads/geotiff2java-master/geotiff2java-master/src/main/resources/operations.bmp");
    }

    // ---- delegation ----
    public void setTool(Tool tool) {
        core.setTool(tool);
    }

    public void save() throws Exception {
        core.saveCanvasToFile();
    }

    // ---- overlay ----
    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;

        Composite old = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 0.6f));
        core.paint(g);
        g.setComposite(old);
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean v) {
        visible = v;
    }

    // ---- input ----
    public void mousePressed(MouseEvent e) {
        if (visible) core.mousePressed(e.getPoint());
    }

    public void mouseDragged(MouseEvent e) {
        if (visible) core.mouseDragged(e.getPoint());
    }

    public void mouseReleased(MouseEvent e) {
        if (visible) core.mouseReleased(e.getPoint());
    }
}
