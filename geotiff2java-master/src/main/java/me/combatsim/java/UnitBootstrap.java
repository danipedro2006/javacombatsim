package me.combatsim.java;

public class UnitBootstrap {
 
		public UnitManager unitManager;
		 static UnitFactory factory = new UnitFactory(MapContext.dem, MapContext.wgsToUtm, MapContext.utmToWgs);
		// ---- Create units ----
		static UnitManager create(MapContext ctx) throws Exception {
			       
					UnitManager unitManager = new UnitManager(ctx.utmToWgs, ctx.dem);
					//Unit infantry = new Unit(800, 400, "infantry.bmp", ctx.dem, ctx.wgsToUtm, ctx.utmToWgs);
					//Unit tank = new Unit(900, 700, "tank.bmp", ctx.dem, ctx.wgsToUtm, ctx.utmToWgs);
					Unit infantry1 = factory.createInfantry(800, 400, UnitTeam.FRIENDLY);
					Unit infantry2 = factory.createInfantry(800, 600, UnitTeam.FRIENDLY);
					Unit tank = factory.createTank(900, 600, UnitTeam.ENEMY);
					unitManager.addUnit(infantry1);
					unitManager.addUnit(infantry2);
					unitManager.addUnit(tank);
					
					return unitManager;
		}
				}

