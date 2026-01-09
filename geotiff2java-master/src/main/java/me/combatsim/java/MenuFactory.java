package me.combatsim.java;

 

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
        JCheckBoxMenuItem los = new JCheckBoxMenuItem("LOS");
        los.addActionListener(e -> app.toggleLOS());

        JCheckBoxMenuItem ops = new JCheckBoxMenuItem("Operations Overlay");
        ops.addActionListener(e -> app.toggleOperations());

        optionsMenu.add(los);
        optionsMenu.add(ops);

        menuBar.add(optionsMenu);
        return menuBar;
    }
}
