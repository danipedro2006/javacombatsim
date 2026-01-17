package me.combatsim.java;

import javax.swing.*;

import org.geotools.geometry.DirectPosition2D;

import java.awt.event.*;

import me.combatsim.java.map.MapUtils;
import me.combatsim.java.overlay.BitmapOverlay;
import me.combatsim.java.overlay.LOSOverlay;
import me.combatsim.java.overlay.OverlayManager;

public class InputController {
	private Unit draggedUnit = null;
	private boolean dragging = false;
	private int dragStartX, dragStartY;
	private int dragEndX, dragEndY;
    private final CombatSimulator app;
    private final OverlayManager overlays;
    private Unit arrowUnit = null;
    private boolean planningMove = false;
     

    public InputController(CombatSimulator app, OverlayManager overlays) {
        this.app = app;
        this.overlays = overlays;
        install();
    }

    private void install() {
 
    	    app.addMouseListener(new MouseAdapter() {
    	        @Override
    	        public void mousePressed(MouseEvent e) {
    	            // CTRL + LEFT CLICK → start planned move
    	            if (SwingUtilities.isLeftMouseButton(e) && e.isControlDown()) {
    	                arrowUnit = app.getUnitManager().getUnitAtPixel(e.getX(), e.getY());
    	                planningMove = arrowUnit != null;
    	            }
    	        }

    	        @Override
    	        public void mouseReleased(MouseEvent e) {
    	            planningMove = false;
    	            arrowUnit = null;
    	        }
    	    });

    	    app.addMouseMotionListener(new MouseMotionAdapter() {
    	        @Override
    	        public void mouseDragged(MouseEvent e) {
    	            // 1) Planned move mode
    	            if (planningMove && arrowUnit != null) {
    	                try {
    	                    DirectPosition2D utm = MapUtils.pixelToUTM(e.getX(), e.getY(), app.getWgsToUtm());
    	                    arrowUnit.setPlannedTarget(utm.x, utm.y);
    	                    app.repaint();
    	                } catch (Exception ex) {
    	                    // safe ignore
    	                }
    	                return; // prevent normal dragging
    	            }

    	            // 2) Normal unit dragging
    	            if (!dragging || draggedUnit == null) return;

    	            try {
    	                draggedUnit.setPixelPosition(e.getX(), e.getY());
    	                draggedUnit.syncUtmFromPixel(app.getWgsToUtm(), app.getDem());
    	                app.repaint();
    	            } catch (Exception ex) { }
    	        }
    	    });

    	    app.addMouseListener(new MouseAdapter() {
    	        @Override
    	        public void mousePressed(MouseEvent e) {
    	            // LEFT CLICK → select unit for dragging
    	            if (SwingUtilities.isLeftMouseButton(e)) {
    	                draggedUnit = app.getUnitManager().getUnitAtPixel(e.getX(), e.getY());
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

    	    // Keyboard
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

