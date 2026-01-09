package me.combatsim.java;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

public class MapContext {
	
	public BufferedImage map;
	public static ElevationModel dem;
	public static MathTransform wgsToUtm;
	public static MathTransform utmToWgs;

	public MapContext() throws Exception {
		map = ImageIO.read(getClass().getResource("/me/combatsim/java/Map-army-export.bmp"));

		dem = new ElevationModel(getClass().getResource("/me/combatsim/java//map.tif"));

		// ---- CRS ----
		CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326", true);
		CoordinateReferenceSystem utm = CRS.decode("EPSG:32634", true);

		wgsToUtm = CRS.findMathTransform(wgs84, utm, true);
		utmToWgs = CRS.findMathTransform(utm, wgs84, true);

		// ---- Calibration ----
		MapUtils.initCalibration(203, 139, 46.32, 21.78, 1528, 779, 46.22, 22.08);

	}
}