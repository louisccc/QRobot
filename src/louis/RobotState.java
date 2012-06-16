package louis;

public class RobotState {
    
    private static final int NUM_ATTR = 6;
       
    private int mZone_number;
    private int mNum_enemy;
    private int mPower_level;
    private int mNearest_enemy_distance;
    private int mEvent;
    private int mScanFreshness;
    
    public RobotState(){
        mZone_number = 0;
        mNum_enemy = 0;
        mPower_level = 0;
        mNearest_enemy_distance = 0;
        mEvent = 0;
        mScanFreshness = 0;
    }
    
    public RobotState(int zone, int num, int power, int dis, int event, int fresh){
        mZone_number = zone;
        mNum_enemy = num;
        mPower_level = power;
        mNearest_enemy_distance = dis;
        mEvent = event;
        mScanFreshness = fresh;
    }
    
    public RobotState(String row){
        String data = row;
        String[] splited_row = data.split(",");
        if(splited_row.length == NUM_ATTR){
            mZone_number = Integer.parseInt(splited_row[0]);
            mNum_enemy = Integer.parseInt(splited_row[1]);
            mPower_level = Integer.parseInt(splited_row[2]);
            mNearest_enemy_distance = Integer.parseInt(splited_row[3]);
            mEvent = Integer.parseInt(splited_row[4]);
            mScanFreshness = Integer.parseInt(splited_row[5]);
        }
    }
    
    public boolean equal(RobotState state){
        if( this.mEvent == state.getEvent() &&
            this.mNearest_enemy_distance == state.getNearestEnemyDistance() &&
            this.mNum_enemy == state.getNumEnemy() && 
            this.mPower_level == state.getPowerLevel() && 
            this.mScanFreshness == state.getScannFreshness() &&
            this.mZone_number == state.getZoneNumber()
            ){
                return true; 
        }
        return false;
    }
    public String toString(){
        return mZone_number + "," + mNum_enemy + "," + mPower_level + "," + mNearest_enemy_distance + "," + mEvent + "," + mScanFreshness;
    }
    public int getEvent(){
        return mEvent;
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
    public int getNearestEnemyDistance(){
        return mNearest_enemy_distance;
    }
    public int getScannFreshness(){
        return mScanFreshness;    
    }
    
}