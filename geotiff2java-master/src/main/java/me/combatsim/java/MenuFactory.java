package me.combatsim.java;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

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
        JMenuItem saveStateItem = new JMenuItem("Save state");
        JMenuItem loadStateItem = new JMenuItem("Load state");
        JMenuItem newItem = new JMenuItem("New");

        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(optionsMenu);

        fileMenu.add(newItem);
        fileMenu.add(loadStateItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveStateItem);
        fileMenu.add(exitItem);

        JCheckBoxMenuItem los = new JCheckBoxMenuItem("LOS");
        los.addActionListener(e -> {
            JCheckBoxMenuItem cb = (JCheckBoxMenuItem) e.getSource();
            System.out.println("Check box visible "+cb.isSelected());
            combatSimulator.setLOSVisible(cb.isSelected());
        });

        JCheckBoxMenuItem opsOverlay = new JCheckBoxMenuItem("Operations Overlay");
        opsOverlay.addActionListener(e -> {
            JCheckBoxMenuItem cb = (JCheckBoxMenuItem) e.getSource();
            combatSimulator.setOperationsOverlayVisible(cb.isSelected());
        });

        JCheckBoxMenuItem sensorOverlay = new JCheckBoxMenuItem("Sensors Overlay");
        
            

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
        
        saveStateItem.addActionListener((ActionEvent e) -> {
        	try {
				SimulationStateIO.save("C:/Users/danie/Downloads/geotiff2java-master/geotiff2java-master/src/main/resources/state.csv",combatSimulator.getUnitManager());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });
        loadStateItem.addActionListener((ActionEvent e) -> {
        	try {
				SimulationStateIO.load("C:/Users/danie/Downloads/geotiff2java-master/geotiff2java-master/src/main/resources/state.csv",combatSimulator.getUnitManager());
				combatSimulator.repaint();
        	} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });

        return menuBar;
    }
}
