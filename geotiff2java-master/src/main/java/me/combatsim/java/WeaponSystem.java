package me.combatsim.java;

public class WeaponSystem {

    private final WeaponDefinition def;

    public WeaponSystem(WeaponDefinition def) {
        this.def = def;
    }

    /** Can this weapon engage the target from attacker, optionally checking LOS */
    public boolean canEngage(Unit attacker, Unit target, boolean hasLOS) {
        double distance = attacker.distance2dTo(target);

        if (distance < def.minRange || distance > def.maxRange)
            return false;

        if (def.requiresLOS && !hasLOS)
            return false;

        return true;
    }

    /** Compute probabilistic kill chance given distance */
    public double computeKillProbability(double distance) {
        double factor = 1.0 - (distance / def.maxRange);
        factor = clamp(factor, 0.0, 1.0);
        return def.killProbability * factor;
    }

    /** Weapon info */
    public WeaponDefinition getDefinition() {
        return def;
    }

    /** Current rounds remaining */
    public int getRounds() {
        return def.rounds;
    }

    /** Is a target at this distance in range? */
    public boolean isInRange(double distance) {
        return distance >= def.minRange && distance <= def.maxRange;
    }

    /** Can weapon fire? */
    public boolean canFire() {
        return getRounds() > 0;
    }

    /** Base kill probability, independent of distance */
    public double getKillProbability() {
        return def.killProbability;
    }

    /** Consume a round after firing */
    public void consumeRound() {
        if (def.rounds > 0) def.rounds--;
    }

    /** Utility clamp */
    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
