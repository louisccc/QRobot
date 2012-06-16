package louis;

import robocode.AdvancedRobot;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobocodeFileOutputStream;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;
import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;


/**
 * TrackFire - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
 * <p/>
 * Sits still.  Tracks and fires at the nearest robot it sees
 */
public class QRobot extends AdvancedRobot {

    /**
     * TrackFire's run method
     */
    
    //private HashMap<Integer, ArrayList<Double>> mQValueTable;
    private static final String QLearningDataFile = "count.dat";
    private ArrayList<String> mRawData = new ArrayList<String>();

    double gunTurnAmt; // How much to turn our gun when searching
    private String trackName = null;
    
    double bulletPower = 1;  /*Narchi*/
    boolean hitEnemy; /*Narchi*/
    
    
    private RobotState mCurrentState;
    int dist = 50; // distance to move when we're hit
    private RobotState mPreviousState;
    private int mPreviousAction = -1;
    
    private ScannedRobotEvent lastseen = null;
    
    public int decideStratgyFromEnvironmentState(RobotState state, int num_actions){
        
        ArrayList<Double> actionsQs = new ArrayList<Double>();
        double sum = 0;
        for(int i = 0; i < num_actions; i++){
            double qvalue = Tool.getQValueByStateAndAction(mRawData, state, num_actions);
            if(qvalue < 0){
                qvalue = 1;
            }
            else{
                qvalue = qvalue + 1;
            }
            sum += qvalue;
            actionsQs.add(sum);
        }
        double random_action = Math.random()*(sum);
        int action = -1;
        for(Double value : actionsQs){
            if(random_action <= value){
                action = actionsQs.indexOf(value);
                break;
            }
            else 
                continue;
        }
        //if(DefVariable.DEBUG){
            //System.out.println("value = " + random_action + " so " + "suggest action " + action);
        //}
        return action;
    }

    public void readTable() throws Exception{

        try {
            BufferedReader r = new BufferedReader(new FileReader(getDataFile("count.dat")));
            String data_row;
            while( (data_row = r.readLine()) !=  null){
                String[] splited = data_row.split(" ");
                if(splited.length == 3){
                    mRawData.add(data_row);
                }
            }
            r.close();
        } catch (IOException e) {
            initRawData(mRawData); // forced to initial raw data here
            e.printStackTrace();
        } catch (NumberFormatException e) {
            initRawData(mRawData); // forced to initial raw data here
            e.printStackTrace();
        }
    }
    
    public void initRawData(ArrayList<String> rawdata){
        //int num_allState = DefVariable.EVENTCOUNT;
        
        rawdata.add(DefVariable.STATE_START + " 0 0\n");
        
        // init on scan robot event 
        for(int i = 0 ; i < DefVariable.ACTIONS_UNDER_ONSCANROBOT; i++){
            rawdata.add(DefVariable.STATE_ONSCAN1 + " " + i + " " + "0\n");
        }
        
        for(int i = 0; i < DefVariable.ACTIONS_UNDER_ONHITROBOT; i++){
            rawdata.add(DefVariable.STATE_ONHIT + " " + i + " " + "0\n");
        }
        
        for(int i = 0; i < DefVariable.ACTIONS_UNDER_ONHITBYBULLET; i++){
            rawdata.add(DefVariable.STATE_ONHITBYBULLET + " " + i + " " + "0\n");
        }
        
        rawdata.add(DefVariable.STATE_END + " 0 0\n");
    }
    
    public void printRawTable() {
        for(int i = 0; i < mRawData.size(); i++){
            System.out.println(mRawData.get(i));
        }
    }
    public void run() {
        // Set colors
        setBodyColor(Color.pink);
        setGunColor(Color.pink);
        setRadarColor(Color.pink);
        setScanColor(Color.pink);
        setBulletColor(Color.pink);

        try {
            readTable();
            //printRawTable();
            mCurrentState = new RobotState();
            mPreviousState = new RobotState();
            mPreviousAction = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Loop forever
        while (true) {
            turnGunRight(10); // Scans automatically
        }
    }

    /**
     * onScannedRobot:  We have a target.  Go get it.
     */
    public void onScannedRobot(ScannedRobotEvent e) {

        mCurrentState = getStateByCurrentEnvironment(DefVariable.STATE_ONSCAN1);
        
        lastseen = e;
        
        //mCurrentState = DefVariable.STATE_ONSCAN1;
        String maxQvalueUnderCurrentStateRow = Tool.getMaxQValueUnderState(mRawData, mCurrentState);
        double maxQvalue = Double.parseDouble(maxQvalueUnderCurrentStateRow.split(" ")[2]);
        if(mPreviousAction != DefVariable.NOACTION){
            executeQLearningFunction(mRawData, DefVariable.onScanRobotReward, maxQvalue, mPreviousState, mPreviousAction);
        }
		
        int action = decideStratgyFromEnvironmentState(mCurrentState, DefVariable.ACTIONS_UNDER_ONSCANROBOT);
        
        mPreviousState = mCurrentState;
        mPreviousAction = action;
        switch (action) {
        case 0:
            double absoluteBearing = getHeading() + e.getBearing();
            double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());

            // If it's close enough, fire!
            if (Math.abs(bearingFromGun) <= 3) {
                turnGunRight(bearingFromGun);
                // We check gun heat here, because calling fire()
                // uses a turn, which could cause us to lose track
                // of the other robot.
                if (getGunHeat() == 0) {
                    fire(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));
                }
            } // otherwise just set the gun to turn.
            // Note:  This will have no effect until we call scan()
            else {
                turnGunRight(bearingFromGun);
            }
            // Generates another scan event if we see a robot.
            // We only need to call this if the gun (and therefore radar)
            // are not turning.  Otherwise, scan is called automatically.
            if (bearingFromGun == 0) {
                scan();
            }
            break;
        case 1: 
            // crazy's robot
            fire(1);
            break;
        case 2: 
            // fire's robot
            if (e.getDistance() < 50 && getEnergy() > 50) {
                fire(3);
            } // otherwise, fire 1.
            else {
                fire(1);
            }
            // Call scan again, before we turn the gun
            scan();
        default:
            break;
        }
        
    }

    public void onHitRobot(HitRobotEvent e){
        
        mCurrentState = getStateByCurrentEnvironment(DefVariable.STATE_ONHIT);
        
        String maxQvalueUnderCurrentStateRow = Tool.getMaxQValueUnderState(mRawData, mCurrentState);
        double maxQvalue = Double.parseDouble(maxQvalueUnderCurrentStateRow.split(" ")[2]);
        
        if(mPreviousAction != DefVariable.NOACTION){
            executeQLearningFunction(mRawData, DefVariable.onHitReward, maxQvalue, mPreviousState, mPreviousAction);
        }
        //printRawTable();
        //System.out.println("-----------------on hit robot----------");
         
        int action = decideStratgyFromEnvironmentState(mCurrentState, DefVariable.ACTIONS_UNDER_ONHITROBOT);
        mPreviousAction = action;
        mPreviousState = mCurrentState;
        switch (action) {
        case 0:
            //Conservative strategy
            if (e.getBearing() > -90 && e.getBearing() < 90) {
                back(100);
            } // else he's in back of us, so set ahead a bit.
            else {
                ahead(100);
            }
            break;
        case 1:
            if (trackName != null && !trackName.equals(e.getName())) {
                out.println("Tracking " + e.getName() + " due to collision");
            }
            // Set the target
            trackName = e.getName();
            // Back up a bit.
            // Note:  We won't get scan events while we're doing this!
            // An AdvancedRobot might use setBack(); execute();
            gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
            turnGunRight(gunTurnAmt);
            fire(3);
            back(50);
            break;
        default:
            break;
        }
        
		        
    }

    public void onHitByBullet(HitByBulletEvent event) {
        mCurrentState = getStateByCurrentEnvironment(DefVariable.STATE_ONHITBYBULLET);
        
        String maxQvalueUnderCurrentStateRow = Tool.getMaxQValueUnderState(mRawData, mCurrentState);
        double maxQvalue = Double.parseDouble(maxQvalueUnderCurrentStateRow.split(" ")[2]);
        
        if(mPreviousAction != DefVariable.NOACTION){
            executeQLearningFunction(mRawData, DefVariable.onHitByBulletReward, maxQvalue, mPreviousState, mPreviousAction);
        }
        //System.out.println("-----------------on hit by bullet----------");
        
        int action = decideStratgyFromEnvironmentState(mCurrentState, DefVariable.ACTIONS_UNDER_ONHITBYBULLET);
        mPreviousAction = action;
        mPreviousState = mCurrentState;
        switch(action){
        case 0: {
            double absoluteBearing = getHeading() + event.getBearing();
            double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
            turnLeft(event.getBearing() + 100);
            ahead(200);
            turnRight(360);
            turnGunRight(bearingFromGun);
            if (getGunHeat() == 0) {
                if (this.hitEnemy) {
                    this.bulletPower += 0.5;
                } else {
                    this.bulletPower -= 0.5;
                }
                fire(this.bulletPower);
            }
            ahead(50);
            break;
        }
        case 1: {
          //track fire's strategy
            turnRight(normalRelativeAngleDegrees(90 - (getHeading() - event.getHeading())));

            ahead(dist);
            dist *= -1;
            scan();
            break;
        }
        }
        
    }
    
    public void onHitByRobot(HitRobotEvent event) {
        //System.out.println("-----------------on hit by robot----------");
    }
    
    public void onHitWall(HitWallEvent event) {
        //System.out.println("-----------------on hit wall----------");
    }

    public void onWin(WinEvent e) {
        
        //String maxQvalueUnderCurrentStateRow = Tool.getMaxQValueUnderState(mRawData, DefVariable.STATE_END);
        //double maxQvalue = Double.parseDouble(maxQvalueUnderCurrentStateRow.split(" ")[2]);
        // TODO give max Q 0 ???
        if(mPreviousAction != DefVariable.NOACTION && mPreviousState != null){
            executeQLearningFunction(mRawData, DefVariable.onWinReward, 0, mPreviousState, mPreviousAction);
        }
        this.writeToFile(mRawData, QLearningDataFile);
     // Victory dance
        turnRight(36000);
    }
	
    public void onDeath(DeathEvent e){
        
        //String maxQvalueUnderCurrentStateRow = Tool.getMaxQValueUnderState(mRawData, DefVariable.STATE_END);
        //double maxQvalue = Double.parseDouble(maxQvalueUnderCurrentStateRow.split(" ")[2]);
        
        if(mPreviousAction != DefVariable.NOACTION && mPreviousState != null){
            executeQLearningFunction(mRawData, DefVariable.onDeathReward, 0, mPreviousState, mPreviousAction);
        }
        
        this.writeToFile(mRawData, QLearningDataFile);
    }
	
	private void writeToFile(ArrayList<String> rawData, String fileName) {
        //System.out.println("Writing File " + System.currentTimeMillis());
		PrintStream w = null;
		try {
			w = new PrintStream(new RobocodeFileOutputStream(getDataFile(fileName)));
			
			String inData = new String();
            for (String s : rawData) {
                inData = inData + s + "\n";
            }
            w.println(inData);
            w.close();
        } catch (IOException e1) {
            e1.printStackTrace(out);
        } finally {
			if (w != null) {
				w.close();
			}
		}
	}
    
    private void executeQLearningFunction(ArrayList<String> rawData, int reward, double maxQvalue, RobotState previousState, int previousAction){
        double alpha = DefVariable.ALPHA;
        double gamma = DefVariable.GAMMA;
        double oldValue = Tool.getQValueByStateAndAction(rawData, previousState, previousAction);
        double newValue = oldValue + alpha*( (double)reward + gamma*maxQvalue - oldValue);
        Tool.updateQValueByStateAndAction(rawData, previousState, previousAction, newValue);
        
    }
    
    
    private RobotState getStateByCurrentEnvironment(int eventNumber){
        int numberEnemy = getOthers();
        int zoneNumber = getAreaZone(getX(), getY());
        int powerLevel = getPowerLevel(getEnergy());
        int nearestDistance = getDistanceWithRobot(lastseen);
        int freshness = getTimeWithRobot(lastseen);
        int event = eventNumber;
        
        RobotState state = new RobotState(zoneNumber, numberEnemy, powerLevel, nearestDistance, event, freshness);
        return state;
    }
    private int getAreaZone(double x, double y){
        return 0;
    }
    private int getDistanceWithRobot(ScannedRobotEvent e){
        
        if(e == null){
            return 5;
        }
        double x = e.getDistance();
        if(x > 0 && x < 100){
            return 0;
        }
        else if(x >= 100 && x < 200){
            return 1;
        }
        else if(x>=200 && x<300){
            return 2;
        }
        else if(x>=300 && x<400){
            return 3;
        }
        else if(x>=400){
            return 4;
        }
        return -1;
    }
    
    private int getTimeWithRobot(ScannedRobotEvent e){
        if (e == null){
            return 5;
        }
        double diff = getTime() - e.getTime();
        if(diff > 0 && diff < 10){
            return 0;
        }
        else if(diff >= 10 && diff < 20){
            return 1;
        }
        else if(diff >= 20 && diff < 30){
            return 2;
        }
        else if(diff >= 30 && diff < 40){
            return 3;
        }
        else if(diff >= 40){
            return 4;
        }
        return -1;
    }
    private int getPowerLevel(double power){
        if(power > 0 && power < 10){
            return 0;
        }
        else if(power >= 10 && power < 20){
            return 1;
        }
        else if(power >= 20 && power < 30){
            return 2;
        }
        else if(power >= 30 && power < 40){
            return 3;
        }
            return 4;
    }
}               

