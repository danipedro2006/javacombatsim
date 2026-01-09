package me.combatsim.java;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.JToolBar;

public class CombatSimulator extends JPanel {

    private final MapContext ctx;
    private final UnitManager unitManager;
    private final OverlayManager overlayManager;
    public JToolBar toolbar;
    private int mouseX = -1, mouseY = -1;
   
    public CombatSimulator() throws Exception {
    	toolbar = new JToolBar("Applications");
        ctx = new MapContext();
        unitManager = UnitBootstrap.create(ctx);
        overlayManager = OverlayBootstrap.create(ctx, unitManager);

        new InputController(this, overlayManager);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(ctx.map, 0, 0, null);

        unitManager.updateRenderPositions();
        unitManager.draw(g);

        overlayManager.drawOverlays((Graphics2D) g);

        if (mouseX >= 0) {
            try {
                var utm = MapUtils.pixelToUTM(mouseX, mouseY, ctx.wgsToUtm);
                double z = MapUtils.getElevationAtPixel(ctx.dem, mouseX, mouseY);

                g.setColor(new Color(0,0,0,170));
                g.fillRect(5,5,420,25);

                g.setColor(Color.WHITE);
                g.drawString(
                    String.format("UTM X %.1f  Y %.1f  Z %.1f m",
                        utm.x, utm.y, z),
                    10,22
                );
            } catch (Exception ignored) {}
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ctx.map.getWidth(), ctx.map.getHeight());
    }

	public static Object toggleLOS() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object toggleOperations() {
		// TODO Auto-generated method stub
		return null;
	}
}
