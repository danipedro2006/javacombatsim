package me.combatsim.java;

import java.awt.image.Raster;
import java.net.URL;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoordinates2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

public class ElevationModel {

    final Raster raster;
    final GridGeometry2D gridGeometry;
    public final MathTransform wgsToDem;

    // Add inverse transform getter (DEM CRS -> WGS84)
    private MathTransform demToWgs;

    /** Load DEM from a resource URL */
    public ElevationModel(URL resourceUrl) throws Exception {
        GeoTiffReader reader = new GeoTiffReader(resourceUrl);
        GridCoverage2D coverage = reader.read(null);

        raster = coverage.getRenderedImage().getData();
        gridGeometry = coverage.getGridGeometry();

        CoordinateReferenceSystem demCRS = coverage.getCoordinateReferenceSystem2D();
        CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326", true);

        wgsToDem = CRS.findMathTransform(wgs84, demCRS, true);
        demToWgs = wgsToDem.inverse(); // precompute inverse
    }

    /** Get elevation in meters from lat/lon */
    public double getElevation(double lat, double lon) {
        try {
            DirectPosition2D src = new DirectPosition2D(lon, lat);
            DirectPosition2D dst = new DirectPosition2D();
            wgsToDem.transform(src, dst);

            GridCoordinates2D grid = gridGeometry.worldToGrid(dst);
            double[] val = new double[1];
            raster.getPixel(grid.x, grid.y, val);

            return val[0];
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    /** Getter for DEM → WGS84 transform (for MapUtils LOS calculations) */
    public MathTransform getDemToWgs() {
        return demToWgs;
    }
}
