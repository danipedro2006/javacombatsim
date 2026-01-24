package me.combatsim.java;

 

import me.combatsim.java.Unit;
import me.combatsim.java.UnitManager;
import me.combatsim.java.UnitStatus;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class SimulationStateIO {

    private SimulationStateIO() {
        // utility class
    }

    // =========================
    // SAVE
    // =========================
    public static void save(String file, UnitManager unitManager) throws IOException {

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.println("NAME,UTM_X,UTM_Y,STATUS");

            for (Unit u : unitManager.getUnits()) {
                out.printf(
                    "%s,%.3f,%.3f,%s%n",
                    u.getName(),
                    u.getUtmX(),
                    u.getUtmY(),
                    u.getUnitStatus().name()
                );
            }
        }

        System.out.println("[SAVE] Simulation state saved to " + file);
    }

    // =========================
    // LOAD
    // =========================
    public static void load(String string, UnitManager unitManager) throws IOException {

         
        // Build lookup by unit name
        Map<String, Unit> unitsByName = new HashMap<>();
        for (Unit u : unitManager.getUnits()) {
            unitsByName.put(u.getName(), u);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(string))) {

            String line = br.readLine(); // header
            if (line == null) return;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String name = parts[0];
                double utmX = Double.parseDouble(parts[1]);
                double utmY = Double.parseDouble(parts[2]);
                UnitStatus status = UnitStatus.valueOf(parts[3]);

                Unit u = unitsByName.get(name);
                if (u == null) {
                    System.err.println("[LOAD] Unit not found: " + name);
                    continue;
                }

                u.setUtmPosition(utmX, utmY);
                u.setUnitStatus(status);
                unitManager.updateRenderPositions();
                 
            }
        }

        System.out.println("[LOAD] Simulation state loaded from " + string);
    }
}
