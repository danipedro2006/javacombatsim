package me.combatsim.java;

import java.awt.geom.Point2D;
import org.geotools.geometry.DirectPosition2D;
import org.opengis.referencing.operation.MathTransform;

public final class MapUtils {

    private MapUtils() {}

    public static int x1, y1, x2, y2;
    public static double lat1, lon1, lat2, lon2;
    public static double scaleX, scaleY;

    public static void initCalibration(
            int px1, int py1, double la1, double lo1,
            int px2, int py2, double la2, double lo2) {

        x1 = px1; y1 = py1;
        x2 = px2; y2 = py2;
        lat1 = la1; lon1 = lo1;
        lat2 = la2; lon2 = lo2;

        scaleX = (lon2 - lon1) / (x2 - x1);
        scaleY = (lat2 - lat1) / (y2 - y1);
    }

    public static DirectPosition2D pixelToWgs84(int x, int y) {
        double lon = lon1 + (x - x1) * scaleX;
        double lat = lat1 + (y - y1) * scaleY;
        return new DirectPosition2D(lon, lat);
    }

    public static Point2D wgs84ToPixel(double lat, double lon) {
        double x = x1 + (lon - lon1) / scaleX;
        double y = y1 + (lat - lat1) / scaleY;
        return new Point2D.Double(x, y);
    }

    public static DirectPosition2D pixelToUTM(
            int x, int y,
            MathTransform wgsToUtm) throws Exception {

        DirectPosition2D wgs = pixelToWgs84(x, y);
        DirectPosition2D utm = new DirectPosition2D();
        wgsToUtm.transform(wgs, utm);
        return utm;
    }

    public static double getElevationAtPixel(ElevationModel dem, int x, int y) {
        if (dem == null) return Double.NaN;
        DirectPosition2D wgs = pixelToWgs84(x, y);
        return dem.getElevation(wgs.y, wgs.x);
    }
    /** 
     * Return elevation at a UTM coordinate by converting to lat/lon → pixel → DEM
     */
    public static double getElevationAtUTM(ElevationModel dem, double utmX, double utmY) {
        try {
            // Convert UTM → WGS84
            DirectPosition2D wgs = new DirectPosition2D();
            dem.getDemToWgs().transform(new DirectPosition2D(utmX, utmY), wgs);


            // Convert WGS → pixel
            java.awt.geom.Point2D pixel = wgs84ToPixel(wgs.getY(), wgs.getX());

            int px = (int) pixel.getX();
            int py = (int) pixel.getY();

            // Get DEM elevation
            return getElevationAtPixel(dem, px, py);
        } catch (Exception e) {
            return Double.NaN;
        }
    }
    /**
     * Convert UTM coordinates to pixel coordinates
     * @param utmX X coordinate in UTM (meters)
     * @param utmY Y coordinate in UTM (meters)
     * @param utmToWgs transform from UTM -> WGS84
     * @return pixel coordinates on the map
     */
    public static Point2D.Double utmToPixel(double utmX, double utmY, MathTransform utmToWgs) throws Exception {
        // UTM -> WGS84
        DirectPosition2D wgs = new DirectPosition2D();
        utmToWgs.transform(new DirectPosition2D(utmX, utmY), wgs); // wgs.x=lon, wgs.y=lat

        // WGS84 -> Pixel
        Point2D px = wgs84ToPixel(wgs.y, wgs.x); // returns Point2D
        return new Point2D.Double(px.getX(), px.getY()); // wrap as Point2D.Double
    }

}
