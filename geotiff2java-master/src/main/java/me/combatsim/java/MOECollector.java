package me.combatsim.java;

import java.util.EnumMap;
import java.util.Map;

public class MOECollector {

    // ---- TURN ----
    private int turn;

    // ---- FORCE ----
    private int friendlyAlive;
    private int enemyAlive;

    // ---- LOSSES ----
    private int friendlyLosses;
    private int enemyLosses;

    // ---- ATTACKS ----
    private int friendlyAttacks;
    private int enemyAttacks;

    // ---- DETECTION ----
    private int friendlyDetected;
    private int enemyDetected;

    // ---- BLIND KILLS ----
    private int blindKills;

    // ---- LOSSES BY TYPE ----
    private final Map<UnitType, Integer> friendlyLossesByType = new EnumMap<>(UnitType.class);
    private final Map<UnitType, Integer> enemyLossesByType = new EnumMap<>(UnitType.class);

    public MOECollector() {
        for (UnitType t : UnitType.values()) {
            friendlyLossesByType.put(t, 0);
            enemyLossesByType.put(t, 0);
        }
    }
}
