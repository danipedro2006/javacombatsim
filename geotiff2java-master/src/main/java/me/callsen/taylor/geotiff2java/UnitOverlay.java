package me.callsen.taylor.geotiff2java;

import java.awt.Graphics2D;

public class UnitOverlay implements Overlay {

    private final UnitManager unitManager;
     
    private boolean visible = true; // units usually always visible
    public UnitOverlay(UnitManager unitManager) {
        this.unitManager = unitManager;
    }

    @Override
    public void draw(Graphics2D g) {
        for (Unit u : unitManager.getUnits()) {
            int px = u.getPixelX() - u.getImage().getWidth()/2;
            int py = u.getPixelY() - u.getImage().getHeight()/2;
            g.drawImage(u.getImage(), px, py, null);
        }
    }

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return visible;
	}
	public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
