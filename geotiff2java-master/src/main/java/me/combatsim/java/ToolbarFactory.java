package me.combatsim.java;

 

import javax.swing.*;

public class ToolbarFactory {

    public static JToolBar createToolbar(CombatSimulator app) {

        JToolBar tb = new JToolBar();

        JButton losBtn = new JButton("LOS");
        losBtn.addActionListener(e -> CombatSimulator.toggleLOS());

        JButton opsBtn = new JButton("Ops");
        opsBtn.addActionListener(e -> app.toggleOperations());

        tb.add(losBtn);
        tb.add(opsBtn);

        return tb;
    }
}
