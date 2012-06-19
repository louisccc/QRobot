package louis;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RobotState {
    
    private int mZone_number;
    private int mNum_enemy;
    private int mPower_level;
    
    public RobotState(){
        mZone_number = 0;
        mNum_enemy = 0;
        mPower_level = 0;
    }
    
    public RobotState(int zone, int num, int power){
        mZone_number = zone;
        mNum_enemy = num;
        mPower_level = power;
    }
    
    public RobotState(String row){
        String data = row;
        String[] splited_row = data.split(",");
        if(splited_row.length == DefVariable.NUM_ATTR){
            mZone_number = Integer.parseInt(splited_row[0]);
            mNum_enemy = Integer.parseInt(splited_row[1]);
            mPower_level = Integer.parseInt(splited_row[2]);
        }
    }
    
    public boolean equal(RobotState state){
        if( this.mNum_enemy == state.getNumEnemy() && 
            this.mPower_level == state.getPowerLevel() && 
            this.mZone_number == state.getZoneNumber()
            ){
                return true; 
        }
        return false;
    }
    public String toString(){
        return mZone_number + "," + mNum_enemy + "," + mPower_level ;
    }
    public int getZoneNumber(){
        return mZone_number;
    }
    public int getNumEnemy(){
        return mNum_enemy;
    }
    public int getPowerLevel(){
        return mPower_level;
    }
    
    public static ArrayList<String> allpossibleState(){
        ArrayList<String> all = new ArrayList<String>();
        for (int j = 0; j < DefVariable.MAX_ENEMY; j++) {
            for (int m = 0; m < DefVariable.MAX_POWER; m++) {
                for (int n = 0; n < DefVariable.MAX_ZONE; n++) {
                    all.add(n + "," + j + "," + m );
                }
            }
        }
        return all;
    }
    
}