package me.combatsim.java;

import java.util.List;
import java.util.Random;

import me.combatsim.java.weapons.WeaponDefinition;

public class BattleManager {

  private final UnitManager unitManager;
  private final DetectionManager detectionManager;
  private final Random rand = new Random();

  public BattleManager(UnitManager unitManager, DetectionManager detectionManager) {
    this.unitManager = unitManager;
    this.detectionManager = detectionManager;
  }

  /** Run one combat turn */
  public void runTurn() {

    // Update detection first (use current units in UnitManager)
    detectionManager.update(
      unitManager.getFriendlyUnits(),
      unitManager.getEnemyUnits()
    );

    // Friendly units attack
    for (Unit friendly: unitManager.getFriendlyUnits()) {
      resolveCombatForUnit(friendly);
    }

    // Enemy units attack
    for (Unit enemy: unitManager.getEnemyUnits()) {
      resolveCombatForUnit(enemy);
    }
  }

  /** Resolve combat for a single unit */
  private void resolveCombatForUnit(Unit attacker) {
	    if (attacker.getUnitStatus() != UnitStatus.ALIVE) return;

	    // 🔹 APPLY PLANNED MOVE FIRST
	    if (attacker.hasPlannedMove()) {
	        attacker.moveTowardPlannedTarget();
	    }

	    List<Unit> detectedTargets = detectionManager.getDetectedUnits(attacker);
	    if (detectedTargets == null || detectedTargets.isEmpty()) return;

	    Unit closestTarget = null;
	    double closestDistance = Double.MAX_VALUE;

	    for (Unit target : detectedTargets) {
	        if (target.getUnitStatus() != UnitStatus.ALIVE) continue;

	        double dist = attacker.distance2dTo(target);
	        if (dist < closestDistance) {
	            closestDistance = dist;
	            closestTarget = target;
	        }
	    }

	    if (closestTarget != null) {
	        WeaponDefinition weapon = attacker.getWeapon();
	        double distance = attacker.distance2dTo(closestTarget);

	        if (weapon != null && distance > weapon.getMaxRange()) {
	            attacker.moveToward(closestTarget);
	        }

	        attack(attacker, closestTarget);
	    }
	}


  /** Simple combat resolution */
  private void attack(Unit attacker, Unit target) {
    if (attacker.getUnitStatus() != UnitStatus.ALIVE) return;
    if (target.getUnitStatus() != UnitStatus.ALIVE) return;

    WeaponDefinition weapon = attacker.getWeapon();
    if (weapon == null) return;

    double distance = attacker.distance2dTo(target);
    if (distance > weapon.getMaxRange()) return;

    double hitChance = weapon.getKillProbability();
    //hitChance = Math.max(0.05, Math.min(0.95, hitChance));
    hitChance=attacker.getWeapon().getKillProbability();

    // Attack
    boolean destroyed = rand.nextDouble() < hitChance;
    if (destroyed) {
      // Update the *same instance* of the unit in UnitManager
      target.setUnitStatus(UnitStatus.DESTROYED);
      /*
       * System.out.println("[COMBAT] " + attacker.getName() + " destroyed " +
       * target.getName());
       */
    } else {
      //            System.out.println("[COMBAT] " + attacker.getName() +
      //                               " attacked " + target.getName() + " but missed");
    }
  }
  private Unit findClosestAliveTarget(Unit attacker, List < Unit > targets) {

    Unit best = null;
    double minDist = Double.MAX_VALUE;

    for (Unit t: targets) {
      if (!t.isAlive()) continue;

      double d = attacker.distance2dTo(t);
      if (d < minDist) {
        minDist = d;
        best = t;
      }
    }
    return best;
  }

}