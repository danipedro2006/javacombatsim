package me.combatsim.java.overlay;
import java.awt.AlphaComposite;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OverlayEditorPanel extends JPanel {

    private final OverlayEditorCore core;

    public OverlayEditorPanel(String bmpFile) throws Exception {
        core = new OverlayEditorCore(bmpFile);

        setOpaque(false); // transparent background

        // Mouse events
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { core.mousePressed(e.getPoint()); repaint(); }
            @Override
            public void mouseReleased(MouseEvent e) { core.mouseReleased(e.getPoint()); repaint(); }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) { core.mouseDragged(e.getPoint()); repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        core.paint(g2);
        g2.dispose();
    }

    public OverlayEditorCore getCore() { return core; }

    /** 
     * Save current drawing to BMP file, overwriting the original file. 
     * Use this in toolbar save button.
     */
    public void saveOverlay() {
        try {
            core.saveCanvasToFile(); // this will overwrite the bmp file
            System.out.println("[OverlayEditor] operations.bmp updated.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Failed to save overlay: " + e.getMessage(),
                "Save Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Set current drawing tool (called from toolbar buttons)
     */
    public void setTool(Tool tool) {
        core.setTool(tool);
    }
}
