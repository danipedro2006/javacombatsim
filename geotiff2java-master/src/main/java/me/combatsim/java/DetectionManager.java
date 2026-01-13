package me.combatsim.java;

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

    public void clear() {
        detectionMap.clear();
    }
}
