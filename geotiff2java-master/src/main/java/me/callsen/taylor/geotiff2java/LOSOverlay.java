package me.callsen.taylor.geotiff2java;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.DirectPosition2D;
import org.opengis.referencing.operation.MathTransform;

public class LOSOverlay implements Overlay {

    private final ElevationModel dem;
    private final MathTransform utmToWgs; // inverse of wgsToUtm
    private final int mapWidth;
    private final int mapHeight;

    private final List<Line2D> losLines = new ArrayList<>();
    private boolean visible = false;

    public LOSOverlay(ElevationModel dem, MathTransform wgsToUtm, int mapWidth, int mapHeight) throws Exception {
        this.dem = dem;
        this.utmToWgs = wgsToUtm.inverse(); // store inverse for UTM -> WGS84
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    /** Toggle visibility */
    public void toggle() {
        visible = !visible;
        if (visible) losLines.clear(); // recalc when turned on
    }

    /**
     * Compute 360° LOS from a pixel observer
     */
    public void computeLOS(int xPixel, int yPixel) {
        losLines.clear();

        try {
            // Observer position in UTM
            DirectPosition2D obsUTM = MapUtils.pixelToUTM(xPixel, yPixel, utmToWgs.inverse());
            double observerElev = MapUtils.getElevationAtPixel(dem, xPixel, yPixel);

            int raySteps = 200;
            double stepSize = 10;
            int angleStep = 15;

            for (int angleDeg = 0; angleDeg < 360; angleDeg += angleStep) {
                double angleRad = Math.toRadians(angleDeg);
                int prevX = xPixel;
                int prevY = yPixel;

                for (int s = 1; s <= raySteps; s++) {
                    double distance = s * stepSize;
                    double dx = distance * Math.cos(angleRad);
                    double dy = distance * Math.sin(angleRad);

                    double sampleX = obsUTM.x + dx;
                    double sampleY = obsUTM.y + dy;

                    // Convert UTM → Pixel
                    Point2D.Double samplePixel = MapUtils.utmToPixel(sampleX, sampleY, utmToWgs);
                    int px = (int) Math.round(samplePixel.x);
                    int py = (int) Math.round(samplePixel.y);

                    // Stop if outside map bounds
                    if (px < 0 || py < 0 || px >= mapWidth || py >= mapHeight) break;

                    double terrainElev = MapUtils.getElevationAtPixel(dem, px, py);
                    double slope = (terrainElev - observerElev) / distance;

                    if (slope > 0) break; // terrain blocks LOS

                    losLines.add(new Line2D.Double(prevX, prevY, px, py));
                    prevX = px;
                    prevY = py;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Draw overlay */
    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;

        g.setColor(new Color(255, 0, 0, 120));
        for (Line2D line : losLines) {
            g.draw(line);
        }
    }

    public boolean isVisible() {
        return visible;
    }
}
