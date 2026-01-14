package me.combatsim.java;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
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

    /* =========================
       UPDATE
       ========================= */

    public void update(UnitManager unitManager) {
        detectionMap.clear();

        List<Unit> friendlyUnits = unitManager.getFriendlyUnits();
        List<Unit> enemyUnits    = unitManager.getEnemyUnits();

        // Friendly → Enemy
        detectBetween(friendlyUnits, enemyUnits);

        // Enemy → Friendly
        detectBetween(enemyUnits, friendlyUnits);
    }
    
   


    private void detectBetween(List<Unit> observers, List<Unit> targets) {
        for (Unit observer : observers) {
            List<Unit> detected = new ArrayList<>();

            for (Unit target : targets) {
                if (observer == target) continue;

                if (observer.canDetect(target, dem, utmToWgs)) {
                    detected.add(target);
                }
            }

            detectionMap.put(observer, detected);
        }
    }

    /* =========================
       QUERY
       ========================= */

    public List<Unit> getDetectedUnits(Unit u) {
        return detectionMap.getOrDefault(u, List.of());
    }

    public boolean isDetected(Unit observer, Unit target) {
        return detectionMap
                .getOrDefault(observer, List.of())
                .contains(target);
    }
    
    public boolean isTargetDetected(Unit target) {
        for (List<Unit> detected : detectionMap.values()) {
            if (detected.contains(target)) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        detectionMap.clear();
    }
    
    
  

    public void draw(Graphics2D g) {
        Stroke oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(1.5f));

        for (Map.Entry<Unit, List<Unit>> entry : detectionMap.entrySet()) {
            Unit observer = entry.getKey();

            for (Unit target : entry.getValue()) {

                if (observer.getUnitTeam() == UnitTeam.FRIENDLY) {
                    g.setColor(new Color(0, 255, 0, 120)); // translucent green
                } else {
                    g.setColor(new Color(255, 0, 0, 120)); // translucent red
                }

                g.drawLine(
                    observer.getPixelX(),
                    observer.getPixelY(),
                    target.getPixelX(),
                    target.getPixelY()
                );
            }
        }

        g.setStroke(oldStroke);
    }

    public void update(List<Unit> friendlyUnits, List<Unit> enemyUnits) {

        detectionMap.clear();

        if (friendlyUnits == null || enemyUnits == null) {
            return;
        }

        // ---- Friendly units detect enemies ----
        for (Unit friendly : friendlyUnits) {

            List<Unit> detectedEnemies = new ArrayList<>();

            for (Unit enemy : enemyUnits) {
                if (friendly.canDetect(enemy, dem, utmToWgs)) {
                    detectedEnemies.add(enemy);
                }
            }

            detectionMap.put(friendly, detectedEnemies);
        }

        // ---- Enemy units detect friendlies ----
        for (Unit enemy : enemyUnits) {

            List<Unit> detectedFriendlies = new ArrayList<>();

            for (Unit friendly : friendlyUnits) {
                if (enemy.canDetect(friendly, dem, utmToWgs)) {
                    detectedFriendlies.add(friendly);
                }
            }

            detectionMap.put(enemy, detectedFriendlies);
        }
    }



	
}
