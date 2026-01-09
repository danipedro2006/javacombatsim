package me.callsen.taylor.geotiff2java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.geotools.geometry.DirectPosition2D;

public class App extends JPanel {

	private BufferedImage map;
	private ElevationModel dem;

	private MathTransform wgsToUtm;
	private MathTransform utmToWgs;

	// ✅ App owns UnitManager
	private UnitManager unitManager;

	private int mouseX = -1;
	private int mouseY = -1;

	private final List<Overlay> overlays = new ArrayList<>();
	private LOSOverlay losOverlay;
	private OverlayManager overlayManager;

	// Add overlays in constructor

	public App() throws Exception {

		map = ImageIO.read(getClass().getResource("/me/callsen/taylor/geotiff2java/Map-army-export.bmp"));

		dem = new ElevationModel(getClass().getResource("/me/callsen/taylor/geotiff2java/map.tif"));

		// ---- CRS ----
		CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326", true);
		CoordinateReferenceSystem utm = CRS.decode("EPSG:32634", true);

		wgsToUtm = CRS.findMathTransform(wgs84, utm, true);
		utmToWgs = CRS.findMathTransform(utm, wgs84, true);

		// ---- Calibration ----
		MapUtils.initCalibration(203, 139, 46.32, 21.78, 1528, 779, 46.22, 22.08);

		// ✅ Create UnitManager here
		unitManager = new UnitManager(utmToWgs, dem);

		// ---- Create units ----
		Unit infantry = new Unit(800, 400, "infantry.bmp", dem, wgsToUtm, utmToWgs);
		Unit tank = new Unit(900, 700, "tank.bmp", dem, wgsToUtm, utmToWgs);

		unitManager.addUnit(infantry);
		unitManager.addUnit(tank);
		overlays.add(new UnitOverlay(unitManager));

		overlayManager = new OverlayManager();

		// Initialize LOS overlay
		BitmapOverlay operationsOverlay = new BitmapOverlay("/operations.bmp");
		overlayManager.addOverlay(operationsOverlay);
		LOSOverlay losOverlay = new LOSOverlay(dem, wgsToUtm, map.getWidth(), map.getHeight());
		overlayManager.addOverlay(losOverlay);

		// Optionally add a units overlay

		UnitOverlay unitsOverlay = new UnitOverlay(unitManager); // create a simple overlay to draw all units
		overlayManager.addOverlay(unitsOverlay);

		// Mouse right-click toggles LOS
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					losOverlay.toggle();
					losOverlay.computeLOS(e.getX(), e.getY());
					repaint();
				}
			}
		});
		double dist = infantry.distance2dTo(tank);
		System.out.println("Distance infantry → tank: " + dist + " meters");

		// ---- TEST LOS ----
		boolean los = unitManager.hasLOS(infantry, tank);
		System.out.println("Line of sight infantry → tank: " + los);

		// --- Mouse motion listener for coordinates ---
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				repaint();
			}
		});

		// --- Mouse listener for right-click to toggle LOS overlay ---
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					losOverlay.toggle(); // toggle visibility
					losOverlay.computeLOS(e.getX(), e.getY()); // compute LOS from clicked pixel
					repaint();
				}
			}
		});
		addKeyListener(new KeyAdapter() {
		    @Override
		    public void keyPressed(KeyEvent e) {
		        if (e.getKeyCode() == KeyEvent.VK_G) {
		        	operationsOverlay.setVisible(!operationsOverlay.isVisible());
		            repaint();
		        }
		    }
		});


	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Base map
		g.drawImage(map, 0, 0, null);

		// Update + draw units
		unitManager.updateRenderPositions();
		unitManager.draw(g);
		// losOverlay.draw((Graphics2D) g);
		overlayManager.drawOverlays((Graphics2D) g);

		// Mouse UTM display
		if (mouseX >= 0 && mouseY >= 0) {
			try {
				DirectPosition2D utm = MapUtils.pixelToUTM(mouseX, mouseY, wgsToUtm);
				double z = MapUtils.getElevationAtPixel(dem, mouseX, mouseY);

				g.setColor(new Color(0, 0, 0, 170));
				g.fillRect(5, 5, 420, 25);

				g.setColor(Color.WHITE);
				g.drawString(String.format("UTM X: %.1f  Y: %.1f  Z: %.1f m", utm.x, utm.y, z), 10, 22);
			} catch (Exception ignored) {
			}
		}

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(map.getWidth(), map.getHeight());
	}

	public static void main(String[] args) throws Exception {

		JFrame f = new JFrame("Combat Simulator");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(new App());
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
}
