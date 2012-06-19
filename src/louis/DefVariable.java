package louis;

public class DefVariable {
    
    public static final String QLEARNING_DATA_FILE = "count.dat";
    
    public static final int onWinReward = 100;
    public static final int onDeathReward = -100;
    public static final int onHitReward = 1;
    public static final int onHitByBulletReward = -1;
    public static final int onScanRobotReward = 0;
    
    public static final int ACTIONS_UNDER_ONSCANROBOT = 3;
    public static final int ACTIONS_UNDER_ONHITROBOT = 2;
    public static final int ACTIONS_UNDER_ONHITBYBULLET= 2;
    //public static final int ACTIONS_UNDER_ONSCANROBOT = 3;
    
    public static double GAMMA = 0.9;
    public static double ALPHA = 0.5;
    
    public static boolean DEBUG = true;
    
    public static final int NOACTION = -1;
    
    public static final int NUM_ATTR = 4;
    public static final int MAX_ZONE = 9;
    public static final int MAX_ENEMY = 2;
    public static final int MAX_POWER = 4;
    public static final int MAX_PERIOD = 2;
    
    public static final int MAX_TIMER_TICKS = 200;
}
