package louis;

import java.util.ArrayList;

public class RobotState {
    
    private int mZone_number;
    private int mNum_enemy;
    private int mPower_level;
    private int mLiving_level;
    
    public RobotState(){
        mZone_number = 0;
        mNum_enemy = 0;
        mPower_level = 0;
        mLiving_level = 0;
    }
    
    public RobotState(int zone, int num, int power, int live){
        mZone_number = zone;
        mNum_enemy = num;
        mPower_level = power;
        mLiving_level = live;
    }
    
    public RobotState(String row){
        String data = row;
        String[] splited_row = data.split(",");
        if(splited_row.length == DefVariable.NUM_ATTR){
            mZone_number = Integer.parseInt(splited_row[0]);
            mNum_enemy = Integer.parseInt(splited_row[1]);
            mPower_level = Integer.parseInt(splited_row[2]);
            mLiving_level = Integer.parseInt(splited_row[3]);
        }
    }
    
    public boolean equal(RobotState state){
        if( this.mNum_enemy == state.getNumEnemy() && 
            this.mPower_level == state.getPowerLevel() && 
            this.mZone_number == state.getZoneNumber() &&
            this.mLiving_level == state.getPeriodLevel()
            ){
                return true; 
        }
        return false;
    }
    public String toString(){
        return mZone_number + "," + mNum_enemy + "," + mPower_level + "," + mLiving_level;
    }
    public int getPeriodLevel(){
        return mLiving_level;
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
                    for (int o = 0; o < DefVariable.MAX_PERIOD; o++) {
                        all.add(n + "," + j + "," + m + "," + o);
                    }
                }
            }
        }
        return all;
    }
    
}