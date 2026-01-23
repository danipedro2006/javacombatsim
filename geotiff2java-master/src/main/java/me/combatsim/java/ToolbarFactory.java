package me.combatsim.java;

import javax.swing.*;
import me.combatsim.java.overlay.OverlayEditorPanel;
import me.combatsim.java.overlay.Tool;

public class ToolbarFactory {

    /**
     * Creates toolbar for overlay editor tools.
     * @param editorPanel the OverlayEditorPanel instance
     */
    public static JToolBar createOverlayEditorToolbar(final OverlayEditorPanel editorPanel) {
        JToolBar toolbar = new JToolBar();

        if (editorPanel == null) {
            System.err.println("[TOOLBAR] OverlayEditorPanel is null!");
            return toolbar;
        }

        // Shortcut to core
        final var core = editorPanel.getCore();

        // ---- Buttons with hardcoded icons ----
        JButton lineBtn = new JButton(new ImageIcon(
            "C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\line.png"));
        JButton circleBtn = new JButton(new ImageIcon(
            "C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\circle.png"));
        JButton rectBtn = new JButton(new ImageIcon(
            "C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\rectangle.png"));
        JButton polylineBtn = new JButton(new ImageIcon(
            "C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\polyline.png"));
        JButton polygonBtn = new JButton(new ImageIcon(
            "C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\polygon.png"));
        JButton saveBtn = new JButton(new ImageIcon(
            "C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\floppy.png"));
        JButton lineArrowBtn = new JButton(new ImageIcon(
            "C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\crossed.png"));
        JButton crossedCircleBtn = new JButton(new ImageIcon(
            "C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\crossedcircle.png"));

        // ---- Tool selection ----
        lineBtn.addActionListener(e -> core.setTool(Tool.LINE));
        circleBtn.addActionListener(e -> core.setTool(Tool.CIRCLE));
        rectBtn.addActionListener(e -> core.setTool(Tool.RECTANGLE));
        polylineBtn.addActionListener(e -> core.setTool(Tool.POLYLINE));
        polygonBtn.addActionListener(e -> core.setTool(Tool.POLYGON));
        lineArrowBtn.addActionListener(e -> core.setTool(Tool.LINE_ARROW));
        crossedCircleBtn.addActionListener(e -> core.setTool(Tool.CROSSED_CIRCLE));

        // ---- Save button now updates operations.bmp via OverlayEditorPanel ----
        saveBtn.addActionListener(e -> {
            try {
                editorPanel.saveOverlay();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // ---- Add buttons to toolbar ----
        toolbar.add(saveBtn);
        toolbar.add(lineBtn);
        toolbar.add(circleBtn);
        toolbar.add(rectBtn);
        toolbar.add(polylineBtn);
        toolbar.add(polygonBtn);
        toolbar.add(lineArrowBtn);
        toolbar.add(crossedCircleBtn);

        return toolbar;
    }
}
