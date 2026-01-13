package me.combatsim.java;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WeaponLoader {

    public static void loadFromCSV(String resourcePath) throws Exception {

        var stream = WeaponLoader.class.getResourceAsStream("/me/combatsim/java/weapons.csv");
        if (stream == null)
            throw new IllegalArgumentException("Weapon CSV not found: " + resourcePath);

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        reader.readLine(); // header
        String line;
        int lineNo = 1;

        while ((line = reader.readLine()) != null) {
            lineNo++;

            if (line.isBlank() || line.startsWith("#"))
                continue;

            String[] p = line.split(",");

            if (p.length < 9) {
                System.err.println(
                    "⚠ Skipping weapon line " + lineNo +
                    " (expected 9 columns, got " + p.length + "): " + line
                );
                continue;
            }

            WeaponDefinition def = new WeaponDefinition(
                p[0].trim(),
                p[1].trim(),
                Double.parseDouble(p[2].trim()),
                Double.parseDouble(p[3].trim()),
                Double.parseDouble(p[4].trim()),
                Double.parseDouble(p[5].trim()),
                Boolean.parseBoolean(p[6].trim()),
                Double.parseDouble(p[7].trim()),
                Double.parseDouble(p[8].trim()),
                Integer.parseInt(p[9].trim())
            );

            WeaponRepository.register(def);
        }

        reader.close();
    }
}
