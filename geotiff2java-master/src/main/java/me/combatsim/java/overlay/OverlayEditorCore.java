package me.combatsim.java.overlay;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class OverlayEditorCore {

     
	private BufferedImage canvas;
    private Point startPoint;
    private final List<Point> tempPoints = new ArrayList<>();
    private final String file;
    private File currentFile;
    
    private Tool currentTool = Tool.LINE;

    public OverlayEditorCore(String file) throws Exception {
        this.file = file;
        File f = new File(file);
        if (f.exists()) {
            canvas = ImageIO.read(f);
        } else {
            canvas = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        }
        // Make currentFile point to the same path as the file you loaded
        currentFile = f;
    }

    

	public void setTool(Tool t) {
		 
		    System.out.println("[EDITOR] Tool set to " + t);
		    currentTool = t;
		}
 
    

    public void paint(Graphics2D g) {
    	if (canvas == null) {
    	    System.out.println("[EDITOR] canvas is NULL");
    	}
    	g.setColor(Color.RED);
    	g.drawString("EDITOR ACTIVE", 50, 50);

        g.drawImage(canvas, 0, 0, null);
        if (startPoint != null && !tempPoints.isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3f));

            Point p = tempPoints.get(0);
            switch (currentTool) {
                case LINE:
                    g2.drawLine(startPoint.x, startPoint.y, p.x, p.y);
                    break;
                case CIRCLE:
                    int rCircle = (int) startPoint.distance(p);
                    g2.drawOval(startPoint.x - rCircle, startPoint.y - rCircle, rCircle * 2, rCircle * 2);
                    break;
                case RECTANGLE:
                    int xRect = Math.min(startPoint.x, p.x);
                    int yRect = Math.min(startPoint.y, p.y);
                    g2.drawRect(xRect, yRect, Math.abs(p.x - startPoint.x), Math.abs(p.y - startPoint.y));
                    break;
                case LINE_ARROW:
                    drawArrow(g2, startPoint, p);
                    break;
                case CROSSED_CIRCLE:
                    int rCrossed = (int) startPoint.distance(p);
                    drawCrossedCircle(g2, startPoint.x, startPoint.y, rCrossed);
                    break;
                case POLYLINE:
                case POLYGON:
                    Point prev = startPoint;
                    for (Point pt : tempPoints) {
                        g2.drawLine(prev.x, prev.y, pt.x, pt.y);
                        prev = pt;
                    }
                    if (currentTool == Tool.POLYGON) {
                        g2.drawLine(prev.x, prev.y, startPoint.x, startPoint.y);
                    }
                    break;
            }

            g2.dispose();
        }
    }

    // ---- Mouse events ----
    public void mousePressed(Point p) {
        startPoint = p;
        tempPoints.clear();
        tempPoints.add(p);
    }

    public void mouseDragged(Point p) {
        if (startPoint != null) {
            tempPoints.set(0, p);
        }
    }

    public void mouseReleased(Point p) {
        Graphics2D g2 = canvas.createGraphics();
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(3f));

        switch (currentTool) {
            case LINE:
                g2.drawLine(startPoint.x, startPoint.y, p.x, p.y);
                break;
            case CIRCLE:
                int rCircle = (int) startPoint.distance(p);
                g2.drawOval(startPoint.x - rCircle, startPoint.y - rCircle, rCircle * 2, rCircle * 2);
                break;
            case RECTANGLE:
                int xRect = Math.min(startPoint.x, p.x);
                int yRect = Math.min(startPoint.y, p.y);
                g2.drawRect(xRect, yRect, Math.abs(p.x - startPoint.x), Math.abs(p.y - startPoint.y));
                break;
            case LINE_ARROW:
                drawArrow(g2, startPoint, p);
                break;
            case CROSSED_CIRCLE:
                int rCrossed = (int) startPoint.distance(p);
                drawCrossedCircle(g2, startPoint.x, startPoint.y, rCrossed);
                break;
            case POLYLINE:
            case POLYGON:
                Point prev = startPoint;
                for (Point pt : tempPoints) {
                    g2.drawLine(prev.x, prev.y, pt.x, pt.y);
                    prev = pt;
                }
                if (currentTool == Tool.POLYGON) {
                    g2.drawLine(prev.x, prev.y, startPoint.x, startPoint.y);
                }
                break;
        }

        g2.dispose();
        startPoint = null;
        tempPoints.clear();
    }

    // ---- Helpers ----
    private void drawArrow(Graphics2D g2, Point from, Point to) {
        g2.drawLine(from.x, from.y, to.x, to.y);
        double angle = Math.atan2(to.y - from.y, to.x - from.x);
        int size = 12;
        int x1 = (int) (to.x - size * Math.cos(angle - Math.PI / 6));
        int y1 = (int) (to.y - size * Math.sin(angle - Math.PI / 6));
        int x2 = (int) (to.x - size * Math.cos(angle + Math.PI / 6));
        int y2 = (int) (to.y - size * Math.sin(angle + Math.PI / 6));
        g2.drawLine(to.x, to.y, x1, y1);
        g2.drawLine(to.x, to.y, x2, y2);
    }

    private void drawCrossedCircle(Graphics2D g2, int cx, int cy, int r) {
        g2.drawOval(cx - r, cy - r, r * 2, r * 2);
        int inner = (int) (r * 0.707);
        g2.drawLine(cx - inner, cy - inner, cx + inner, cy + inner);
        g2.drawLine(cx - inner, cy + inner, cx + inner, cy - inner);
    }

    void saveCanvasToFile() {
        if (currentFile == null) return;
        try {
            ImageIO.write(canvas, "bmp", currentFile);
            System.out.println("[SAVE] Overlay saved to " + currentFile.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
