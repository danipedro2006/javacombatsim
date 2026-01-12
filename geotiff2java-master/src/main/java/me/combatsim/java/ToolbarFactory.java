package me.combatsim.java;

import javax.swing.*;

public class ToolbarFactory {

    public static JToolBar createToolbar(CombatSimulator sim) {

        JToolBar bar = new JToolBar();

        JButton losBtn = new JButton("LOS");
        losBtn.addActionListener(e -> sim.toggleLOS());

        JButton opsBtn = new JButton("OPS");
        opsBtn.addActionListener(e -> sim.toggleOperations());

        bar.add(losBtn);
        bar.add(opsBtn);

        return bar;
    }
}
