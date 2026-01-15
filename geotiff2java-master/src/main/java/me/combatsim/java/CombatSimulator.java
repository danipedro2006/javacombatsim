package me.combatsim.java;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.Timer;

import me.combatsim.java.map.MapContext;
import me.combatsim.java.map.MapUtils;
import me.combatsim.java.overlay.BitmapOverlay;
import me.combatsim.java.overlay.LOSOverlay;
import me.combatsim.java.overlay.OverlayBootstrap;
import me.combatsim.java.overlay.OverlayManager;

public class CombatSimulator extends JPanel {
	boolean toggleSensorOverlay = false;
	private final MapContext ctx;
	private final UnitManager unitManager;
	private final UnitFactory unitFactory;
	private final UnitBootstrap unitBootstrap;
	private final OverlayManager overlayManager;
	private final DetectionManager detectionManager;
	private final BattleManager battleManager;

	private int mouseX = -1, mouseY = -1;
	private final Timer battleTimer;

	public CombatSimulator() throws Exception {

		// ---- Map / CRS / DEM ----
		ctx = new MapContext();

		// ---- Core simulation objects ----
		unitManager = new UnitManager(ctx.utmToWgs, ctx.dem);
		unitFactory = new UnitFactory(ctx.dem, ctx.wgsToUtm, ctx.utmToWgs);
		unitBootstrap = new UnitBootstrap(unitFactory, unitManager);

		// ---- Overlays ----
		overlayManager = OverlayBootstrap.create(ctx, unitManager);
		detectionManager = new DetectionManager(ctx.dem, ctx.utmToWgs);
		battleManager = new BattleManager(unitManager, detectionManager);

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

		// ---- Battle timer: 1 turn per second ----
		battleTimer = new Timer(1000, e -> {
			battleManager.runTurn(); // resolve combat first
			detectionManager.update(unitManager.getFriendlyUnits(), unitManager.getEnemyUnits());
			repaint();
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(ctx.map, 0, 0, null);

		// --- Units ---
		Graphics2D gUnits = (Graphics2D) g.create();
		unitManager.updateRenderPositions();
		unitManager.draw(gUnits);
		gUnits.dispose();

		// --- Overlays ---
		Graphics2D gOverlays = (Graphics2D) g.create();
		 
		if (toggleSensorOverlay) {
		    overlayManager.drawOverlays(gOverlays);
		}
		gOverlays.dispose();

			// --- Detection ---
			Graphics2D gDetect = (Graphics2D) g.create();
			//detectionManager.update(unitManager.getFriendlyUnits(), unitManager.getEnemyUnits());
			detectionManager.draw(gDetect);
			gDetect.dispose();

			// Mouse UTM display
			if (mouseX >= 0 && mouseY >= 0) {
				try {
					var utm = MapUtils.pixelToUTM(mouseX, mouseY, ctx.wgsToUtm);
					double z = MapUtils.getElevationAtPixel(ctx.dem, mouseX, mouseY);

					g.setColor(new Color(0, 0, 0, 170));
					g.fillRect(5, 5, 420, 25);

					g.setColor(Color.WHITE);
					g.drawString(String.format("UTM X %.1f  Y %.1f  Z %.1f m", utm.x, utm.y, z), 10, 22);
				} catch (Exception ignored) {
				}
			}
		}


	@Override
	public Dimension getPreferredSize() {
		return new Dimension(ctx.map.getWidth(), ctx.map.getHeight());
	}

	// ---- Expose for menus / UI ----
	public UnitBootstrap getUnitBootstrap() {
		return unitBootstrap;
	}

	public UnitManager getUnitManager() {
		return unitManager;
	}

	public DetectionManager getDetectionManager() {
		return detectionManager;
	}

	public BattleManager getBattleManager() {
		return battleManager;
	}

	public void startBattle() {
		battleTimer.start();
	}

	public void stopBattle() {
		battleTimer.stop();
	}

	public void toggleLOS() {
		overlayManager.toggle(LOSOverlay.class);
		repaint();
	}

	public void toggleSensor() {
	    toggleSensorOverlay = !toggleSensorOverlay;
	    repaint();
	}


	public void toggleOperations() {
		overlayManager.toggle(BitmapOverlay.class);
		repaint();
	}
}
