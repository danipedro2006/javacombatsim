package me.combatsim.java;

import javax.swing.*;

public class ToolbarFactory {

    public static JToolBar createToolbar(CombatSimulator sim) {

        JToolBar bar = new JToolBar();
        JButton btnLine = new JButton(new ImageIcon("C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\line.png"));
        bar.add(btnLine);
        
        JButton btnCircle = new JButton(new ImageIcon("C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\circle.png"));
        bar.add(btnCircle);
        
        JButton btnPolygon = new JButton(new ImageIcon("C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\polygon.png"));
        bar.add(btnPolygon);
        JButton btnPolyLine = new JButton(new ImageIcon("C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\polyline.png"));
        bar.add(btnPolyLine);
        JButton losBtn = new JButton("LOS");
        losBtn.addActionListener(e -> sim.toggleLOS());

        JButton opsBtn = new JButton("OPS");
        opsBtn.addActionListener(e -> sim.toggleOperations());

        bar.add(losBtn);
        bar.add(opsBtn);
        bar.add(btnLine);
        return bar;
    }
}
