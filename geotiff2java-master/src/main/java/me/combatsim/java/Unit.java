package me.combatsim.java;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.geotools.geometry.DirectPosition2D;
import org.opengis.referencing.operation.MathTransform;

import me.combatsim.java.map.ElevationModel;
import me.combatsim.java.map.MapUtils;
import me.combatsim.java.weapons.WeaponDefinition;

public class Unit {
	private String name;

	private boolean hasPlannedMove = false;
	private double utmX;
	private double utmY;
	private double utmZ;
	private Double plannedUtmX = null;
	private Double plannedUtmY = null;
	private int pixelX;
	private int pixelY;
	private boolean isVisible;
	private UnitStatus unitStatus = UnitStatus.ALIVE;
	private double sensorRange;
	private UnitType unitType;
	private UnitTeam unitTeam;
	private double combatPower;
	private String mapSymbol;
	private double speed;
	private final BufferedImage image;
	private WeaponDefinition weapon;
	private double unitRadius;

	// name,type,team,x,y,visible,sensorRange,combatPower,speed,weapon,radius,symbol
	public Unit(String name, UnitType unitType, UnitTeam unitTeam, int startPixelX, int startPixelY, boolean isVisible,
			double sensorRange, double combatPower, double speed, WeaponDefinition weapon, double unitRadius,
			String mapSymbol, // ← just "infantry.bmp"

			ElevationModel dem, MathTransform wgsToUtm, MathTransform utmToWgs) throws Exception {

		// Pixel → UTM
		DirectPosition2D utm = MapUtils.pixelToUTM(startPixelX, startPixelY, wgsToUtm);
		this.name = name;
		this.unitType = unitType;
		this.unitTeam = unitTeam;
		this.utmX = utm.x;
		this.utmY = utm.y;
		this.utmZ = MapUtils.getElevationAtPixel(dem, startPixelX, startPixelY);
		this.isVisible = isVisible;
		this.sensorRange = sensorRange;
		this.combatPower = combatPower;
		this.speed = speed;
		this.weapon = weapon;
		this.unitRadius = unitRadius;
		this.mapSymbol = mapSymbol;

		// ---- LOAD IMAGE FROM RESOURCES ----
		this.image = ImageIO.read(Unit.class.getResource("/" + mapSymbol));

		if (this.image == null) {
			throw new IllegalArgumentException("Unit image not found in resources: " + mapSymbol);
		}
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

	public void moveToward(Unit target) {
		if (!isAlive())
			return;
		double meters = speed; // per turn
		double dx = target.getUtmX() - utmX;
		double dy = target.getUtmY() - utmY;
		double len = Math.sqrt(dx * dx + dy * dy);
		if (len > 1.0) {
			utmX += (dx / len) * meters;
			utmY += (dy / len) * meters;
		}
	}

	public void syncUtmFromPixel(MathTransform wgsToUtm, ElevationModel dem) throws Exception {
		DirectPosition2D utm = MapUtils.pixelToUTM(pixelX, pixelY, wgsToUtm);
		this.utmX = utm.x;
		this.utmY = utm.y;
		this.utmZ = MapUtils.getElevationAtPixel(dem, pixelX, pixelY);
	}

	public void moveTowardPlannedTarget() {
		if (!hasPlannedMove)
			return;

		double dx = plannedUtmX - utmX;
		double dy = plannedUtmY - utmY;
		double distance = Math.sqrt(dx * dx + dy * dy);

		if (distance == 0) {
			clearPlannedMove(); // <-- use safe method
			return;
		}

		double step = Math.min(speed, distance); // move by speed or remaining distance
		utmX += dx / distance * step;
		utmY += dy / distance * step;
	}

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

	// ----SETTERS----
	public void setPixelPosition(int x, int y) {
		this.pixelX = x;
		this.pixelY = y;
	}

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
		this.unitType = (UnitType) unitType;
	}

	public void setUnitTeam(Enum unitTeam) {
		this.unitTeam = (UnitTeam) unitTeam;
	}

	public void setWeapon(WeaponDefinition weapon) {
		this.weapon = weapon;
	}

	public void setUnitRadius(double unitRadius) {
		this.unitRadius = unitRadius;
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	public WeaponDefinition getWeapon() {
		return weapon;
	}

	public static boolean hasLOS(Unit a, Unit b, ElevationModel dem, MathTransform utmToWgs) {
		int steps = 50;
		double dx = (b.utmX - a.utmX) / steps;
		double dy = (b.utmY - a.utmY) / steps;

		double startZ = a.utmZ;
		double endZ = b.utmZ;

		for (int i = 1; i < steps; i++) {
			double x = a.utmX + dx * i;
			double y = a.utmY + dy * i;

			double expectedZ = startZ + (endZ - startZ) * (i / (double) steps);

			try {
				DirectPosition2D wgs = new DirectPosition2D();
				utmToWgs.transform(new DirectPosition2D(x, y), wgs);

				Point2D px = MapUtils.wgs84ToPixel(wgs.y, wgs.x);
				double terrainZ = MapUtils.getElevationAtPixel(dem, (int) px.getX(), (int) px.getY());

				if (terrainZ > expectedZ) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}

		return true;
	}

	public String getName() {
		return name;
	}

	public String getmapSymbol() {
		return mapSymbol;
	}

	public UnitTeam getUnitTeam() {
		return unitTeam;
	}

	public Double getSensorRange() {
		return sensorRange;
	}

	public UnitType getUnitType() {
		return unitType;
	}

	public double getunitRadius() {
		return unitRadius;
	}

	public UnitStatus getUnitStatus() {
		return unitStatus;
	}

	public void setUnitStatus(UnitStatus unitStatus) {
		this.unitStatus = unitStatus;
	}

	public boolean isAlive() {
		return unitStatus == UnitStatus.ALIVE;
	}

	public void setUtmPosition(double x, double y) {
		this.utmX = x;
		this.utmY = y;
	}

	public void setPlannedTarget(double x, double y) {
		plannedUtmX = x;
		plannedUtmY = y;
		hasPlannedMove = true;
	}

	public void clearPlannedMove() {
		plannedUtmX = null;
		plannedUtmY = null;
		hasPlannedMove = false; // fixed
	}

	public boolean hasPlannedMove() {
		return hasPlannedMove;
	}

	public Double getPlannedUtmX() {
		return plannedUtmX;
	}

	public Double getPlannedUtmY() {
		return plannedUtmY;
	}

}
