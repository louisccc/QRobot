package louis;

import louis.action.*;

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
    private Action mPreviousAction = null;
    
    private ScannedRobotEvent lastseen = null;
    private DataInterface mDataInterface;
    public void readTable() throws Exception{
        mDataInterface = new DataInterface();
        try {
            BufferedReader r = new BufferedReader(new FileReader(getDataFile("count.dat")));
            String data_row;
            while( (data_row = r.readLine()) !=  null){
                String[] splited = data_row.split(" ");
                if(splited.length == 3){
                    mRawData.add(data_row);
                    mDataInterface.addDataRow(data_row);
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
        //mDataInterface.printAllData();
    }
    
    public void initRawData(ArrayList<String> rawdata){
        //int num_allState = DefVariable.EVENTCOUNT;
        ArrayList<String> allStates = RobotState.allposibleState();
		
		for (String s1 : RobotState.allposibleState(DefVariable.STATE_START)) {
			for (String s2 : NoEventAction.getAllPossibleActions()) {
				rawdata.add(s1 + " " + s2 + " 0");
			}
		}
		
		for (String s1 : RobotState.allposibleState(DefVariable.STATE_ONSCAN1)) {
			for (String s2 : OnScannedRobotAction.getAllPossibleActions()) {
				rawdata.add(s1 + " " + s2 + " 0");
			}
		}
		
		for (String s1 : RobotState.allposibleState(DefVariable.STATE_ONHIT)) {
			for (String s2 : OnHitRobotAction.getAllPossibleActions()) {
				rawdata.add(s1 + " " + s2 + " 0");
			}
		}
		
		for (String s1 : RobotState.allposibleState(DefVariable.STATE_ONHITBYBULLET)) {
			for (String s2 : OnHitByBulletAction.getAllPossibleActions()) {
				rawdata.add(s1 + " " + s2 + " 0");
			}
		}
		
		for (String s1 : RobotState.allposibleState(DefVariable.STATE_END)) {
			rawdata.add(s1 + " " + " 0 0");
		}
    }
    
    public void printRawTable() {
        for(int i = 0; i < mRawData.size(); i++){
            System.out.println(mRawData.get(i));
        }
    }
    public void printDataInterfaceData() {
        
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Loop forever
        while (true) {
//			turnGunRight(10);

			// Decide action
			mPreviousState = mCurrentState;
			mCurrentState = getStateByCurrentEnvironment(DefVariable.STATE_START);			
			
			String actionId = Tool.decideStratgyFromEnvironmentState(mRawData, mCurrentState);
            mPreviousAction = new NoEventAction(actionId);
			mPreviousAction.run(this);
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
        if(mPreviousAction != null){
            executeQLearningFunction(mRawData, DefVariable.onScanRobotReward, maxQvalue, mPreviousState, mPreviousAction);
        }
		
        String actionId = Tool.decideStratgyFromEnvironmentState(mRawData, mCurrentState);
		Action action = new OnScannedRobotAction(actionId, e);	
        
        mPreviousState = mCurrentState;
        mPreviousAction = action;
		
        action.run(this);
    }

    public void onHitRobot(HitRobotEvent e){
        
        mCurrentState = getStateByCurrentEnvironment(DefVariable.STATE_ONHIT);
        
        String maxQvalueUnderCurrentStateRow = Tool.getMaxQValueUnderState(mRawData, mCurrentState);
        double maxQvalue = Double.parseDouble(maxQvalueUnderCurrentStateRow.split(" ")[2]);
        
        if(mPreviousAction != null){
            executeQLearningFunction(mRawData, DefVariable.onHitReward, maxQvalue, mPreviousState, mPreviousAction);
        }
        //printRawTable();
        //System.out.println("-----------------on hit robot----------");
         
		String actionId = Tool.decideStratgyFromEnvironmentState(mRawData, mCurrentState);
		Action action = new OnHitRobotAction(actionId, e);	
        
        mPreviousState = mCurrentState;
        mPreviousAction = action;
		
        action.run(this);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        mCurrentState = getStateByCurrentEnvironment(DefVariable.STATE_ONHITBYBULLET);
        
        String maxQvalueUnderCurrentStateRow = Tool.getMaxQValueUnderState(mRawData, mCurrentState);
        double maxQvalue = Double.parseDouble(maxQvalueUnderCurrentStateRow.split(" ")[2]);
        
        if(mPreviousAction != null){
            executeQLearningFunction(mRawData, DefVariable.onHitByBulletReward, maxQvalue, mPreviousState, mPreviousAction);
        }
        //System.out.println("-----------------on hit by bullet----------");
        
		
		String actionId = Tool.decideStratgyFromEnvironmentState(mRawData, mCurrentState);
		Action action = new OnHitByBulletAction(actionId, e);	
        
        mPreviousState = mCurrentState;
        mPreviousAction = action;
		
        action.run(this);        
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
        if(mPreviousAction != null && mPreviousState != null){
            executeQLearningFunction(mRawData, DefVariable.onWinReward, 0, mPreviousState, mPreviousAction);
        }
        this.writeToFile(mRawData, QLearningDataFile);
     // Victory dance
        turnRight(36000);
    }
	
    public void onDeath(DeathEvent e){
        
        //String maxQvalueUnderCurrentStateRow = Tool.getMaxQValueUnderState(mRawData, DefVariable.STATE_END);
        //double maxQvalue = Double.parseDouble(maxQvalueUnderCurrentStateRow.split(" ")[2]);
        
        if(mPreviousAction != null && mPreviousState != null){
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
    
    private void executeQLearningFunction(ArrayList<String> rawData, int reward, double maxQvalue, RobotState previousState, Action previousAction){
        double alpha = DefVariable.ALPHA;
        double gamma = DefVariable.GAMMA;
        double oldValue = Tool.getQValueByStateAndAction(rawData, previousState, previousAction);
        double newValue = oldValue + alpha*( (double)reward + gamma*maxQvalue - oldValue);
        Tool.updateQValueByStateAndAction(rawData, previousState, previousAction, newValue);
        
    }
    
    private RobotState getStateByCurrentEnvironment(int eventNumber){
        int numberEnemy = getOthers() > 1 ? 1 : 0;
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
        
/*        if(e == null){
            return 4;
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
        return -1;*/
		
		if (e == null) return 1;
		double x = e.getDistance();
        if(x > 0 && x < 250){
            return 0;
        }
        else {
            return 1;
        }

    }
    
    private int getTimeWithRobot(ScannedRobotEvent e){
/*        if (e == null) return 1;
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
        return -1;*/
		if (e == null) return 1;
        double diff = getTime() - e.getTime();
        if(diff < 5){
            return 0;
        }
        else {
            return 1;
        }
    }
    private int getPowerLevel(double power){
        if (power < 50) return 0;
		else return 1;
    }
}               

