package louis;

import louis.driver.*;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.MessageEvent;
import robocode.RobocodeFileOutputStream;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;
import robocode.WinEvent;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class QRobot extends TeamRobot {

    private RobotState mCurrentState;
    private RobotState mPreviousState;
	private Integer mPreviousAction;
	private Long mPreviousActionStartedTurn;
	private DataInterface mDataInterface;
	private Driver mCurrentStrategy = null;
    
    private ScannedRobotEvent lastseen = null; // TODO not used >? 
    
	
    public void readTable() throws Exception{
        mDataInterface = new DataInterface();
        try {
            BufferedReader r = new BufferedReader(new FileReader(getDataFile("count.dat")));
            String data_row;
            while( (data_row = r.readLine()) !=  null){
				mDataInterface.addDataRow(data_row);
            }
            r.close();
        } catch (IOException e) {
			mDataInterface.initAllData();
			System.out.println("new table is created.");
        } catch (NumberFormatException e) {
			mDataInterface.initAllData();
			System.out.println("new table is created.");
        }
        //mDataInterface.printAllData(); for debug
    }
	
	public Driver SwitchDriverTo(Integer actionId) {
	    System.out.println("actionId : " + actionId);
		Driver d = DriverManager.getDriver(actionId, this);
		d.init();
		
		mPreviousAction = actionId;
		mPreviousActionStartedTurn = getTime();
		return d;
	}
	
	public RobotState detectCurrentState(){
	    RobotState s = getStateByCurrentEnvironment();
	    //System.out.println("currentState is : " + s.toString());
	    return s;      
	}
	
	public void switchCurrentStateToState(RobotState newState){
	    mPreviousState = mCurrentState;
	    mCurrentState = newState;
	}
	
	public boolean isDriverExpire(long timeNow) {
		if (timeNow - mPreviousActionStartedTurn > DefVariable.MAX_TIMER_TICKS) {
			return true;
		}
		return false;
	}
	private double accumulated_reward = 0;
	
	public void turnRoutine(){
	    if(isDriverExpire(getTime())){
            RobotState temp_currentState = detectCurrentState();
            switchCurrentStateToState( temp_currentState );
            
            executeQLearningFunction(accumulated_reward, mDataInterface.getMaxQValueUnderState(mCurrentState), mPreviousState, mPreviousAction);
            accumulated_reward = 0;
            mCurrentStrategy.reset();
            mCurrentStrategy = SwitchDriverTo(mDataInterface.decideStratgyFromEnvironmentState(mCurrentState));
        }
	}
	public void run() {
	    
        setBodyColor(Color.pink);
        setGunColor(Color.pink);
        setRadarColor(Color.pink);
        setScanColor(Color.pink);
        setBulletColor(Color.pink);
		
        try {
            readTable();
            //printRawTable();
            mCurrentState = detectCurrentState();
            mPreviousState = detectCurrentState();
			mPreviousAction = DefVariable.NOACTION;
			mPreviousActionStartedTurn = getTime();
			accumulated_reward = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCurrentStrategy = SwitchDriverTo(mDataInterface.decideStratgyFromEnvironmentState(mCurrentState));
		
		while (true) {
            try {
                if (getTeammates() != null) {
                    for (int i = 0; i < getTeammates().length; i++) {
                        sendMessage(getTeammates()[i], "fuck");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
		    turnRoutine();
		    mCurrentStrategy.loop();
		}
    }

    public void onScannedRobot(ScannedRobotEvent e) {
		turnRoutine();
		mCurrentStrategy.onScannedRobot(e);
    }
	
    public void onHitRobot(HitRobotEvent e){
        if(getEnergy() > e.getEnergy()){
            accumulated_reward = accumulated_reward + 1;
        }
        else{
            accumulated_reward = accumulated_reward - 1;
        }
        turnRoutine();
        mCurrentStrategy.onHitRobot(e);
    }
	
    public void onHitByBullet(HitByBulletEvent e) {
        accumulated_reward = accumulated_reward - (4 * e.getPower() + 2*Math.max(e.getPower()-1, 0));
        turnRoutine();
        mCurrentStrategy.onHitByBullet(e);
    }
    
    public void onHitWall(HitWallEvent e) {
        accumulated_reward = accumulated_reward - (getVelocity() * 0.5 - 1);
        turnRoutine();
        mCurrentStrategy.onHitWall(e);
    }
	
    public void onBulletHit(BulletHitEvent e){
        accumulated_reward = accumulated_reward + (4 * e.getBullet().getPower() + 2*Math.max(e.getBullet().getPower()-1, 0));
        turnRoutine();
        mCurrentStrategy.onBulletHit(e);
    }
    
    public void onBulletMiss(BulletMissedEvent e){
        accumulated_reward = accumulated_reward - e.getBullet().getPower();
        turnRoutine();
        mCurrentStrategy.onBulletMissed(e);
    }
    
    public void onWin(WinEvent e) {
        accumulated_reward = accumulated_reward + 60;
        executeQLearningFunction(accumulated_reward, 0, mPreviousState, mPreviousAction);
		this.writeToFile(DefVariable.QLEARNING_DATA_FILE);
        turnRight(36000);
    }
	
    public void onDeath(DeathEvent e){
        accumulated_reward = accumulated_reward - 60;
	    executeQLearningFunction(accumulated_reward, 0, mPreviousState, mPreviousAction);
		this.writeToFile(DefVariable.QLEARNING_DATA_FILE);
    }
	
    public void onMessageReceived(MessageEvent e){
        String[] e1 = e.getMessage().toString().split(" ");
        System.out.println(e.getSender() + " sent me: " + e.getMessage());
    }
    
	private void writeToFile(String fileName) {
		PrintStream w = null;
		try {
			w = new PrintStream(new RobocodeFileOutputStream(getDataFile(fileName)));
            w.print(mDataInterface.toStringOfAllData());
            w.close();
        } catch (IOException e1) {
            e1.printStackTrace(out);
        } finally {
			if (w != null) {
				w.close();
			}
		}
	}
    
    private void executeQLearningFunction(double reward, double maxQvalue, RobotState previousState, Integer previousAction){
        if(mPreviousAction != DefVariable.NOACTION && mPreviousState != null){
            double alpha = DefVariable.ALPHA;
            double gamma = DefVariable.GAMMA;
            double oldValue = mDataInterface.getQValueByStateAndAction(previousState, previousAction);
            double newValue = oldValue + alpha*( reward + gamma*maxQvalue - oldValue);
            mDataInterface.updateQValueByStateAndAction(previousState, previousAction, newValue);
        }
    }
    
    private RobotState getStateByCurrentEnvironment(){
        int numberEnemy = getEnemyLevel(getOthers());
        int zoneNumber = getAreaZone(getX(), getY());
        int powerLevel = getPowerLevel(getEnergy());
		int periodLevel = getPeriodLevel(getTime());
        RobotState state = new RobotState(zoneNumber, numberEnemy, powerLevel, periodLevel);
        return state;
    }
    private int getEnemyLevel(int x){
        if( x == 1 ){
            return 0;
        }
        else if ( x == 2 || x == 3 ){
            return 1;
        }
        else {
            return 2;
        }
    }
    private int getAreaZone(double x, double y){
        if (x < 200) {
            if (y < 200) {
                return 0;
            } else if (y >= 200 && y < 400) {
                return 1;
            } else {
                return 0;
            }
        } else if (x >= 200 && x < 400) {
            if (y < 200) {
                return 1;
            } else if (y >= 200 && y < 400) {
                return 2;
            } else {
                return 1;
            }
        } else if (x >= 400 && x < 600) {
            if (y < 200) {
                return 1;
            } else if (y >= 200 && y < 400) {
                return 2;
            } else {
                return 1;
            }
        } else {
            if (y < 200) {
                return 0;
            } else if (y >= 200 && y < 400) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private int getPowerLevel(double power){
        if (power < 30) return 0;
        else if( power < 60) return 1;
        else if( power < 90) return 2;
		else return 3;
    }

    private int getPeriodLevel(double time){
        if (time > 500){
            return 1;
        }
        return 0;
    }
}               

