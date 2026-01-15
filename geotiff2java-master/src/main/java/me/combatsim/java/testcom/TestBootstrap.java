package me.combatsim.java.testcom;

import me.combatsim.java.Unit;
import me.combatsim.java.UnitManager;
import me.combatsim.java.UnitStatus;
import me.combatsim.java.UnitTeam;
import me.combatsim.java.UnitType;
import me.combatsim.java.map.MapContext;
import me.combatsim.java.weapons.WeaponDefinition;
import me.combatsim.java.weapons.WeaponLoader;
import me.combatsim.java.weapons.WeaponRepository;

public class TestBootstrap {

    private final UnitManager unitManager;

    public TestBootstrap() throws Exception {
    	WeaponLoader.loadFromCSV("/me/combatsim/java/weapons.csv");
        MapContext ctx = new MapContext();
        unitManager = new UnitManager(ctx.utmToWgs, ctx.dem);
        WeaponDefinition weapon=WeaponRepository.get("Javelin");
       
        // Create units directly (NO UnitFactory)
        Unit alive = new Unit(
                "ALIVE",
                UnitType.INFANTRY,
                UnitTeam.FRIENDLY,
                200, 200,
                true,
                0,
                0,
                0,
                weapon,          // weapon = null
                10,
                "infantry.bmp",
                ctx.dem,
                ctx.wgsToUtm,
                ctx.utmToWgs
        );

        Unit dead1 = new Unit(
                "DEAD-1",
                UnitType.INFANTRY,
                UnitTeam.FRIENDLY,
                300, 200,
                true,
                0,
                0,
                0,
                weapon,
                10,
                "infantry.bmp",
                ctx.dem,
                ctx.wgsToUtm,
                ctx.utmToWgs
        );

        Unit dead2 = new Unit(
                "DEAD-2",
                UnitType.INFANTRY,
                UnitTeam.ENEMY,
                400, 300,
                true,
                0,
                0,
                0,
                null,
                10,
                "infantry.bmp",
                ctx.dem,
                ctx.wgsToUtm,
                ctx.utmToWgs
        );
        alive.setUnitStatus(UnitStatus.DESTROYED);
        dead1.setUnitStatus(UnitStatus.DESTROYED);
        dead2.setUnitStatus(UnitStatus.DESTROYED);

        unitManager.addUnit(alive);
        unitManager.addUnit(dead1);
        unitManager.addUnit(dead2);
    }

    public UnitManager getUnitManager() {
        return unitManager;
    }
}
