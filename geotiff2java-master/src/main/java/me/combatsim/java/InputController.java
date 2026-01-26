package me.combatsim.java;

import javax.swing.*;
import java.awt.Point;
import java.awt.event.*;
import org.geotools.geometry.DirectPosition2D;
import me.combatsim.java.map.MapUtils;
import me.combatsim.java.overlay.OperationsOverlay;
import me.combatsim.java.overlay.LOSOverlay;
import me.combatsim.java.overlay.OverlayEditorOverlay;
import me.combatsim.java.overlay.OverlayManager;

public class InputController {
	Point first = null;
	private Unit draggedUnit = null;
	private boolean dragging = false;

	private Unit arrowUnit = null;
	private boolean planningMove = false;

	private final CombatSimulator combatSimulator;
	private final OverlayManager overlayManager;

	public InputController(CombatSimulator combatSimulator, OverlayManager overlayManager) {
		this.combatSimulator = combatSimulator;
		this.overlayManager = overlayManager;
		install();
	}

	private OverlayEditorOverlay getEditor() {
		return overlayManager.get(OverlayEditorOverlay.class);
	}

	private void install() {

		// ---------------- MOUSE LISTENER ----------------
		combatSimulator.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				// SHIFT + LEFT → distance measurement
				if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()) {
					LOSOverlay los = overlayManager.get(LOSOverlay.class);
					if (!los.isVisible())
						return;
					if (first == null) {
						first = e.getPoint();
					} else {
						Point second = e.getPoint();
						double d = los.distanceMeters(first.x, first.y, second.x, second.y);
						los.setDistancePoints(first, second, d);
						first = null;
						combatSimulator.repaint();
					}
					return; // IMPORTANT: stop further processing
				}

				OverlayEditorOverlay editor = getEditor();
				if (editor != null && editor.isVisible()) {
					editor.mousePressed(e);
					combatSimulator.repaint();
					return; // editor consumes input
				}

				// CTRL + LEFT → planned move
				if (SwingUtilities.isLeftMouseButton(e) && e.isControlDown()) {
					arrowUnit = combatSimulator.getUnitManager().getUnitAtPixel(e.getX(), e.getY());
					planningMove = (arrowUnit != null);
					return;
				}

				// LEFT → unit drag
				if (SwingUtilities.isLeftMouseButton(e)) {
					draggedUnit = combatSimulator.getUnitManager().getUnitAtPixel(e.getX(), e.getY());
					dragging = (draggedUnit != null);
					return;
				}

				// RIGHT → LOS
				if (SwingUtilities.isRightMouseButton(e)) {
					LOSOverlay los = overlayManager.get(LOSOverlay.class);
					if (!los.isVisible())
						return;

					los.computeLOS(e.getX(), e.getY());
					combatSimulator.repaint();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				OverlayEditorOverlay editor = getEditor();
				if (editor != null && editor.isVisible()) {
					editor.mouseReleased(e);
					combatSimulator.repaint();
					return;
				}

				dragging = false;
				draggedUnit = null;
				planningMove = false;
				arrowUnit = null;
			}
		});

		// ---------------- MOUSE MOTION ----------------
		combatSimulator.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {

				OverlayEditorOverlay editor = getEditor();
				if (editor != null && editor.isVisible()) {
					editor.mouseDragged(e);
					combatSimulator.repaint();
					return;
				}

				// Planned move
				if (planningMove && arrowUnit != null) {
					try {
						DirectPosition2D utm = MapUtils.pixelToUTM(e.getX(), e.getY(), combatSimulator.getWgsToUtm());
						arrowUnit.setPlannedTarget(utm.x, utm.y);
						combatSimulator.repaint();
					} catch (Exception ignored) {
					}
					return;
				}

				// Unit dragging
				if (dragging && draggedUnit != null) {
					try {
						draggedUnit.setPixelPosition(e.getX(), e.getY());
						draggedUnit.syncUtmFromPixel(combatSimulator.getWgsToUtm(), combatSimulator.getDem());
						combatSimulator.repaint();
					} catch (Exception ignored) {
					}
				}
			}
		});

		// ---------------- KEYBOARD ----------------
		InputMap im = combatSimulator.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = combatSimulator.getActionMap();

		im.put(KeyStroke.getKeyStroke("ctrl G"), "toggleOps");
		am.put("toggleOps", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				overlayManager.setVisible(OperationsOverlay.class, true);
				combatSimulator.repaint();
			}
		});
	}
}
