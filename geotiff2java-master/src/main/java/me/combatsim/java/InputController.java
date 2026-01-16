package me.combatsim.java;

import javax.swing.*;
import java.awt.event.*;

import me.combatsim.java.overlay.BitmapOverlay;
import me.combatsim.java.overlay.LOSOverlay;
import me.combatsim.java.overlay.OverlayManager;

public class InputController {

    private final CombatSimulator app;
    private final OverlayManager overlays;

    private Unit draggedUnit = null;
    private boolean dragging = false;

    public InputController(CombatSimulator app, OverlayManager overlays) {
        this.app = app;
        this.overlays = overlays;
        install();
    }

    private void install() {

        /* =========================
           DRAGGING
           ========================= */

        app.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                if (!dragging || draggedUnit == null) return;

                try {
                    // 1) move visually (pixel space)
                    draggedUnit.setPixelPosition(e.getX(), e.getY());

                    // 2) sync combat coordinates (UTM)
                    draggedUnit.syncUtmFromPixel(
                        app.getWgsToUtm(),
                        app.getDem()
                    );

                    app.repaint();

                } catch (Exception ex) {
                    // safe ignore
                }
            }
        });

        app.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                // LEFT CLICK → select unit for dragging
                if (SwingUtilities.isLeftMouseButton(e)) {
                    draggedUnit =
                        app.getUnitManager().getUnitAtPixel(e.getX(), e.getY());
                    dragging = (draggedUnit != null);
                }

                // RIGHT CLICK → LOS overlay
                if (SwingUtilities.isRightMouseButton(e)) {
                    LOSOverlay los = overlays.get(LOSOverlay.class);
                    if (los != null) {
                        los.toggle();
                        los.computeLOS(e.getX(), e.getY());
                        app.repaint();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragging = false;
                draggedUnit = null;
            }
        });

        /* =========================
           KEYBOARD
           ========================= */

        InputMap im = app.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = app.getActionMap();

        im.put(KeyStroke.getKeyStroke("ctrl G"), "toggleOps");
        am.put("toggleOps", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                overlays.toggle(BitmapOverlay.class);
                app.repaint();
            }
        });
    }
}
