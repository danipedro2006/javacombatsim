package me.combatsim.java;

import java.util.*;

public class WeaponRepository {

    private static final Map<String, WeaponDefinition> weapons = new HashMap<>();

    public static void register(WeaponDefinition def) {
        weapons.put(def.id, def);
    }

    public static WeaponDefinition get(String id) {
        WeaponDefinition w = weapons.get(id);
        if (w == null)
            throw new IllegalArgumentException("Weapon not found: " + id);
        return w;
    }

    public static Collection<WeaponDefinition> all() {
        return weapons.values();
    }

    public static void printAll() {
        weapons.values().forEach(System.out::println);
    }

	
}
