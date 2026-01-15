package me.combatsim.java;

 

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import me.combatsim.java.weapons.WeaponLoader;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Combat Simulator");
               
                CombatSimulator sim = new CombatSimulator();

                JToolBar toolbar = ToolbarFactory.createToolbar(sim);

                frame.add(toolbar, BorderLayout.NORTH);
                frame.add(sim, BorderLayout.CENTER);

                 

                // 🔥 TEST UNIT BOOTSTRAP HERE
                WeaponLoader.loadFromCSV("/me/combatsim/java/weapons.csv");
				sim.getUnitBootstrap().loadFromCSV( "/me/combatsim/java/scenario.csv" );
				 
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setJMenuBar(MenuFactory.createMenuBar(sim));
                frame.add(ToolbarFactory.createToolbar(sim), BorderLayout.NORTH);
                frame.add(sim, BorderLayout.CENTER);
                frame.setContentPane(sim);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
 