package me.combatsim.java;

import org.opengis.referencing.operation.MathTransform;

/**
 * Factory responsible ONLY for constructing Unit objects.
 * All unit parameters are supplied externally (CSV / scenario).
 */
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

    /**
     * Generic unit creator.
     * All properties are provided by scenario / bootstrap.
     */
    public Unit createUnit(
            String name,
            UnitType unitType,
            UnitTeam unitTeam,
            int pixelX,
            int pixelY,
            boolean visible,
            double sensorRange,
            double combatPower,
            double speed,
            WeaponDefinition weapon,
            double unitRadius,
            String symbol
    ) throws Exception {

        return new Unit(
                name,
                unitType,
                unitTeam,
                pixelX,
                pixelY,
                visible,
                sensorRange,
                combatPower,
                speed,
                weapon,
                unitRadius,
                symbol,
                dem,
                wgsToUtm,
                utmToWgs
        );
    }
}
