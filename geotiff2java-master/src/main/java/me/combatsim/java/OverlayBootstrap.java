package me.combatsim.java;

import java.util.List;

public class OverlayBootstrap {

	public static OverlayManager create(MapContext ctx, UnitManager unitManager) throws Exception {

		OverlayManager om = new OverlayManager();

		om.addOverlay(new BitmapOverlay("/operations.bmp"));
		om.addOverlay(new UnitOverlay(unitManager));
		om.addOverlay(new LOSOverlay(ctx.dem, ctx.wgsToUtm, ctx.map.getWidth(), ctx.map.getHeight()));

		return om;
	}
}
