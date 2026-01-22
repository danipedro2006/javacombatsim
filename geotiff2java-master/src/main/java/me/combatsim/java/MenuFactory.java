package me.combatsim.java;

import java.awt.event.ActionEvent;
import javax.swing.*;

import me.combatsim.java.overlay.OverlayEditorPanel;

public class MenuFactory {

    public static JMenuBar createMenuBar(
            CombatSimulator combatSimulator,
            OverlayEditorPanel editorPanel // <-- pass the editor panel here
    ) {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu toolsMenu = new JMenu("Tools");
        JMenu optionsMenu = new JMenu("Options");

        JMenuItem exitItem = new JMenuItem("Exit");
        JMenuItem saveItem = new JMenuItem("Save scenario");
        JMenuItem loadItem = new JMenuItem("Load scenario");
        JMenuItem newItem = new JMenuItem("New");

        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(optionsMenu);

        fileMenu.add(newItem);
        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);

        JCheckBoxMenuItem los = new JCheckBoxMenuItem("LOS");
        los.addActionListener(e -> combatSimulator.toggleLOS());

        JCheckBoxMenuItem opsOverlay = new JCheckBoxMenuItem("Operations Overlay");
        opsOverlay.addActionListener(e -> combatSimulator.toggleOperations());

        JCheckBoxMenuItem sensorOverlay = new JCheckBoxMenuItem("Sensors Overlay");
        sensorOverlay.addActionListener(e -> combatSimulator.toggleSensor());

        // ---- NEW: Overlay Editor toggle ----
        JCheckBoxMenuItem overlayEditorToggle = new JCheckBoxMenuItem("Overlay Editor");
        overlayEditorToggle.addActionListener((ActionEvent e) -> {
            boolean visible = overlayEditorToggle.isSelected();
            editorPanel.setVisible(visible); // toggle overlay editor visibility
            editorPanel.repaint();
        });

        optionsMenu.add(los);
        optionsMenu.add(opsOverlay);
        optionsMenu.add(sensorOverlay);
        optionsMenu.addSeparator();
        optionsMenu.add(overlayEditorToggle);

        // ---- Battle controls ----
        JMenuItem startBattle = new JMenuItem("Start Battle");
        JMenuItem stopBattle = new JMenuItem("Stop Battle");
        JMenuItem stepBattle = new JMenuItem("Step Battle");

        toolsMenu.add(startBattle);
        toolsMenu.add(stopBattle);
        toolsMenu.addSeparator();
        toolsMenu.add(stepBattle);

        startBattle.addActionListener((ActionEvent e) -> {
            combatSimulator.setSimMode(SimMode.BATTLE);
            combatSimulator.startBattle();
        });

        stopBattle.addActionListener((ActionEvent e) -> {
            combatSimulator.stopBattle();
            combatSimulator.setSimMode(SimMode.EDIT);
        });

        stepBattle.addActionListener((ActionEvent e) -> {
            combatSimulator.getDetectionManager().update(
                    combatSimulator.getUnitManager().getFriendlyUnits(),
                    combatSimulator.getUnitManager().getEnemyUnits()
            );
            combatSimulator.getBattleManager().runTurn();
            combatSimulator.repaint();
        });

        return menuBar;
    }
}
