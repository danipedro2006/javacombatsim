package me.combatsim.java;

import javax.swing.*;
import java.awt.event.*;

import org.geotools.geometry.DirectPosition2D;

import me.combatsim.java.map.MapUtils;
import me.combatsim.java.overlay.BitmapOverlay;
import me.combatsim.java.overlay.LOSOverlay;
import me.combatsim.java.overlay.OverlayEditorOverlay;
import me.combatsim.java.overlay.OverlayManager;

public class InputController {

    private Unit draggedUnit = null;
    private boolean dragging = false;

    private Unit arrowUnit = null;
    private boolean planningMove = false;

    private final CombatSimulator app;
    private final OverlayManager overlayManager;

    public InputController(CombatSimulator app, OverlayManager overlayManager) {
        this.app = app;
        this.overlayManager = overlayManager;
        install();
    }

    private OverlayEditorOverlay getEditor() {
        return overlayManager.get(OverlayEditorOverlay.class);
    }

    private void install() {

        // ---------------- MOUSE LISTENER ----------------
        app.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                OverlayEditorOverlay editor = getEditor();
                if (editor != null && editor.isVisible()) {
                    editor.mousePressed(e);
                    app.repaint();
                    return; // editor consumes input
                }

                // CTRL + LEFT → planned move
                if (SwingUtilities.isLeftMouseButton(e) && e.isControlDown()) {
                    arrowUnit = app.getUnitManager().getUnitAtPixel(e.getX(), e.getY());
                    planningMove = (arrowUnit != null);
                    return;
                }

                // LEFT → unit drag
                if (SwingUtilities.isLeftMouseButton(e)) {
                    draggedUnit = app.getUnitManager().getUnitAtPixel(e.getX(), e.getY());
                    dragging = (draggedUnit != null);
                    return;
                }

                // RIGHT → LOS
                if (SwingUtilities.isRightMouseButton(e)) {
                    LOSOverlay los = overlayManager.get(LOSOverlay.class);
                    if (los != null) {
                        los.toggle();
                        los.computeLOS(e.getX(), e.getY());
                        app.repaint();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                OverlayEditorOverlay editor = getEditor();
                if (editor != null && editor.isVisible()) {
                    editor.mouseReleased(e);
                    app.repaint();
                    return;
                }

                dragging = false;
                draggedUnit = null;
                planningMove = false;
                arrowUnit = null;
            }
        });

        // ---------------- MOUSE MOTION ----------------
        app.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {

                OverlayEditorOverlay editor = getEditor();
                if (editor != null && editor.isVisible()) {
                    editor.mouseDragged(e);
                    app.repaint();
                    return;
                }

                // Planned move
                if (planningMove && arrowUnit != null) {
                    try {
                        DirectPosition2D utm =
                                MapUtils.pixelToUTM(e.getX(), e.getY(), app.getWgsToUtm());
                        arrowUnit.setPlannedTarget(utm.x, utm.y);
                        app.repaint();
                    } catch (Exception ignored) {}
                    return;
                }

                // Unit dragging
                if (dragging && draggedUnit != null) {
                    try {
                        draggedUnit.setPixelPosition(e.getX(), e.getY());
                        draggedUnit.syncUtmFromPixel(app.getWgsToUtm(), app.getDem());
                        app.repaint();
                    } catch (Exception ignored) {}
                }
            }
        });

        // ---------------- KEYBOARD ----------------
        InputMap im = app.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = app.getActionMap();

        im.put(KeyStroke.getKeyStroke("ctrl G"), "toggleOps");
        am.put("toggleOps", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                overlayManager.toggle(BitmapOverlay.class);
                app.repaint();
            }
        });
    }
}
