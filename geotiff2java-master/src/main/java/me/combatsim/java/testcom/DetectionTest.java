package me.combatsim.java.testcom;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import me.combatsim.java.DetectionManager;
import me.combatsim.java.Unit;
import me.combatsim.java.UnitBootstrap;
import me.combatsim.java.UnitFactory;
import me.combatsim.java.UnitManager;
import me.combatsim.java.map.MapContext;
import me.combatsim.java.weapons.WeaponLoader;

import java.util.List;
import java.util.stream.Collectors;

public class DetectionTest {

    public static void main(String[] args) throws Exception {

        System.out.println("=== UNIT BOOTSTRAP + DETECTION TEST ===");

        /* =========================
           MAP + CRS SETUP
           ========================= */

        MapContext ctx = new MapContext();

        CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326", true);
        CoordinateReferenceSystem utm = CRS.decode("EPSG:32634", true);

        MathTransform wgsToUtm = CRS.findMathTransform(wgs84, utm, true);
        MathTransform utmToWgs = CRS.findMathTransform(utm, wgs84, true);

        /* =========================
           LOAD WEAPONS
           ========================= */

        WeaponLoader.loadFromCSV("/me/combatsim/java/weapons.csv");

        /* =========================
           UNIT SYSTEM
           ========================= */

        UnitFactory factory = new UnitFactory(ctx.dem, wgsToUtm, utmToWgs);
        UnitManager unitManager = new UnitManager(utmToWgs, ctx.dem);

        UnitBootstrap bootstrap = new UnitBootstrap(factory, unitManager);

        // 🔥 Load units from scenario CSV
        bootstrap.loadFromCSV("/me/combatsim/java/scenario.csv");

        System.out.println("Units loaded: " + unitManager.getUnits().size());

        /* =========================
           DETECTION
           ========================= */

        DetectionManager detectionManager =
                new DetectionManager(ctx.dem, utmToWgs);

        detectionManager.update(unitManager);

        System.out.println("\n=== Detection Report ===");

        /* ---- Friendly units ---- */
        for (Unit u : unitManager.getFriendlyUnits()) {
            List<String> detected =
                    detectionManager.getDetectedUnits(u)
                            .stream()
                            .map(Unit::getName)
                            .collect(Collectors.toList());

            System.out.printf(
                    "%s detected: %s%n",
                    u.getName(),
                    detected
            );
        }

        /* ---- Enemy units ---- */
        for (Unit e : unitManager.getEnemyUnits()) {
            List<String> detected =
                    detectionManager.getDetectedUnits(e)
                            .stream()
                            .map(Unit::getName)
                            .collect(Collectors.toList());

            System.out.printf(
                    "%s detected: %s%n",
                    e.getName(),
                    detected
            );
        }

        System.out.println("=== DETECTION TEST END ===");
    }
}
