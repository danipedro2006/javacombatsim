package me.combatsim.java.guieditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import me.combatsim.java.map.MapContext;
import me.combatsim.java.weapons.WeaponLoader;
import me.combatsim.java.guieditor.MapPanel;
import java.awt.*;

public class MapPanelTestApp {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            try {
                MapContext ctx = new MapContext();
                WeaponLoader.loadFromCSV("/me/combatsim/java/weapons.csv");
                JFrame frame = new JFrame("MapPanel Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(frame, new MapPanel());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
