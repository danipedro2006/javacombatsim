package me.combatsim.java;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UnitBootstrap {

    private final UnitFactory factory;
    private final UnitManager unitManager;

    public UnitBootstrap(UnitFactory factory, UnitManager unitManager) {
        this.factory = factory;
        this.unitManager = unitManager;
    }

    public void loadFromCSV(String resourcePath) throws Exception {

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                UnitBootstrap.class.getResourceAsStream(resourcePath)
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

            String[] p = line.split(",");
            if (p.length < 12) {
                System.err.println("Invalid scenario row: " + line);
                continue;
            }

            String name = p[0].trim();
            UnitType type = UnitType.valueOf(p[1].trim());
            UnitTeam team = UnitTeam.valueOf(p[2].trim());
            int x = Integer.parseInt(p[3].trim());
            int y = Integer.parseInt(p[4].trim());
            boolean visible = Boolean.parseBoolean(p[5].trim());
            double sensor = Double.parseDouble(p[6].trim());
            double combat = Double.parseDouble(p[7].trim());
            double speed = Double.parseDouble(p[8].trim());
            WeaponDefinition weapon = WeaponRepository.get(p[9].trim());
            double radius = Double.parseDouble(p[10].trim());
            String symbol = p[11].trim();

            Unit u = factory.createUnit(
                name,
                type,
                team,
                x,
                y,
                visible,
                sensor,
                combat,
                speed,
                weapon,
                radius,
                symbol
            );

            unitManager.addUnit(u);
        }

        reader.close();
    }
}
