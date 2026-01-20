package me.combatsim.java.overlay;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OverlayEditor extends JFrame {

    private enum Tool { LINE, LINE_ARROW, CIRCLE, RECTANGLE, POLYLINE, POLYGON, CROSSED_CIRCLE }

    private Tool currentTool = Tool.LINE;
    private BufferedImage canvas;
    private Point startPoint;
    private List<Point> tempPoints = new ArrayList<>();
    private File currentFile;

    private final JPanel drawPanel;

    public OverlayEditor(String filename) {
        setTitle("Overlay Editor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ---- Toolbar ----
        JToolBar toolbar = new JToolBar();
        JButton lineBtn = new JButton(new ImageIcon("C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\line.png"));
        JButton circleBtn = new JButton(new ImageIcon("C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\circle.png"));
        JButton rectBtn = new JButton(new ImageIcon("C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\rectangle.png"));
        JButton polylineBtn = new JButton(new ImageIcon("C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\polyline.png"));
        JButton polygonBtn = new JButton(new ImageIcon("C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\polygon.png"));
        JButton saveBtn = new JButton(new ImageIcon("C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\floppy.png"));
        JButton lineArrowBtn = new JButton(new ImageIcon("C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\crossed.png"));
        JButton crossedCircleBtn= new JButton(new ImageIcon("C:\\Users\\danie\\Downloads\\geotiff2java-master\\geotiff2java-master\\src\\main\\java\\me\\combatsim\\java\\crossedcircle.png"));
        
        lineBtn.addActionListener(e -> selectTool(Tool.LINE));
        circleBtn.addActionListener(e -> selectTool(Tool.CIRCLE));
        rectBtn.addActionListener(e -> selectTool(Tool.RECTANGLE));
        polylineBtn.addActionListener(e -> selectTool(Tool.POLYLINE));
        polygonBtn.addActionListener(e -> selectTool(Tool.POLYGON));
        lineArrowBtn.addActionListener(e->selectTool(Tool.LINE_ARROW));
        crossedCircleBtn.addActionListener(e->selectTool(Tool.CROSSED_CIRCLE));
        saveBtn.addActionListener(e -> saveCanvasToFile());

        toolbar.add(saveBtn);
        toolbar.add(lineBtn);
        toolbar.add(circleBtn);
        toolbar.add(rectBtn);
        toolbar.add(polylineBtn);
        toolbar.add(polygonBtn);
        toolbar.add(lineArrowBtn);
        toolbar.add(crossedCircleBtn);

        add(toolbar, BorderLayout.NORTH);

        // ---- Drawing panel ----
        drawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (canvas != null) g.drawImage(canvas, 0, 0, null);

                // Draw preview shapes
                if (startPoint != null && !tempPoints.isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(Color.RED);
                    g2.setStroke(new BasicStroke(3f)); // THICK PREVIEW

                    switch (currentTool) {
                        case LINE:
                            g2.drawLine(startPoint.x, startPoint.y, tempPoints.get(0).x, tempPoints.get(0).y);
                            break;
                        case CIRCLE:
                            int radius = (int) startPoint.distance(tempPoints.get(0));
                            g2.drawOval(startPoint.x - radius, startPoint.y - radius, radius * 2, radius * 2);
                            break;
                        case RECTANGLE:
                            int x = Math.min(startPoint.x, tempPoints.get(0).x);
                            int y = Math.min(startPoint.y, tempPoints.get(0).y);
                            int w = Math.abs(tempPoints.get(0).x - startPoint.x);
                            int h = Math.abs(tempPoints.get(0).y - startPoint.y);
                            g2.drawRect(x, y, w, h);
                            break;
                        case POLYLINE:
                        case POLYGON:
                            Point prev = startPoint;
                            for (Point p : tempPoints) {
                                g2.drawLine(prev.x, prev.y, p.x, p.y);
                                prev = p;
                            }
                            if (currentTool == Tool.POLYGON) g2.drawLine(prev.x, prev.y, startPoint.x, startPoint.y);
                            break;
                        case LINE_ARROW:
                            drawArrow(g2,
                                startPoint.x, startPoint.y,
                                tempPoints.get(0).x, tempPoints.get(0).y
                            );
                            break;
                            
                        case CROSSED_CIRCLE:
                            int r2 = (int) startPoint.distance(tempPoints.get(0));
                            drawCrossedCircle(g2, startPoint.x, startPoint.y, r2);
                            break;

                    }
                }
            }
        };
        drawPanel.setBackground(Color.WHITE);
        add(drawPanel, BorderLayout.CENTER);

        // ---- Mouse listeners ----
        drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                tempPoints.clear();
                tempPoints.add(startPoint);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (canvas == null) return;
                Graphics2D g2 = canvas.createGraphics();
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(3f)); // THICK DRAW
                switch (currentTool) {
                    case LINE:
                        g2.drawLine(startPoint.x, startPoint.y, e.getX(), e.getY());
                        break;
                    case CIRCLE:
                        int radius = (int) startPoint.distance(e.getPoint());
                        g2.drawOval(startPoint.x - radius, startPoint.y - radius, radius * 2, radius * 2);
                        break;
                    case RECTANGLE:
                        int x = Math.min(startPoint.x, e.getX());
                        int y = Math.min(startPoint.y, e.getY());
                        int w = Math.abs(e.getX() - startPoint.x);
                        int h = Math.abs(e.getY() - startPoint.y);
                        g2.drawRect(x, y, w, h);
                        break;
                    case POLYLINE:
                    case POLYGON:
                        tempPoints.add(e.getPoint());
                        Point prev = startPoint;
                        for (Point p : tempPoints) {
                            g2.drawLine(prev.x, prev.y, p.x, p.y);
                            prev = p;
                        }
                        if (currentTool == Tool.POLYGON) g2.drawLine(prev.x, prev.y, startPoint.x, startPoint.y);
                        break;
                    case LINE_ARROW:
                        drawArrow(g2,
                            startPoint.x, startPoint.y,
                            tempPoints.get(0).x, tempPoints.get(0).y
                        );
                        break;
                    case CROSSED_CIRCLE:
                        int r2 = (int) startPoint.distance(tempPoints.get(0));
                        drawCrossedCircle(g2, startPoint.x, startPoint.y, r2);
                        break;

                }
                g2.dispose();
                startPoint = null;
                tempPoints.clear();
                drawPanel.repaint();
            }
        });

        drawPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (startPoint != null) {
                    if (currentTool == Tool.POLYLINE || currentTool == Tool.POLYGON) {
                        tempPoints.add(e.getPoint());
                        startPoint = e.getPoint();
                    } else {
                        tempPoints.set(0, e.getPoint());
                    }
                    drawPanel.repaint();
                }
            }
        });

        // ---- Load canvas AFTER drawPanel exists ----
        currentFile = new File(filename);
        loadCanvasFromFile(filename);

        // ---- Resize frame to image size ----
        if (canvas != null) {
            setSize(canvas.getWidth() + 20, canvas.getHeight() + 80);
        }

        setVisible(true);
    }

    private void selectTool(Tool tool) {
        currentTool = tool;
        System.out.println("[TOOL] Selected: " + tool);
    }

    private void saveCanvasToFile() {
        if (currentFile == null) return;
        try {
            ImageIO.write(canvas, "bmp", currentFile);
            System.out.println("[SAVE] Overlay saved to " + currentFile.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadCanvasFromFile(String filename) {
        try {
            File file = new File(filename);
            if (file.exists()) {
                canvas = ImageIO.read(file);
                if (drawPanel != null) drawPanel.repaint();
                System.out.println("[LOAD] Overlay loaded from " + file.getAbsolutePath());
            } else {
                canvas = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
                System.out.println("[LOAD] New blank canvas");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.drawLine(x1, y1, x2, y2);

        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowSize = 12;

        int xArrow1 = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6));
        int yArrow1 = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6));

        int xArrow2 = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6));
        int yArrow2 = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6));

        g2.drawLine(x2, y2, xArrow1, yArrow1);
        g2.drawLine(x2, y2, xArrow2, yArrow2);
    }
    
    private void drawCrossedCircle(Graphics2D g2, int cx, int cy, int r) {
        // Circle
        g2.drawOval(cx - r, cy - r, r * 2, r * 2);

        // X inside circle (scaled to stay inside)
        int inner = (int) (r * 0.707); // sqrt(2)/2 keeps lines inside
        g2.drawLine(cx - inner, cy - inner, cx + inner, cy + inner);
        g2.drawLine(cx - inner, cy + inner, cx + inner, cy - inner);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OverlayEditor(
                "C:/Users/danie/Downloads/geotiff2java-master/geotiff2java-master/src/main/resources/operations.bmp"));
    }
}
