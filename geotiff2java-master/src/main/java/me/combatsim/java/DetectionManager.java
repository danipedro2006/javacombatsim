package me.combatsim.java;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opengis.referencing.operation.MathTransform;

public class DetectionManager {

    private final Map<Unit, List<Unit>> detectionMap = new HashMap<>();
    private final ElevationModel dem;
    private final MathTransform utmToWgs;

    public DetectionManager(ElevationModel dem, MathTransform utmToWgs) {
        this.dem = dem;
        this.utmToWgs = utmToWgs;
    }

    /** Update detection map for given friendly and enemy units */
    public void update(List<Unit> friendlyUnits, List<Unit> enemyUnits) {
        detectionMap.clear();

        for (Unit u : friendlyUnits) {
            List<Unit> detected = new ArrayList<Unit>();
            for (Unit e : enemyUnits) {
                if (u.canDetect(e, dem, utmToWgs)) {
                    detected.add(e);
                }
            }
            detectionMap.put(u, detected);
        }

        for (Unit e : enemyUnits) {
            List<Unit> detected = new ArrayList<Unit>();
            for (Unit u : friendlyUnits) {
                if (e.canDetect(u, dem, utmToWgs)) {
                    detected.add(u);
                }
            }
            detectionMap.put(e, detected);
        }
    }

    /** Get units detected by u */
    public List<Unit> getDetectedUnits(Unit u) {
        return detectionMap.getOrDefault(u, new ArrayList<Unit>());
    }

    /** Draw detection info (optional) */
    public void draw(Graphics2D g) {
    	g.setComposite(AlphaComposite.SrcOver);
    	g.setStroke(new BasicStroke(1f));
    	g.setColor(Color.BLACK);
        // Example: highlight detected units
        g.setColor(new java.awt.Color(255, 0, 0, 128));
        for (Map.Entry<Unit, List<Unit>> entry : detectionMap.entrySet()) {
            Unit u = entry.getKey();
            for (Unit target : entry.getValue()) {
                g.drawLine(u.getPixelX(), u.getPixelY(), target.getPixelX(), target.getPixelY());
            }
        }
    }
}
