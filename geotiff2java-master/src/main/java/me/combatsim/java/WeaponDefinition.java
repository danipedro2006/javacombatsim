package me.combatsim.java;

 

public class WeaponDefinition {

    public final String id;            // "JAVELIN"
    public final String category;      // "ATGM"
    public final double maxRange;      // meters
    public final double minRange;
    public final double killProbability;
    public final double flightSpeed;   // m/s
    public final boolean requiresLOS;
    public final double effectRadius;
    public final double precision;
	public int rounds;

    public WeaponDefinition(
            String id,
            String category,
            double minRange,
            double maxRange,
            double killProbability,
            double flightSpeed,
            boolean requiresLOS,
            double effectRadius,
            double precision,
            int rounds
            
    ) {
        this.id = id;
        this.category = category;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.killProbability = killProbability;
        this.flightSpeed = flightSpeed;
        this.requiresLOS = requiresLOS;
        this.effectRadius=effectRadius;
        this.precision=precision;
        this.rounds=rounds;
    }

	public int getRounds() {
		// TODO Auto-generated method stub
		return rounds;
	}

	public double getMinRange() {
		// TODO Auto-generated method stub
		return minRange;
	}

	public double getMaxRange() {
		// TODO Auto-generated method stub
		return maxRange;
	}

	 

	public boolean requiresLOS() {
		// TODO Auto-generated method stub
		return false;
	}

	public double computeKillProbability(double distance) {
		// TODO Auto-generated method stub
		return killProbability;
	}

	public void consumeRound() {
		rounds--;		
	}
	
	public String getId() {
		return id;
	}

	public double getKillProbability() {
		// TODO Auto-generated method stub
		return killProbability;
	}
}
