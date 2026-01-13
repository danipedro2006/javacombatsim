package me.combatsim.java;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.util.List;

public class DetectionTest {

    public static void main(String[] args) throws Exception {

        System.out.println("=== DETECTION TEST START ===");

        // ---- Setup map and DEM ----
        MapContext ctx = new MapContext();

        // ---- CRS transforms ----
        CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326", true);
        CoordinateReferenceSystem utm   = CRS.decode("EPSG:32634", true);
        MathTransform wgsToUtm = CRS.findMathTransform(wgs84, utm, true);
        MathTransform utmToWgs = CRS.findMathTransform(utm, wgs84, true);

        // ---- Load weapons ----
        WeaponLoader.loadFromCSV("/me/combatsim/java/weapons.csv");

        // ---- Unit factory and manager ----
        UnitFactory factory = new UnitFactory(ctx.dem, wgsToUtm, utmToWgs);
        UnitManager unitManager = new UnitManager(utmToWgs, ctx.dem);

        // ---- Create units ----
        //name,type,team,x,y,visible,sensorRange,combatPower,speed,weapon,radius,symbol
        WeaponDefinition Javelin = WeaponRepository.get("Javelin");
        Unit f1=factory.createUnit("alfa", UnitType.INFANTRY, UnitTeam.FRIENDLY, 600,700,true, 5000,10,20,Javelin,20,"infantry_blue.bmp");
        Unit f2=factory.createUnit("beta", UnitType.INFANTRY, UnitTeam.ENEMY, 650,750,true, 5000,10,20,Javelin,20,"infantry.bmp");

        // ---- Register units ----
        add(unitManager, f1);
        add(unitManager, f2);
        

        // ---- Detection manager ----
        DetectionManager dm = new DetectionManager(ctx.dem, utmToWgs);
        dm.update(unitManager);

        System.out.println("\n=== Detection Report ===");

        // ---- Friendly units ----
        for (Unit u : unitManager.getFriendlyUnits()) {
            printDetected(dm, u);
        }

        // ---- Enemy units ----
        for (Unit u : unitManager.getEnemyUnits()) {
            printDetected(dm, u);
        }

        System.out.println("=== DETECTION TEST END ===");
    }

    /* =========================
       HELPERS
       ========================= */

    private static void add(UnitManager um, Unit u) {
             // main list
        um.addUnit(u);  // side list
    }

    private static void printDetected(DetectionManager dm, Unit u) {
        List<Unit> detected = dm.getDetectedUnits(u);

        System.out.print(u.getName() + " detected: ");

        if (detected.isEmpty()) {
            System.out.println("[]");
            return;
        }

        System.out.print("[ ");
        for (Unit t : detected) {
            System.out.print(t.getName() + " ");
        }
        System.out.println("]");
    }
}
