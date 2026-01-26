package me.combatsim.java;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.opengis.referencing.operation.MathTransform;

import me.combatsim.java.map.ElevationModel;
import me.combatsim.java.map.MapUtils;

public class UnitManager {

	private final List<Unit> units = new ArrayList<>();
	private final ElevationModel dem;
	private final MathTransform utmToWgs;

	public UnitManager(MathTransform utmToWgs, ElevationModel dem) {
		this.utmToWgs = utmToWgs;
		this.dem = dem;
	}

	public void addUnit(Unit u) {
		units.add(u);
	}

	public void removeUnit(Unit u) {
		units.remove(u);
	}

	public List<Unit> getUnits() {
		return Collections.unmodifiableList(units);
	}

	public List<Unit> getFriendlyUnits() {
		List<Unit> result = new ArrayList<>();
		for (Unit u : units) {
			if (u.getUnitTeam() == UnitTeam.FRIENDLY)
				result.add(u);
		}
		return result;
	}

	public List<Unit> getEnemyUnits() {
		List<Unit> result = new ArrayList<>();
		for (Unit u : units) {
			if (u.getUnitTeam() == UnitTeam.ENEMY)
				result.add(u);
		}
		return result;
	}

	/** Update pixel positions for all units, alive or destroyed */
	public void updateRenderPositions() {
		for (Unit u : units) {
			try {
				u.updatePixelPosition(utmToWgs);
			} catch (Exception e) {
				// ignore
			}
		}
	}

	 public void draw(Graphics g) {
	        Graphics2D g2 = (Graphics2D) g;

	        for (Unit u : units) {
	        	drawPlannedMoveArrow(g2, u);
	            BufferedImage img = u.getImage();
	            int x = u.getPixelX() - img.getWidth() / 2;
	            int y = u.getPixelY() - img.getHeight() / 2;

	            if (u.getUnitStatus() != UnitStatus.ALIVE) {
	                // Fade image
	                g2.setComposite(AlphaComposite.getInstance(
	                    AlphaComposite.SRC_OVER, 0.3f));
	            } else {
	                g2.setComposite(AlphaComposite.SrcOver);
	            }
	            System.out.println("[DRAW] " + u.getName() + " status=" + u.getUnitStatus());

	            g2.drawImage(img, x, y, null);

	            // 🔴 IMPORTANT: reset composite BEFORE drawing cross
	            if (u.getUnitStatus() != UnitStatus.ALIVE) {
	                g2.setComposite(AlphaComposite.SrcOver); // <-- THIS LINE
	                g2.setColor(Color.RED);
	                g2.setStroke(new BasicStroke(2f));

	                g2.drawLine(x, y, x + img.getWidth(), y + img.getHeight());
	                g2.drawLine(x, y + img.getHeight(), x + img.getWidth(), y);
	            }
	        }
	        }



	private void drawDestroyedMark(Graphics2D g2, Unit u) {
		
		BufferedImage img = u.getImage();
		int x = u.getPixelX() - img.getWidth() / 2;
		int y = u.getPixelY() - img.getHeight() / 2;
		g2.setColor(Color.RED);
		g2.drawLine(x, y, x + img.getWidth(), y + img.getHeight());
		g2.drawLine(x, y + img.getHeight(), x + img.getWidth(), y);
		AlphaComposite ac = (AlphaComposite) g2.getComposite();
		System.out.println("UNITS alpha=" + ac.getAlpha());
	}

	public Unit getUnitAtPixel(int x, int y) {
		for (int i = units.size() - 1; i >= 0; i--) {
			Unit u = units.get(i);
			BufferedImage img = u.getImage();
			int px = u.getPixelX() - img.getWidth() / 2;
			int py = u.getPixelY() - img.getHeight() / 2;

			if (x >= px && x <= px + img.getWidth() && y >= py && y <= py + img.getHeight()
					&& u.getUnitStatus() == UnitStatus.ALIVE) {
				return u;
			}
		}
		return null;
	}

	public boolean hasLOS(Unit a, Unit b) {
		
		double dx = b.getUtmX() - a.getUtmX();
		double dy = b.getUtmY() - a.getUtmY();

		int steps = 100;
		for (int i = 1; i <= steps; i++) {
			double t = i / 100.0;
			double x = a.getUtmX() + dx * t;
			double y = a.getUtmY() + dy * t;
			double z = a.getUtmZ() + (b.getUtmZ() - a.getUtmZ()) * t;

			double terrainZ = MapUtils.getElevationAtUTM(dem, x, y);
			if (terrainZ > z)
				return false;
		}
		return true;
	}

	private void drawPlannedMoveArrow(Graphics2D g2, Unit u) {
		if (!u.hasPlannedMove())
			return;

		try {
			
			Point2D from = new Point2D.Double(u.getPixelX(), u.getPixelY());
			Point2D to = MapUtils.utmToPixel(u.getPlannedUtmX(), u.getPlannedUtmY(), utmToWgs);

			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(2f));

			// Draw line
			g2.drawLine((int) from.getX(), (int) from.getY(), (int) to.getX(), (int) to.getY());

			// Draw arrowhead
			drawArrowHead(g2, from, to);

		} catch (Exception ignored) {
		}

	}

	private void drawArrowHead(Graphics2D g2, Point2D from, Point2D to) {
		
		double phi = Math.toRadians(20);
		int barb = 10;

		double dx = to.getX() - from.getX();
		double dy = to.getY() - from.getY();
		double theta = Math.atan2(dy, dx);

		double x = to.getX() - barb * Math.cos(theta + phi);
		double y = to.getY() - barb * Math.sin(theta + phi);
		g2.drawLine((int) to.getX(), (int) to.getY(), (int) x, (int) y);

		x = to.getX() - barb * Math.cos(theta - phi);
		y = to.getY() - barb * Math.sin(theta - phi);
		g2.drawLine((int) to.getX(), (int) to.getY(), (int) x, (int) y);
	}

	public List<Unit> getFriendlyUnitsAlive() {
		List<Unit> alive = new ArrayList<>();
		for (Unit u : units) {
			if (u.getUnitTeam() == UnitTeam.FRIENDLY && u.getUnitStatus() == UnitStatus.ALIVE) {
				alive.add(u);
			}
		}
		return alive;
	}

	public List<Unit> getEnemyUnitsAlive() {
		List<Unit> alive = new ArrayList<>();
		for (Unit u : units) {
			if (u.getUnitTeam() == UnitTeam.ENEMY && u.getUnitStatus() == UnitStatus.ALIVE) {
				alive.add(u);
			}
		}
		return alive;
	}

}
