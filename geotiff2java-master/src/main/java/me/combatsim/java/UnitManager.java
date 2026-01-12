package me.combatsim.java;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opengis.referencing.operation.MathTransform;

/**
 * Manages all combat units.
 * - Units are stored and simulated in UTM coordinates
 * - Pixel coordinates are derived only for rendering
 */
public class UnitManager {

    final List<Unit> units = new ArrayList<>();
    private final ElevationModel dem; 
    
    // Transform used to convert UTM → WGS84 → pixel
    private final MathTransform utmToWgs;

    public UnitManager(MathTransform utmToWgs, ElevationModel dem) {
        this.utmToWgs = utmToWgs;
        this.dem = dem;
    }

    /* =========================
       UNIT MANAGEMENT
       ========================= */

    public void addUnit(Unit unit) {
        units.add(unit);
    }

    public void removeUnit(Unit unit) {
        units.remove(unit);
    }
    
    public boolean hasLOS(Unit a, Unit b) {
        double dx = b.getUtmX() - a.getUtmX();
        double dy = b.getUtmY() - a.getUtmY();
        double distance = Math.sqrt(dx*dx + dy*dy);
        
        int steps = 100;
        for (int i = 1; i <= steps; i++) {
            double t = i / (double) steps;
            double x = a.getUtmX() + dx * t;
            double y = a.getUtmY() + dy * t;
            double z = a.getUtmZ() + (b.getUtmZ() - a.getUtmZ()) * t;

            double terrainZ = MapUtils.getElevationAtUTM(dem, x, y);
            if (terrainZ > z) return false; // blocked
        }
        return true;
    }


    public void clear() {
        units.clear();
    }

    /** Immutable view of units */
    public List<Unit> getUnits() {
        return Collections.unmodifiableList(units);
    }

    /* =========================
       UPDATE / SIMULATION
       ========================= */

    /**
     * Update pixel positions for all units.
     * Call once per frame or after movement.
     */
    public void updateRenderPositions() {
        for (Unit u : units) {
            try {
                u.updatePixelPosition(utmToWgs);
            } catch (Exception e) {
                // Unit outside map or transform failure
            }
        }
    }

    /* =========================
       RENDERING
       ========================= */

    /**
     * Draw all units on the given Graphics context.
     */
    public void draw(Graphics g) {
        for (Unit u : units) {
            BufferedImage img = u.getImage();

            int x = u.getPixelX() - img.getWidth() / 2;
            int y = u.getPixelY() - img.getHeight() / 2;

            g.drawImage(img, x, y, null);
        }
    }

    /* =========================
       SELECTION / QUERY
       ========================= */

    /**
     * Get the topmost unit at pixel position (for mouse picking).
     */
    public Unit getUnitAtPixel(int x, int y) {
        for (int i = units.size() - 1; i >= 0; i--) {
            Unit u = units.get(i);
            BufferedImage img = u.getImage();

            int px = u.getPixelX() - img.getWidth() / 2;
            int py = u.getPixelY() - img.getHeight() / 2;

            if (x >= px && x <= px + img.getWidth()
             && y >= py && y <= py + img.getHeight()) {
                return u;
            }
        }
        return null;
    }

    /* =========================
       DEBUG / UTIL
       ========================= */

    public void printAllUnits() {
        for (Unit u : units) {
            System.out.printf(
                "Unit UTM: X=%.2f Y=%.2f Z=%.2f%n",
                u.getUtmX(), u.getUtmY(), u.getUtmZ()
            );
        }
    }

	public void add(Unit unit) {
		units.add(unit);
		
	}
}
