package me.combatsim.java.overlay;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.DirectPosition2D;
import org.opengis.referencing.operation.MathTransform;

import me.combatsim.java.map.ElevationModel;
import me.combatsim.java.map.MapUtils;

public class LOSOverlay implements Overlay {

    private final ElevationModel dem;
    private final MathTransform utmToWgs; // inverse of wgsToUtm
    private final int mapWidth;
    private final int mapHeight;
 // ---- Distance measurement ----
    private Point distanceP1 = null;
    private Point distanceP2 = null;
    private Double distanceMeters = null;

    private final List<Line2D> losLines = new ArrayList<>();
     
    private boolean visible=false;

    
    public LOSOverlay(ElevationModel dem, MathTransform wgsToUtm, int mapWidth, int mapHeight) throws Exception {
        this.dem = dem;
        this.utmToWgs = wgsToUtm.inverse(); // store inverse for UTM -> WGS84
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
         
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
        System.out.println("[LOSOverlay] draw()");
        g.setColor(Color.RED);
        for (Line2D line : losLines) {
            g.draw(line);
        }
        
     // ---- Distance overlay ----
        if (distanceP1 != null && distanceP2 != null && distanceMeters != null) {
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(2f));
            g.drawLine(
                distanceP1.x, distanceP1.y,
                distanceP2.x, distanceP2.y
            );

            int mx = (distanceP1.x + distanceP2.x) / 2;
            int my = (distanceP1.y + distanceP2.y) / 2;

            String label = String.format("%.1f m", distanceMeters);

            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(mx - 30, my - 15, 70, 20);

            g.setColor(Color.WHITE);
            g.drawString(label, mx - 25, my);
        }

    }

    public boolean isVisible() {
        return this.visible;
    }
 
	/**
	 * Distance in meters between two pixel points
	 */
	public double distanceMeters(int x1, int y1, int x2, int y2) {
	    try {
	        // Pixel → UTM
	        DirectPosition2D p1 = MapUtils.pixelToUTM(x1, y1, utmToWgs.inverse());
	        DirectPosition2D p2 = MapUtils.pixelToUTM(x2, y2, utmToWgs.inverse());

	        double dx = p2.x - p1.x;
	        double dy = p2.y - p1.y;

	        return Math.sqrt(dx * dx + dy * dy);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1;
	    }
	}
	public void setDistancePoints(Point p1, Point p2, double meters) {
	    this.distanceP1 = p1;
	    this.distanceP2 = p2;
	    this.distanceMeters = meters;
	}
	@Override
	public void setVisible(boolean visible) {
	    this.visible = visible;

	    if (!visible) {
	        losLines.clear();
	        distanceP1 = null;
	        distanceP2 = null;
	        distanceMeters = null;
	    }
	}

	 



}
