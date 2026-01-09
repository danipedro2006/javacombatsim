package me.combatsim.java;

public class CombatSystem {
	private String name;
	private double range;          // in meters
    private double hitProbability; // 0..1
    private double efectiveRadius;
    private double precision;
    private int rounds;
    
    public CombatSystem(String name, double range, double hitProbability, double efectiveRadius) {
		super();
		this.name = name;
		this.range = range;
		this.hitProbability = hitProbability;
		this.efectiveRadius = efectiveRadius;
		this.precision = precision;
		this.rounds = rounds;
	}
	
    
	 
	
    
}
