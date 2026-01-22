package me.combatsim.java.overlay;

import java.util.List;

import me.combatsim.java.UnitManager;
import me.combatsim.java.map.MapContext;

public class Overlays {

	public static OverlayManager create(MapContext ctx, UnitManager unitManager) throws Exception {

		OverlayManager om = new OverlayManager();

		om.addOverlay(new BitmapOverlay("/operations.bmp"));
		om.addOverlay(new UnitOverlay(unitManager));
		om.addOverlay(new LOSOverlay(ctx.dem, ctx.wgsToUtm, ctx.map.getWidth(), ctx.map.getHeight()));
		om.addOverlay(new OverlayEditorOverlay("C:/Users/danie/Downloads/geotiff2java-master/geotiff2java-master/src/main/resources/operations.bmp"));
		return om;
	}
}
