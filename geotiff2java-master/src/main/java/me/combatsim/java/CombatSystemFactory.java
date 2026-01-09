package me.combatsim.java;

 

public final class CombatSystemFactory {

    private CombatSystemFactory() {}

    public static CombatSystem rifle() {
        return new CombatSystem(
            "Rifle",
            400.00,
            0.25,
            10
        );
    }

    public static CombatSystem tankCannon() {
        return new CombatSystem(
            "Tank Cannon",
            2500.00,
            0.6,
            80
        );
    }

    public static CombatSystem artillery() {
        return new CombatSystem(
            "Artillery",
            12000.00,
            0.4,
            120
        );
    }
}
