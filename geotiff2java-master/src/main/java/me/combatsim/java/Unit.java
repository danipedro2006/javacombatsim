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
	// ---- TRUE POSITION (UTM) ----
	private double utmX;
	private double utmY;
	private double utmZ;
	private Double plannedUtmX = null;
	private Double plannedUtmY = null;
	// ---- CACHED RENDER POSITION ----
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
	private List<Point> path = new ArrayList<>();
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

	public List<Point> getPath() {
		return path;
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

//----Define Point class to store unitPath----

	public static class Point {
		public final int x;
		public final int y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

	}

	/*
	 * public boolean canDetect(Unit e, ElevationModel dem, MathTransform utmToWgs)
	 * { if (!(this.getUnitStatus() == UnitStatus.ALIVE)) return false;
	 * 
	 * 
	 * double distance = this.distance2dTo(e); // 2D distance is enough //return
	 * distance <= this.sensorRange; return (distance <= this.sensorRange &&
	 * hasLOS(this, e, dem, utmToWgs));//changed }
	 */
	/*
	 * public boolean canDetect(Unit e, ElevationModel dem, MathTransform utmToWgs)
	 * { // 1. Verificări de bază (Hard stops) if (this.getUnitStatus() !=
	 * UnitStatus.ALIVE) return false;
	 * 
	 * double distance = this.distance2dTo(e); if (distance > this.sensorRange)
	 * return false;
	 * 
	 * // 2. Line of Sight (Cea mai costisitoare operație, o facem după distanță) if
	 * (!hasLOS(this, e, dem, utmToWgs)) { // Dacă pierdem LOS, scădem rapid scorul
	 * de detecție (opțional) e.setDetectionScore(this, Math.max(0,
	 * e.getDetectionScore(this) - 10)); return false; }
	 * 
	 * // 3. Power Law Formula // k = 2.0 pentru condiții normale, k = 4.0 pentru
	 * condiții grele (ceață/tufișuri) double k = 2.0; double pBase = Math.pow(1.0 -
	 * (distance / this.sensorRange), k);
	 * 
	 * // 4. Modificatori de context (Hacks) //if (e.isMoving()) pBase *= 2.0; //
	 * Mișcarea dublează șansa de a fi observat //if (e.isFiring()) pBase *= 5.0; //
	 * Tragerea cu arma face detecția aproape instantanee
	 * 
	 * // 5. Simulare eveniment random (The "Roll") if (Math.random() < pBase) { //
	 * Nu "vedem" instant, ci creștem un buffer double currentScore =
	 * e.getDetectionScore(this); e.setDetectionScore(this, Math.min(100,
	 * currentScore + 20)); }
	 * 
	 * // 6. Rezultatul depinde de pragul de acumulare // Unitatea este considerată
	 * detectată doar dacă scorul a trecut de 50 return e.getDetectionScore(this) >=
	 * 50; }
	 */
	private void setDetectionScore(Unit unit, double d) {
		// TODO Auto-generated method stub
		
	}

	private int getDetectionScore(Unit unit) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * public boolean canDetect(Unit target, ElevationModel dem, MathTransform
	 * utmToWgs) {
	 * 
	 * // Cannot detect self if (target == this) return false;
	 * 
	 * // Must have sensor range if (sensorRange <= 0) return false;
	 * 
	 * // Distance check (2D) double distance = distance2dTo(target); if (distance >
	 * sensorRange) return false;
	 * 
	 * // Line of sight check return hasLOS(this, target, dem, utmToWgs); }
	 */

	public static boolean hasLOS(Unit a, Unit b, ElevationModel dem, MathTransform utmToWgs) {
// Simple step-based LOS (can be optimized later)
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
		// TODO Auto-generated method stub
		return name;
	}

	public String getmapSymbol() {
		return mapSymbol;
	}

	public UnitTeam getUnitTeam() {
		// TODO Auto-generated method stub
		return unitTeam;
	}

	public Double getSensorRange() {
		// TODO Auto-generated method stub
		return sensorRange;
	}

	public UnitType getUnitType() {
		// TODO Auto-generated method stub
		return unitType;
	}

	public double getunitRadius() {
		// TODO Auto-generated method stub
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
