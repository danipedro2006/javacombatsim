package me.combatsim.java;

 

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import me.combatsim.java.map.MapContext;
import me.combatsim.java.weapons.WeaponLoader;

public class UnitBootstrapTest {

    public static void main(String[] args) throws Exception {

        System.out.println("=== UNIT BOOTSTRAP TEST ===");

        // ---- Map context ----
        MapContext ctx = new MapContext();

        // ---- CRS ----
        CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326", true);
        CoordinateReferenceSystem utm   = CRS.decode("EPSG:32634", true);

        MathTransform wgsToUtm = CRS.findMathTransform(wgs84, utm, true);
        MathTransform utmToWgs = CRS.findMathTransform(utm, wgs84, true);

        // ---- Load weapons FIRST ----
        WeaponLoader.loadFromCSV("/me/combatsim/java/weapons.csv");

        // ---- Core objects ----
        UnitManager unitManager = new UnitManager(utmToWgs, ctx.dem);
        UnitFactory factory     = new UnitFactory(ctx.dem, wgsToUtm, utmToWgs);

        UnitBootstrap bootstrap = new UnitBootstrap(factory, unitManager);

        // ---- Load scenario ----
        bootstrap.loadFromCSV("/me/combatsim/java/scenario.csv");

        // ---- Verify ----
        System.out.println("Units loaded: " + unitManager.getEnemyUnits().size());

        for (Unit u : unitManager.getEnemyUnits()) {
            System.out.printf(
                "%s | %s | %s | pos=(%.1f, %.1f) | sensor=%.0f | weapon=%s%n | radius=%.0f | | %s |",
                
                u.getName(),
                u.getUnitType(),
                u.getUnitTeam(),
                u.getUtmX(),
                u.getUtmY(),
                u.getSensorRange(),
                (u.getWeapon() != null ? u.getWeapon().getId() : "NONE"), 
                u.getSensorRange(),
                u.getmapSymbol()
                
            );
        }

        System.out.println("=== TEST COMPLETE ===");
    }
}
