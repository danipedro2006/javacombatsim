package me.combatsim.java;

 

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Combat Simulator");
               
                CombatSimulator app = new CombatSimulator();

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setJMenuBar(MenuFactory.createMenuBar(app));
                frame.add(ToolbarFactory.createToolbar(app), BorderLayout.NORTH);
                frame.add(app, BorderLayout.CENTER);
                frame.setContentPane(app);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
