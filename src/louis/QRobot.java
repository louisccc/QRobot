package louis;

import robocode.AdvancedRobot;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;
import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.xml.crypto.Data;

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
    private ArrayList<String> mRawData = new ArrayList<String>();

    private final double gamma = 0.9;
    private final double alpha = 0.5;
    
    private static final int STARTSTATE = 0;
    private static final int NOACTION = -1;
    
    private static final int ONSCAN1 = 1;
    private static final int ONHIT = 2;
    private static final int ONHITBYBULLET = 3;
    
    private final int onHitReward = 1;
    private final int onHitByBulletReward = -1;
    
    double gunTurnAmt; // How much to turn our gun when searching
    private String trackName = null;
    
    double bulletPower = 1;  /*Narchi*/
    boolean hitEnemy; /*Narchi*/
    
    
    private int mCurrentState;
    int dist = 50; // distance to move when we're hit
    private int mPreviousState = -1;
    private int mPreviousAction = -1;
    public int decideStratgyFromEnvironmentState(){
        int state = mCurrentState;
        //int[] actions = getActionsUnderState(state);
        //int random_action = (int)Math.random()*(actions.length);
        return 0;
    }

    public void readTable() throws Exception{
        //mQValueTable = new HashMap<>();
        //int roundCount=0;
        //int battleCount=0;
        
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
			// TODO: Should initial raw data here
            e.printStackTrace();
        } catch (NumberFormatException e) {
			// TODO: Should initial raw data here
            e.printStackTrace();
        }
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
            setCurrentState(STARTSTATE);
            mPreviousState = STARTSTATE;
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
        // Calculate exact location of the robot
        //mPreviousAction = xxx
        //judge whether the state is 
        mCurrentState = ONSCAN1;
        //printRawTable();
        String maxQvalueUnderCurrentStateRow = getMaxQValueUnderState(mRawData, ONSCAN1);
        int reward = 0;
        double maxQvalue = Double.parseDouble(maxQvalueUnderCurrentStateRow.split(" ")[2]);
        //System.out.println("max Q is " + maxQvalue);
        if(mPreviousAction != NOACTION){
            executeQLearningFunction(mRawData, reward, maxQvalue, mPreviousState, mPreviousAction);
        }
		
        int action = decideStratgyFromEnvironmentState();
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
        mPreviousState = mCurrentState;
        mPreviousAction = action;
    }

    public void onHitRobot(HitRobotEvent e){
        mCurrentState = ONHIT;
        String maxQvalueUnderCurrentStateRow = getMaxQValueUnderState(mRawData, ONHIT);
        int reward = 1;
        double maxQvalue = Double.parseDouble(maxQvalueUnderCurrentStateRow.split(" ")[2]);
        
        if(mPreviousAction != NOACTION){
            executeQLearningFunction(mRawData, reward, maxQvalue, mPreviousState, mPreviousAction);
        }
        //printRawTable();
        System.out.println("-----------------on hit robot----------");
        
        int action = decideStratgyFromEnvironmentState();
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
        
		mPreviousAction = 0;
        mPreviousState = ONHIT;        
    }

    public void onHitByBullet(HitByBulletEvent event) {
        mCurrentState = ONHITBYBULLET;
        String maxQvalueUnderCurrentStateRow = getMaxQValueUnderState(mRawData, ONHITBYBULLET);
        double maxQvalue = Double.parseDouble(maxQvalueUnderCurrentStateRow.split(" ")[2]);
        
        if(mPreviousAction != NOACTION){
            executeQLearningFunction(mRawData, onHitByBulletReward, maxQvalue, mPreviousState, mPreviousAction);
        }
        System.out.println("-----------------on hit by bullet----------");
        
        int action = decideStratgyFromEnvironmentState();
        mPreviousAction = action;
        mPreviousState = ONHITBYBULLET;
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
        System.out.println("-----------------on hit by robot----------");
    }
    
    public void onHitWall(HitWallEvent event) {
        System.out.println("-----------------on hit wall----------");
    }

    public void onWin(WinEvent e) {
        // Victory dance
        String maxQvalueUnderCurrentStateRow = getMaxQValueUnderState(mRawData, ONHIT);
        int reward = 100;
        double maxQvalue = Double.parseDouble(maxQvalueUnderCurrentStateRow.split(" ")[2]);
        
        if(mPreviousAction != NOACTION && mPreviousState != STARTSTATE){
            executeQLearningFunction(mRawData, reward, maxQvalue, mPreviousState, mPreviousAction);
        }
        
		this.writeToFile();
        turnRight(36000);
    }
	
    public void onDeath(DeathEvent e){
        
        String maxQvalueUnderCurrentStateRow = getMaxQValueUnderState(mRawData, ONHIT);
        int reward = -100;
        double maxQvalue = Double.parseDouble(maxQvalueUnderCurrentStateRow.split(" ")[2]);
        
        if(mPreviousAction != NOACTION && mPreviousState != STARTSTATE){
            executeQLearningFunction(mRawData, reward, maxQvalue, mPreviousState, mPreviousAction);
        }
        
        this.writeToFile();
    }
	
	private void writeToFile() {
        System.out.println("Writing File " + System.currentTimeMillis());
		
		PrintStream w = null;
		
		try {
			w = new PrintStream(new RobocodeFileOutputStream(getDataFile("count.dat")));
			
			String inData = new String();
            for (String s : mRawData) {
                inData = inData + s + "\n";
            }
            w.println(inData);
            // PrintStreams don't throw IOExceptions during prints,
            // they simply set a flag.... so check it here.
            w.close();
        } catch (IOException e1) {
            e1.printStackTrace(out);
        } finally {
			if (w != null) {
				w.close();
			}
		}
	}
    
    private void setCurrentState(int state_number){
        mCurrentState = state_number;
    }
    
    private double getQValueByStateAndAction(ArrayList<String> rawData, int stateNumber, int actionNumber){
        int Qvalue = 0;
        ArrayList<String> data = rawData;
        for(int i = 0; i < data.size(); i++){
            String[] splited_row = data.get(i).split(" ");
            if (splited_row.length == 3) {
                int state = Integer.parseInt(splited_row[0]);
                int action = Integer.parseInt(splited_row[1]);
                if(stateNumber == state && actionNumber == action){
                    return Double.parseDouble(splited_row[2]);
                }
            }
        }
        return Qvalue;
    }
	
    private boolean updateQValueByStateAndAction(ArrayList<String> rawData, int stateNumber, int actionNumber, double newQValue){
        ArrayList<String> data = rawData;
        for(int i = 0; i < data.size(); i++){
            String[] splited_row = data.get(i).split(" ");
            if (splited_row.length == 3) {
                int state = Integer.parseInt(splited_row[0]);
                int action = Integer.parseInt(splited_row[1]);
                if(stateNumber == state && actionNumber == action){
                    String newRowString = state + " " + action + " " + Double.toString(newQValue);
                    data.set(i, newRowString);
                    return true;
                }
            }
        }
        return false;
    }
    
    private int[] getActionsUnderState(int state){
        int[] returnValue = null;
        return returnValue;
    }
    
    private void executeQLearningFunction(ArrayList<String> rawData, int reward, double maxQvalue, int previousState, int previousAction){
        double oldValue = getQValueByStateAndAction(rawData, previousState, previousAction);
        double newValue = oldValue + alpha*( (double)reward + gamma*maxQvalue - oldValue);
        updateQValueByStateAndAction(rawData, previousState, previousAction, newValue);
    }
    
    private ArrayList<String> getQValueDataRows(ArrayList<String> rawData, int state){
        ArrayList<String> actionsUnderState = new ArrayList<String>();
        for(String row : rawData){
            String[] splited = row.split(" ");
            if(splited.length == 3 && Integer.parseInt(splited[0]) == state){
                actionsUnderState.add(row);
            }
        }
        return actionsUnderState;
    }
    private String getMaxQValueUnderState(ArrayList<String> rawData, int state){
        ArrayList<String> actionsUnderState = getQValueDataRows(rawData, state);
        double maxQ = -1000;
        String maxRow = null;
        for(String row : actionsUnderState){
            String[] splited = row.split(" ");
            if(splited.length == 3){
                double value = Double.parseDouble(splited[2]);
                if(value > maxQ){
                    maxQ = value;
                    maxRow = row;
                }
            }
        }
        return maxRow;
    }
}               

