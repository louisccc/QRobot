package louis;

import louis.driver.*;

import robocode.AdvancedRobot;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobocodeFileOutputStream;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class QRobot extends AdvancedRobot {

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
		Driver d = DriverManager.getDriver(actionId, this);
		d.init();
		
		mPreviousAction = actionId;
		mPreviousActionStartedTurn = getTime();
		return d;
	}
	
	public RobotState detectCurrentState(){
	    return getStateByCurrentEnvironment(DefVariable.STATE_START);      
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCurrentStrategy = SwitchDriverTo(mDataInterface.decideStratgyFromEnvironmentState(mCurrentState));
		
		while (true) {
		    if(isDriverExpire(getTime())){
		        RobotState temp_currentState = detectCurrentState();
		        switchCurrentStateToState( temp_currentState );
	            
	            // TODO sum up to accumulated reward ?  and replace 1 with sum up value
		        
	            executeQLearningFunction(1, mDataInterface.getMaxQValueUnderState(mCurrentState), mPreviousState, mPreviousAction);
	            
	            mCurrentStrategy.reset();
	            mCurrentStrategy = SwitchDriverTo(mDataInterface.decideStratgyFromEnvironmentState(mCurrentState));
		    }
		    mCurrentStrategy.loop();
		}
    }

    public void onScannedRobot(ScannedRobotEvent e) {
		if(isDriverExpire(getTime())){
		    RobotState temp_currentState = detectCurrentState();
            switchCurrentStateToState( temp_currentState );
            
            // TODO sum up to accumulated reward ?  and replace 1 with sum up value
            
            executeQLearningFunction(1, mDataInterface.getMaxQValueUnderState(mCurrentState), mPreviousState, mPreviousAction);
            
            mCurrentStrategy.reset();
            mCurrentStrategy = SwitchDriverTo(mDataInterface.decideStratgyFromEnvironmentState(mCurrentState));
		}
		else{
		 // If driver is not expired, accumulate the reward number.
		}
		mCurrentStrategy.onScannedRobot(e);
    }
	
    public void onHitRobot(HitRobotEvent e){
        if(isDriverExpire(getTime())){
            RobotState temp_currentState = detectCurrentState();
            switchCurrentStateToState( temp_currentState );
            
            // TODO sum up to accumulated reward ?  and replace 1 with sum up value
            
            executeQLearningFunction(1, mDataInterface.getMaxQValueUnderState(mCurrentState), mPreviousState, mPreviousAction);

            mCurrentStrategy.reset();
            mCurrentStrategy = SwitchDriverTo(mDataInterface.decideStratgyFromEnvironmentState(mCurrentState));
        }
        else{
         // If driver is not expired, accumulate the reward number.
        }
        mCurrentStrategy.onHitRobot(e);
    }
	
    public void onHitByBullet(HitByBulletEvent e) {
        if(isDriverExpire(getTime())){
            RobotState temp_currentState = detectCurrentState();
            switchCurrentStateToState( temp_currentState );
            
            // TODO sum up to accumulated reward ?  and replace 1 with sum up value
            
            executeQLearningFunction(1, mDataInterface.getMaxQValueUnderState(mCurrentState), mPreviousState, mPreviousAction);
            
            mCurrentStrategy.reset();
            mCurrentStrategy = SwitchDriverTo(mDataInterface.decideStratgyFromEnvironmentState(mCurrentState));   
        }
        else{
         // If driver is not expired, accumulate the reward number.
        }
        mCurrentStrategy.onHitByBullet(e);
    }
    
    public void onHitWall(HitWallEvent e) {
        
        if(isDriverExpire(getTime())){
            RobotState temp_currentState = detectCurrentState();
            switchCurrentStateToState( temp_currentState );
            
            // TODO sum up to accumulated reward ?  and replace 1 with sum up value
            
            executeQLearningFunction(1, mDataInterface.getMaxQValueUnderState(mCurrentState), mPreviousState, mPreviousAction);
            
            mCurrentStrategy.reset();
            mCurrentStrategy = SwitchDriverTo(mDataInterface.decideStratgyFromEnvironmentState(mCurrentState));
        }
        else {
         // If driver is not expired, accumulate the reward number.
        }
        mCurrentStrategy.onHitWall(e);
    }
	
    public void onWin(WinEvent e) {
        executeQLearningFunction(DefVariable.onWinReward, 0, mPreviousState, mPreviousAction);
		this.writeToFile(DefVariable.QLEARNING_DATA_FILE);
        turnRight(36000);
    }
	
    public void onDeath(DeathEvent e){
	    executeQLearningFunction(DefVariable.onDeathReward, 0, mPreviousState, mPreviousAction);
		this.writeToFile(DefVariable.QLEARNING_DATA_FILE);
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

