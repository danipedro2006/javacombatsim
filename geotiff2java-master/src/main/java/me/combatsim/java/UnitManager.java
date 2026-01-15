package me.combatsim.java;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opengis.referencing.operation.MathTransform;

import me.combatsim.java.map.ElevationModel;
import me.combatsim.java.map.MapUtils;

/**
 * Manages all combat units.
 */
public class UnitManager {

    private final List<Unit> units = new ArrayList<>();
    private final ElevationModel dem;
    private final MathTransform utmToWgs;

    public UnitManager(MathTransform utmToWgs, ElevationModel dem) {
        this.utmToWgs = utmToWgs;
        this.dem = dem;
    }

    /* =========================
       UNIT MANAGEMENT
       ========================= */

    public void addUnit(Unit u) {
        units.add(u);
    }

    public void removeUnit(Unit u) {
        units.remove(u);
    }

    public List<Unit> getUnits() {
        return Collections.unmodifiableList(units);
    }

    public List<Unit> getFriendlyUnits() {
        List<Unit> result = new ArrayList<>();
        for (Unit u : units) {
            if (u.getUnitTeam() == UnitTeam.FRIENDLY) result.add(u);
        }
        return result;
    }

    public List<Unit> getEnemyUnits() {
        List<Unit> result = new ArrayList<>();
        for (Unit u : units) {
            if (u.getUnitTeam() == UnitTeam.ENEMY) result.add(u);
        }
        return result;
    }

    /* =========================
       UPDATE / SIMULATION
       ========================= */

    /** Update pixel positions for all units, alive or destroyed */
    public void updateRenderPositions() {
        for (Unit u : units) {
            try {
                u.updatePixelPosition(utmToWgs);
            } catch (Exception e) {
                // ignore
            }
        }
    }


    /* =========================
       RENDERING
       ========================= */

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        for (Unit u : units) {
            BufferedImage img = u.getImage();
            int x = u.getPixelX() - img.getWidth() / 2;
            int y = u.getPixelY() - img.getHeight() / 2;

            if (u.getUnitStatus() != UnitStatus.ALIVE) {
                // Fade image
                g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 0.3f));
            } else {
                g2.setComposite(AlphaComposite.SrcOver);
            }
            System.out.println("[DRAW] " + u.getName() + " status=" + u.getUnitStatus());

            g2.drawImage(img, x, y, null);

            // 🔴 IMPORTANT: reset composite BEFORE drawing cross
            if (u.getUnitStatus() != UnitStatus.ALIVE) {
                g2.setComposite(AlphaComposite.SrcOver); // <-- THIS LINE
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(2f));

                g2.drawLine(x, y, x + img.getWidth(), y + img.getHeight());
                g2.drawLine(x, y + img.getHeight(), x + img.getWidth(), y);
            }
        }

        g2.setComposite(AlphaComposite.SrcOver);
    }


    private void drawUnit(Graphics2D g2, Unit u, float alpha) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        BufferedImage img = u.getImage();
        int x = u.getPixelX() - img.getWidth() / 2;
        int y = u.getPixelY() - img.getHeight() / 2;
        g2.drawImage(img, x, y, null);
    }
    private void drawDestroyedMark(Graphics2D g2, Unit u) {
        BufferedImage img = u.getImage();
        int x = u.getPixelX() - img.getWidth() / 2;
        int y = u.getPixelY() - img.getHeight() / 2;
        g2.setColor(Color.RED);
        g2.drawLine(x, y, x + img.getWidth(), y + img.getHeight());
        g2.drawLine(x, y + img.getHeight(), x + img.getWidth(), y);
    }





    /* =========================
       SELECTION / QUERY
       ========================= */

    public Unit getUnitAtPixel(int x, int y) {
        for (int i = units.size() - 1; i >= 0; i--) {
            Unit u = units.get(i);
            BufferedImage img = u.getImage();
            int px = u.getPixelX() - img.getWidth() / 2;
            int py = u.getPixelY() - img.getHeight() / 2;

            if (x >= px && x <= px + img.getWidth() &&
                y >= py && y <= py + img.getHeight() &&
                u.getUnitStatus() == UnitStatus.ALIVE) {
                return u;
            }
        }
        return null;
    }

    /* =========================
       LINE OF SIGHT
       ========================= */

    public boolean hasLOS(Unit a, Unit b) {
        double dx = b.getUtmX() - a.getUtmX();
        double dy = b.getUtmY() - a.getUtmY();

        int steps = 100;
        for (int i = 1; i <= steps; i++) {
            double t = i / 100.0;
            double x = a.getUtmX() + dx * t;
            double y = a.getUtmY() + dy * t;
            double z = a.getUtmZ() + (b.getUtmZ() - a.getUtmZ()) * t;

            double terrainZ = MapUtils.getElevationAtUTM(dem, x, y);
            if (terrainZ > z) return false;
        }
        return true;
    }
}
