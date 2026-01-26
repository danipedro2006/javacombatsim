package me.combatsim.java;

public class MOECollector {

    // -------------------------------------------------
    // TURN
    // -------------------------------------------------
    private int turn = 0;

    // -------------------------------------------------
    // INITIAL FORCE (FIXED AT START)
    // -------------------------------------------------
    private int initialFriendly = -1;
    private int initialEnemy = -1;

    // -------------------------------------------------
    // CURRENT FORCE
    // -------------------------------------------------
    private int friendlyAlive;
    private int enemyAlive;

    // -------------------------------------------------
    // LOSSES (DERIVED)
    // -------------------------------------------------
    private int friendlyKilled;
    private int enemyKilled;

    // -------------------------------------------------
    // RATIOS
    // -------------------------------------------------
    private double forceRatio;     // Enemy / Friendly (alive)
    private double exchangeRatio;  // Enemy Killed / Friendly Killed

    // -------------------------------------------------
    // INITIALIZATION (CALL ONCE AFTER SCENARIO LOAD)
    // -------------------------------------------------
    public void initialize(UnitManager unitManager) {

        initialFriendly = unitManager.getFriendlyUnits().size();
        initialEnemy = unitManager.getEnemyUnits().size();

        friendlyAlive = initialFriendly;
        enemyAlive = initialEnemy;

        friendlyKilled = 0;
        enemyKilled = 0;

        forceRatio = (friendlyAlive == 0)
                ? Double.POSITIVE_INFINITY
                : (double) enemyAlive / friendlyAlive;

        exchangeRatio = 0.0;

        System.out.println(
                "[MOE] Initialized | Friendly=" + initialFriendly +
                " Enemy=" + initialEnemy
        );
    }

    // -------------------------------------------------
    // COLLECT (CALL ONCE PER TURN)
    // -------------------------------------------------
    public void collect(UnitManager unitManager) {

        if (initialFriendly < 0 || initialEnemy < 0) {
            System.err.println("[MOE] ERROR: initialize() not called");
            return;
        }

        turn++;

        friendlyAlive = unitManager.getFriendlyUnitsAlive().size();
        enemyAlive = unitManager.getEnemyUnitsAlive().size();

        friendlyKilled = initialFriendly - friendlyAlive;
        enemyKilled = initialEnemy - enemyAlive;

        forceRatio = (friendlyAlive == 0)
                ? Double.POSITIVE_INFINITY
                : (double) enemyAlive / friendlyAlive;

        exchangeRatio = (friendlyKilled == 0)
                ? 0.0
                : (double) enemyKilled / friendlyKilled;
    }

    // -------------------------------------------------
    // CONSOLE REPORT
    // -------------------------------------------------
    public void printToConsole() {

        System.out.println("==================================================");
        System.out.println(" MOE REPORT - TURN " + turn);
        System.out.println("==================================================");

        System.out.println("FORCE STATUS:");
        System.out.println("  Friendly Alive : " + friendlyAlive + " / " + initialFriendly);
        System.out.println("  Enemy Alive    : " + enemyAlive + " / " + initialEnemy);

        System.out.println();

        System.out.println("LOSSES:");
        System.out.println("  Friendly Killed: " + friendlyKilled);
        System.out.println("  Enemy Killed   : " + enemyKilled);

        System.out.println();

        System.out.printf("FORCE RATIO    : %.2f (Enemy / Friendly Alive)%n", forceRatio);
        System.out.printf("EXCHANGE RATIO : %.2f (Enemy / Friendly Killed)%n", exchangeRatio);

        System.out.println("==================================================");
    }

    // -------------------------------------------------
    // GETTERS (OPTIONAL)
    // -------------------------------------------------
    public int getTurn() { return turn; }

    public int getFriendlyAlive() { return friendlyAlive; }
    public int getEnemyAlive() { return enemyAlive; }

    public int getFriendlyKilled() { return friendlyKilled; }
    public int getEnemyKilled() { return enemyKilled; }

    public double getForceRatio() { return forceRatio; }
    public double getExchangeRatio() { return exchangeRatio; }
}
