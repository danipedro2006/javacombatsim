package me.combatsim.java;

import java.util.*;
import java.util.stream.Collectors;

import org.opengis.referencing.operation.MathTransform;

public class BattleManager {

    private final List<Unit> friendlyUnits;
    private final List<Unit> enemyUnits;
    private final ElevationModel dem;
    private final MathTransform utmToWgs;
    private final Random rand = new Random();

    public BattleManager(List<Unit> friendlyUnits, List<Unit> enemyUnits, ElevationModel dem, MathTransform utmToWgs) {
        this.friendlyUnits = friendlyUnits;
        this.enemyUnits = enemyUnits;
        this.dem = dem;
        this.utmToWgs = utmToWgs;
    }

    /** Run one combat turn */
    public void runTurn() {

        // Detection maps
        Map<Unit, List<Unit>> friendlyDetected = computeDetection(friendlyUnits, enemyUnits);
        Map<Unit, List<Unit>> enemyDetected = computeDetection(enemyUnits, friendlyUnits);

        // Friendly units attack
        for (Unit u : friendlyUnits) {
            if (!u.isAlive()) continue;

            List<Unit> targets = friendlyDetected.getOrDefault(u, Collections.emptyList())
                    .stream().filter(Unit::isAlive)
                    .sorted(Comparator.comparingDouble(t -> u.distance2dTo(t)))
                    .collect(Collectors.toList());

            if (!targets.isEmpty()) {
                attack(u, targets.get(0));
            }
        }

        // Enemy units attack
        for (Unit e : enemyUnits) {
            if (!e.isAlive()) continue;

            List<Unit> targets = enemyDetected.getOrDefault(e, Collections.emptyList())
                    .stream().filter(Unit::isAlive)
                    .sorted(Comparator.comparingDouble(t -> e.distance2dTo(t)))
                    .collect(Collectors.toList());

            if (!targets.isEmpty()) {
                attack(e, targets.get(0));
            }
        }
    }

    /** Compute detection map for a side */
    private Map<Unit, List<Unit>> computeDetection(List<Unit> side, List<Unit> opponents) {
        Map<Unit, List<Unit>> detectionMap = new HashMap<>();
        for (Unit u : side) {
            if (!u.isAlive()) continue;

            List<Unit> detected = new ArrayList<>();
            for (Unit o : opponents) {
                if (!o.isAlive()) continue;

                if (u.canDetect(o, dem, utmToWgs) && Unit.hasLOS(u, o, dem, utmToWgs)) {
                    detected.add(o);
                }
            }
            detectionMap.put(u, detected);
        }
        return detectionMap;
    }

    /** Simulate an attack from attacker to target */
    private void attack(Unit attacker, Unit target) {
        if (!attacker.canAttack(target, dem, utmToWgs)) return;

        WeaponDefinition weapon = attacker.getWeapon();  // returns WeaponSystem
        if (weapon == null) return;

        double distance = attacker.distance2dTo(target);
        double killProbability = weapon.computeKillProbability(distance);

        if (rand.nextDouble() < killProbability) {
            target.setStatus(UnitStatus.DESTROYED);
            System.out.printf("%s destroyed %s%n", attacker.getName(), target.getName());
        } else {
            System.out.printf("%s attacked %s but missed%n", attacker.getName(), target.getName());
        }

        // Consume one round
        weapon.consumeRound();
    }
}
