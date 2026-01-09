package me.combatsim.java;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.geotools.geometry.DirectPosition2D;
import org.opengis.referencing.operation.MathTransform;

public class Unit {
	private String name;

	// ---- TRUE POSITION (UTM) ----
	private double utmX;
	private double utmY;
	private double utmZ;

	// ---- CACHED RENDER POSITION ----
	private int pixelX;
	private int pixelY;
	private boolean isVisible;
	// private Status status;
	private double sensorRange;
	private Enum unitType;
	private Enum unitTeam;
	private double combatPower;
	private double speed;
    private List<Point> path = new ArrayList<>();
	private List<CombatSystem> armament;
	private final BufferedImage image;
	private CombatSystem weapon;
	private double unitRadius;
	
	public Unit(int startPixelX, int startPixelY, String mapSymbol, // ← just "infantry.bmp"
			ElevationModel dem, MathTransform wgsToUtm, MathTransform utmToWgs) throws Exception {

		// Pixel → UTM
		DirectPosition2D utm = MapUtils.pixelToUTM(startPixelX, startPixelY, wgsToUtm);

		this.utmX = utm.x;
		this.utmY = utm.y;
		this.utmZ = MapUtils.getElevationAtPixel(dem, startPixelX, startPixelY);

		// ---- LOAD IMAGE FROM RESOURCES ----
		this.image = ImageIO.read(Unit.class.getResource("/" + mapSymbol));

		if (this.image == null) {
			throw new IllegalArgumentException("Unit image not found in resources: " + mapSymbol);
		}

		// Initial render position
		updatePixelPosition(utmToWgs);
	}

	/** Recalculate pixel coordinates from UTM */
	public void updatePixelPosition(MathTransform utmToWgs) throws Exception {
		DirectPosition2D wgs = new DirectPosition2D();
		utmToWgs.transform(new DirectPosition2D(utmX, utmY), wgs);

		Point2D px = MapUtils.wgs84ToPixel(wgs.y, wgs.x);
		this.pixelX = (int) px.getX();
		this.pixelY = (int) px.getY();
	}

	// ---- Movement in UTM space ----
	public void move(double dxMeters, double dyMeters) {
		utmX += dxMeters;
		utmY += dyMeters;
	}

	public double distance3dTo(Unit other) {
		double dx = other.utmX - this.utmX;
		double dy = other.utmY - this.utmY;
		double dz = other.utmZ - this.utmZ; // optional for 3D distance
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public double distance2dTo(Unit other) {
		double dx = other.utmX - this.utmX;
		double dy = other.utmY - this.utmY;
		return Math.sqrt(dx * dx + dy * dy);
	}

	// public boolean hasLOS(Unit a, Unit b) { ... }
	// public boolean canAtack(Unit a, Unit b) { ... }
	//public boolean detectTarget(Unit targetUnit) { ... }
	
	// ---- Getters ----
	public int getPixelX() {
		return pixelX;
	}

	public int getPixelY() {
		return pixelY;
	}

	public double getUtmX() {
		return utmX;
	}

	public double getUtmY() {
		return utmY;
	}

	public double getUtmZ() {
		return utmZ;
	}

	public BufferedImage getImage() {
		return image;
	}
	
	public List<Point> getPath() {
        return path;
    }
	
	//----SETTERS----
	public void setName(String name) {
	    this.name = name;
	}

	public void setSensorRange(double sensorRange) {
	    this.sensorRange = sensorRange;
	}

	public void setCombatPower(double combatPower) {
	    this.combatPower = combatPower;
	}

	public void setSpeed(double speed) {
	    this.speed = speed;
	}

	public void setUnitType(Enum unitType) {
	    this.unitType = unitType;
	}

	public void setUnitTeam(Enum unitTeam) {
	    this.unitTeam = unitTeam;
	}

	public void setWeapon(CombatSystem weapon) {
	    this.weapon = weapon;
	}

	public void setUnitRadius(double unitRadius) {
	    this.unitRadius = unitRadius;
	}

	public void setVisible(boolean visible) {
	    isVisible = visible;
	}

	 
	
//----Define Point class to store unitPath----
	
	public static class Point {
		public final int x;
		public final int y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

	}
}
