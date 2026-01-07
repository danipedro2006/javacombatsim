package me.callsen.taylor.geotiff2java;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.geotools.geometry.DirectPosition2D;
import org.opengis.referencing.operation.MathTransform;

public class Unit {

    // ---- TRUE POSITION (UTM) ----
    private double utmX;
    private double utmY;
    private double utmZ;

    // ---- CACHED RENDER POSITION ----
    private int pixelX;
    private int pixelY;

    private final BufferedImage image;

    public Unit(
            int startPixelX,
            int startPixelY,
            String imageName,                 // ← just "infantry.bmp"
            ElevationModel dem,
            MathTransform wgsToUtm,
            MathTransform utmToWgs) throws Exception {

        // Pixel → UTM
        DirectPosition2D utm =
                MapUtils.pixelToUTM(startPixelX, startPixelY, wgsToUtm);

        this.utmX = utm.x;
        this.utmY = utm.y;
        this.utmZ = MapUtils.getElevationAtPixel(dem, startPixelX, startPixelY);

        // ---- LOAD IMAGE FROM RESOURCES ----
        this.image = ImageIO.read(
            Unit.class.getResource("/" + imageName)
        );

        if (this.image == null) {
            throw new IllegalArgumentException(
                "Unit image not found in resources: " + imageName
            );
        }

        // Initial render position
        updatePixelPosition(utmToWgs);
    }

    /** Recalculate pixel coordinates from UTM */
    public void updatePixelPosition(MathTransform utmToWgs) throws Exception {
        DirectPosition2D wgs = new DirectPosition2D();
        utmToWgs.transform(new DirectPosition2D(utmX, utmY), wgs);

        Point2D px = MapUtils.wgs84ToPixel(wgs.y, wgs.x);
        this.pixelX = (int) px.getX();
        this.pixelY = (int) px.getY();
    }

    // ---- Movement in UTM space ----
    public void move(double dxMeters, double dyMeters) {
        utmX += dxMeters;
        utmY += dyMeters;
    }
    
    public double distance3dTo(Unit other) {
        double dx = other.utmX - this.utmX;
        double dy = other.utmY - this.utmY;
        double dz = other.utmZ - this.utmZ; // optional for 3D distance
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    public double distance2dTo(Unit other) {
        double dx = other.utmX - this.utmX;
        double dy = other.utmY - this.utmY;
        return Math.sqrt(dx*dx + dy*dy);
    }

    // ---- Getters ----
    public int getPixelX() { return pixelX; }
    public int getPixelY() { return pixelY; }
    public double getUtmX() { return utmX; }
    public double getUtmY() { return utmY; }
    public double getUtmZ() { return utmZ; }
    public BufferedImage getImage() { return image; }
}
