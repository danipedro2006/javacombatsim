package me.combatsim.java;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.util.List;

public class BattleTest {

    public static void main(String[] args) throws Exception {

        System.out.println("=== BATTLE TEST START ===");

        // ---- Map + DEM ----
        MapContext ctx = new MapContext();

        // ---- CRS transforms ----
        CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326", true);
        CoordinateReferenceSystem utm = CRS.decode("EPSG:32634", true);
        MathTransform wgsToUtm = CRS.findMathTransform(wgs84, utm, true);
        MathTransform utmToWgs = CRS.findMathTransform(utm, wgs84, true);

        // ---- Load weapons ----
        WeaponLoader.loadFromCSV("/me/combatsim/java/weapons.csv");

        // ---- Managers ----
        UnitManager unitManager = new UnitManager(utmToWgs, ctx.dem);
        DetectionManager detectionManager =
                new DetectionManager(ctx.dem, utmToWgs);
        BattleManager battleManager =
                new BattleManager(unitManager, detectionManager);

        // ---- Factory + Bootstrap ----
        UnitFactory factory =
                new UnitFactory(ctx.dem, wgsToUtm, utmToWgs);
        UnitBootstrap bootstrap =
                new UnitBootstrap(factory, unitManager);

        bootstrap.loadFromCSV("/me/combatsim/java/scenario.csv");

        System.out.println("Units loaded: " + unitManager.getUnits().size());

        // ---- Run battle turns ----
        for (int turn = 1; turn <= 20; turn++) {

            System.out.println("\n--- TURN " + turn + " ---");

            battleManager.runTurn();

            // ---- Stop if one side is eliminated ----
            if (unitManager.getFriendlyUnits().isEmpty()
                    || unitManager.getEnemyUnits().isEmpty()) {
                break;
            }
        }

        // ---- Final state ----
        System.out.println("\n=== FINAL STATE ===");

        printSide("FRIENDLY", unitManager.getFriendlyUnits());
        printSide("ENEMY", unitManager.getEnemyUnits());

        System.out.println("=== BATTLE TEST END ===");
    }

    private static void printSide(String label, List<Unit> units) {
        System.out.println(label + " UNITS:");
        if (units.isEmpty()) {
            System.out.println("  none");
            return;
        }

        for (Unit u : units) {
            System.out.println(
                    "  " + u.getName() +
                    " | Status=" + u.isAlive()
            );
        }
    }
}
