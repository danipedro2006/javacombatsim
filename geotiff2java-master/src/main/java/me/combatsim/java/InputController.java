package me.combatsim.java;

 

import javax.swing.*;
import java.awt.event.*;

public class InputController {

    private final CombatSimulator app;
    private final OverlayManager overlays;

    public InputController(CombatSimulator app, OverlayManager overlays) {
        this.app = app;
        this.overlays = overlays;
        install();
    }

    private void install() {

        app.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    LOSOverlay los = overlays.get(LOSOverlay.class);
                    if (los != null) {
                        los.toggle();
                        los.computeLOS(e.getX(), e.getY());
                        app.repaint();
                    }
                }
            }
        });

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
