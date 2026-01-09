package me.combatsim.java;

import org.opengis.referencing.operation.MathTransform;

public class UnitFactory {

    private final ElevationModel dem;
    private final MathTransform wgsToUtm;
    private final MathTransform utmToWgs;

    public UnitFactory(ElevationModel dem,
                       MathTransform wgsToUtm,
                       MathTransform utmToWgs) {
        this.dem = dem;
        this.wgsToUtm = wgsToUtm;
        this.utmToWgs = utmToWgs;
    }

    public Unit createInfantry(int px, int py, UnitTeam team) throws Exception {
        Unit u = new Unit(px, py, "infantry.bmp", dem, wgsToUtm, utmToWgs);

        u.setName("Infantry");
        u.setUnitType(UnitType.INFANTRY);
        u.setUnitTeam(team);
        u.setSensorRange(300);
        u.setSpeed(1.2);
        u.setCombatPower(10);
        u.setUnitRadius(2);
        u.setWeapon(CombatSystemFactory.rifle());
        u.setVisible(true);

        return u;
    }

    public Unit createTank(int px, int py, UnitTeam team) throws Exception {
        Unit u = new Unit(px, py, "tank.bmp", dem, wgsToUtm, utmToWgs);

        u.setName("Tank");
        u.setUnitType(UnitType.TANK);
        u.setUnitTeam(team);
        u.setSensorRange(2500);
        u.setSpeed(0.6);
        u.setCombatPower(80);
        u.setUnitRadius(5);
        u.setWeapon(CombatSystemFactory.tankCannon());
        u.setVisible(true);

        return u;
    }
}
