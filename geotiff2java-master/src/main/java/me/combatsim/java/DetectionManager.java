package me.combatsim.java;

import java.awt.*;
import java.util.*;
import java.util.List;
import org.opengis.referencing.operation.MathTransform;
import me.combatsim.java.map.ElevationModel;

public class DetectionManager {

    // Structure: Observer -> (Target -> DetectionScore 0-100)
    private final Map<Unit, Map<Unit, Double>> continuousDetectionMap = new HashMap<>();
    // This maintains your original API for the rest of the app
    private final Map<Unit, List<Unit>> publicDetectionMap = new HashMap<>();
    
    private final ElevationModel dem;
    private final MathTransform utmToWgs;

    public DetectionManager(ElevationModel dem, MathTransform utmToWgs) {
        this.dem = dem;
        this.utmToWgs = utmToWgs;
    }

    public void update(List<Unit> friendlyUnits, List<Unit> enemyUnits) {
        // We update the probability for both sides
        processSide(friendlyUnits, enemyUnits);
        processSide(enemyUnits, friendlyUnits);
        
        // Update the public list of "actually detected" units (score > 50)
        refreshPublicMap();
    }

    private void processSide(List<Unit> observers, List<Unit> targets) {
        for (Unit u : observers) {
            if (u.getUnitStatus() != UnitStatus.ALIVE) continue;

            Map<Unit, Double> scores = continuousDetectionMap.computeIfAbsent(u, k -> new HashMap<>());

            for (Unit e : targets) {
                double distance = u.distance2dTo(e);
                double currentScore = scores.getOrDefault(e, 0.0);

                // 1. Physical check (Hard limits)
                if (distance <= u.getSensorRange() && u.hasLOS(e, u, dem, utmToWgs)) {
                    
                    // 2. Power Law Probability Formula: P = (1 - d/d_max)^k
                    // k=2 is standard. Lower k (e.g. 1.5) makes spotting easier at distance.
                    double pBase = Math.pow(1.0 - (distance / u.getSensorRange()), 2.0);
                    
                    // 3. Apply Target Radius (Scale)
                    // A bigger radius increases the chance of a "successful glance"
                    double finalProb = pBase * (e.getunitRadius() * 1.5); 

                    // 4. The Roll
                    if (Math.random() < finalProb) {
                        // Spotted something! Increase score based on size
                        currentScore += (10.0 * e.getunitRadius());
                    } else {
                        // Glance failed, slow decay
                        currentScore -= 0.5;
                    }
                } else {
                    // No LOS or Out of Range: Detection fades away
                    currentScore -= 2.0; 
                }

                scores.put(e, Math.max(0, Math.min(100, currentScore)));
            }
        }
    }

    private void refreshPublicMap() {
        publicDetectionMap.clear();
        for (var entry : continuousDetectionMap.entrySet()) {
            List<Unit> spotted = new ArrayList<>();
            for (var targetEntry : entry.getValue().entrySet()) {
                if (targetEntry.getValue() >= 50.0) { // Detection Threshold
                    spotted.add(targetEntry.getKey());
                }
            }
            publicDetectionMap.put(entry.getKey(), spotted);
        }
    }

    public List<Unit> getDetectedUnits(Unit u) {
        return publicDetectionMap.getOrDefault(u, Collections.emptyList());
    }

    public void draw(Graphics2D g) {
        g.setStroke(new BasicStroke(1f));
        for (var entry : publicDetectionMap.entrySet()) {
            Unit u = entry.getKey();
            for (Unit target : entry.getValue()) {
                // Feature: Line transparency based on detection score
                double score = continuousDetectionMap.get(u).get(target);
                int alpha = (int) (score * 2.55); // Map 0-100 to 0-255
                g.setColor(new Color(255, 0, 0, alpha));
                g.drawLine(u.getPixelX(), u.getPixelY(), target.getPixelX(), target.getPixelY());
            }
        }
    }

 
	public Map<UnitType, Integer> getDetectedFriendlyUnits() {
	    Map<UnitType, Integer> result = new EnumMap<>(UnitType.class);
	    Set<Unit> uniqueDetected = new HashSet<>();

	    // Collect all detected targets (unique)
	    for (List<Unit> detectedList : publicDetectionMap.values()) {
	        uniqueDetected.addAll(detectedList);
	    }

	    // Filter + group
	    for (Unit u : uniqueDetected) {
	        if (u.getUnitStatus() != UnitStatus.ALIVE) continue;
	        if (u.getUnitTeam() != UnitTeam.FRIENDLY) continue;

	        result.merge(u.getUnitType(), 1, Integer::sum);
	    }

	    return result;
	}

	public Map<UnitType, Integer> getDetectedEnemyUnits() {
	    Map<UnitType, Integer> result = new EnumMap<>(UnitType.class);
	    Set<Unit> uniqueDetected = new HashSet<>();

	    // Collect all detected targets (unique)
	    for (List<Unit> detectedList : publicDetectionMap.values()) {
	        uniqueDetected.addAll(detectedList);
	    }

	    // Filter + group
	    for (Unit u : uniqueDetected) {
	        if (u.getUnitStatus() != UnitStatus.ALIVE) continue;
	        if (u.getUnitTeam() != UnitTeam.ENEMY) continue;

	        result.merge(u.getUnitType(), 1, Integer::sum);
	    }

	    return result;
	}

}