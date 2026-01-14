package me.combatsim.java;

import java.util.List;
import java.util.Random;

public class BattleManager {

    private final UnitManager unitManager;
    private final DetectionManager detectionManager;
    private final Random rand = new Random();

    public BattleManager(UnitManager unitManager,
                         DetectionManager detectionManager) {
        this.unitManager = unitManager;
        this.detectionManager = detectionManager;
    }

    /** Run one combat turn */
    public void runTurn() {

        // Update detection first
        detectionManager.update(
                unitManager.getFriendlyUnits(),
                unitManager.getEnemyUnits()
        );

        // Friendly units attack
        for (Unit friendly : unitManager.getFriendlyUnits()) {
            resolveCombatForUnit(friendly);
        }

        // Enemy units attack
        for (Unit enemy : unitManager.getEnemyUnits()) {
            resolveCombatForUnit(enemy);
        }
    }

    /** Resolve combat for a single unit */
    private void resolveCombatForUnit(Unit attacker) {

        if (!(attacker.isAlive()==UnitStatus.ALIVE)) {
            return;
        }

        List<Unit> detectedTargets =
                detectionManager.getDetectedUnits(attacker);

        if (detectedTargets == null || detectedTargets.isEmpty()) {
            return;
        }

        Unit closestTarget = null;
        double closestDistance = Double.MAX_VALUE;

        for (Unit target : detectedTargets) {
            if (!(target.isAlive()==UnitStatus.ALIVE)) {
                continue;
            }

            double dist = attacker.distance2dTo(target);
            if (dist < closestDistance) {
                closestDistance = dist;
                closestTarget = target;
            }
        }

        if (closestTarget != null) {
            attack(attacker, closestTarget);
        }
    }

    /** Simple combat resolution */
    private void attack(Unit attacker, Unit target) {

        WeaponDefinition weapon = attacker.getWeapon();
        if (weapon == null) {
            return;
        }

        double distance = attacker.distance2dTo(target);

        if (distance > weapon.getMaxRange()) {
            return;
        }

        double hitChance =
                weapon.getKillProbability();

        if (hitChance < 0.05) hitChance = 0.05;
        if (hitChance > 0.95) hitChance = 0.95;

        if (rand.nextDouble() < hitChance) {
            target.setUnitStatus(UnitStatus.DESTROYED);
            System.out.println(
                "[COMBAT] " + attacker.getName() +
                " destroyed " + target.getName()
            );
        } else {
            System.out.println(
                "[COMBAT] " + attacker.getName() +
                " attacked " + target.getName() +
                " but missed"
            );
        }
    }
}
