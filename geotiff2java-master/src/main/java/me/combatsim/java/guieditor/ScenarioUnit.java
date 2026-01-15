package me.combatsim.java.guieditor;

import me.combatsim.java.UnitTeam;
import me.combatsim.java.UnitType;
import me.combatsim.java.weapons.WeaponDefinition;

public class ScenarioUnit {

    public UnitType unitType;
    public UnitTeam unitTeam;

    public int pixelX;
    public int pixelY;

    public boolean isVisible;

    public double sensorRange;
    public double combatPower;
    public double speed;

    public WeaponDefinition weapon;

    public double unitRadius;
    public String mapSymbol;

    public boolean isValid() {
        return unitType != null
            && unitTeam != null
            && weapon != null
            && mapSymbol != null
            && unitRadius > 0
            && speed >= 0
            && combatPower > 0;
    }
}
