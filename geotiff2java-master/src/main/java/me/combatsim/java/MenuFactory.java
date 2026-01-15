package me.combatsim.java;

 

import java.awt.event.ActionEvent;

import javax.swing.*;

public class MenuFactory {

    public static JMenuBar createMenuBar(CombatSimulator app) {

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu toolsMenu=new JMenu("Tools");
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
        
        JMenuItem testItem=new JMenuItem("Test");
		toolsMenu.add(testItem);
		
        JCheckBoxMenuItem los = new JCheckBoxMenuItem("LOS");
         
        JMenuItem startBattle = new JMenuItem("Start Battle");
        JMenuItem stopBattle = new JMenuItem("Stop Battle");
        JMenuItem stepBattle = new JMenuItem("Step Battle");
        
        toolsMenu.add(startBattle);
        toolsMenu.add(stopBattle);
        toolsMenu.addSeparator();
        toolsMenu.add(stepBattle);
        
        startBattle.addActionListener((ActionEvent e) -> {
            app.startBattle();
        });

        stopBattle.addActionListener((ActionEvent e) -> {
            app.stopBattle();
        });

        stepBattle.addActionListener((ActionEvent e) -> {
            app.getDetectionManager().update(
                app.getUnitManager().getFriendlyUnits(),
                app.getUnitManager().getEnemyUnits()
            );
            app.getBattleManager().runTurn();
            app.repaint();
        });


        

        menuBar.add(toolsMenu);
        los.addActionListener(e -> app.toggleLOS());

        JCheckBoxMenuItem ops = new JCheckBoxMenuItem("Operations Overlay");
        ops.addActionListener(e -> app.toggleOperations());
        JCheckBoxMenuItem sensor = new JCheckBoxMenuItem("Sensors Overlay");
        sensor.addActionListener(e -> app.toggleSensor());
        optionsMenu.add(los);
        optionsMenu.add(ops);
        optionsMenu.add(sensor);

        menuBar.add(optionsMenu);
        return menuBar;
    }
}
