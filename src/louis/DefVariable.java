package louis;

public class DefVariable {
    public static final int onWinReward = 100;
    public static final int onDeathReward = -100;
    public static final int onHitReward = 1;
    public static final int onHitByBulletReward = -1;
    public static final int onScanRobotReward = 0;
    
    public static final int EVENTCOUNT = 5; // record the number of events... e.g onscanrobot , onhitrobot .... etc
    
    public static final int ACTIONS_UNDER_ONSCANROBOT = 3;
    public static final int ACTIONS_UNDER_ONHITROBOT = 2;
    public static final int ACTIONS_UNDER_ONHITBYBULLET= 2;
    //public static final int ACTIONS_UNDER_ONSCANROBOT = 3;
    
    public static double GAMMA = 0.9;
    public static double ALPHA = 0.5;
    
    
    public static boolean DEBUG = true;
    
    
    public static final int NOACTION = -1;
    
    public static final int STATE_START = 0;
    public static final int STATE_ONSCAN1 = 1;
    public static final int STATE_ONHIT = 2;
    public static final int STATE_ONHITBYBULLET = 3;
    public static final int STATE_END = 4;
}
