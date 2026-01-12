package me.combatsim.java;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UnitBootstrap {

    private final UnitFactory factory;
    final UnitManager unitManager;

    public UnitBootstrap(UnitFactory factory, UnitManager unitManager) {
        this.factory = factory;
        this.unitManager = unitManager;
    }

    public void loadFromCSV(String resourcePath) throws Exception {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        UnitBootstrap.class.getResourceAsStream("/me/combatsim/java/scenario.csv")
                )
        );

        if (reader == null) {
            throw new IllegalArgumentException("Scenario file not found: " + resourcePath);
        }

        String line;
        boolean firstLine = true;

        while ((line = reader.readLine()) != null) {

            if (firstLine) {
                firstLine = false;
                continue;
            }

            String[] parts = line.split(",");
            if (parts.length < 4) continue;

            UnitType type = UnitType.valueOf(parts[0].trim());
            UnitTeam team = UnitTeam.valueOf(parts[1].trim());
            int x = Integer.parseInt(parts[2].trim());
            int y = Integer.parseInt(parts[3].trim());

            Unit unit = null;

            switch (type) {
                case INFANTRY:
                    unit = factory.createInfantry(x, y, team);
                    unitManager.units.add(unit);
                    break;

                case TANK:
                    unit = factory.createTank(x, y, team);
                    unitManager.add(unit);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown unit type: " + type);
            }

            unitManager.addUnit(unit);
        }

        reader.close();
    }
}
