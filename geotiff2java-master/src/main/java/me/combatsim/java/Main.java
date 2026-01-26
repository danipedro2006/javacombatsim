package me.combatsim.java;

import java.awt.BorderLayout;
import javax.swing.*;
import me.combatsim.java.weapons.WeaponLoader;
import me.combatsim.java.overlay.OverlayEditorPanel;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Combat Simulator");

                // ---- Combat simulator panel ----
                CombatSimulator sim = new CombatSimulator();

                // ---- Overlay editor panel ----
                String bmpFile = "C:/Users/danie/Downloads/geotiff2java-master/geotiff2java-master/src/main/resources/operations.bmp";
                OverlayEditorPanel editorPanel = new OverlayEditorPanel(bmpFile);
                editorPanel.setOpaque(false);

                // ---- Toolbar for overlay editor ----
                JToolBar overlayToolbar = ToolbarFactory.createOverlayEditorToolbar(editorPanel);

                // ---- Layered pane for simulator + overlay editor ----
                JLayeredPane layeredPane = new JLayeredPane();
                layeredPane.setPreferredSize(sim.getPreferredSize());

                // Simulator panel at bottom
                sim.setBounds(0, 0, sim.getPreferredSize().width, sim.getPreferredSize().height);
                layeredPane.add(sim, Integer.valueOf(0));

                // Overlay editor above simulator
                editorPanel.setBounds(0, 0, sim.getPreferredSize().width, sim.getPreferredSize().height);
                layeredPane.add(editorPanel, Integer.valueOf(1));

                // ---- Add components to frame ----
                frame.setLayout(new BorderLayout());
                frame.add(overlayToolbar, BorderLayout.NORTH); // toolbar on top
                frame.add(layeredPane, BorderLayout.CENTER);   // simulator + overlay stacked

                // ---- Load weapons & scenario ----
                WeaponLoader.loadFromCSV("/me/combatsim/java/weapons.csv");
                sim.getUnitBootstrap().loadFromCSV("/me/combatsim/java/scenario.csv");
                sim.moeCollector.initialize(sim.getUnitManager());
                // ---- Menu ----
                frame.setJMenuBar(MenuFactory.createMenuBar(sim, editorPanel));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
}
