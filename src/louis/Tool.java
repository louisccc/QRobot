package louis;

import java.util.ArrayList;

public class Tool {
    
    public static ArrayList<String> getQValueDataRows(ArrayList<String> rawData, RobotState state){
        ArrayList<String> actionsUnderState = new ArrayList<String>();
        for(String row : rawData){
            String[] splited = row.split(" ");
            if(splited.length == 3 ){
                String stateString = splited[0];
                RobotState compare = new RobotState(stateString);
                if(compare.equal(state)){
                    actionsUnderState.add(row);
                }
            }
        }
        return actionsUnderState;
    }
    
    public static String getMaxQValueUnderState(ArrayList<String> rawData, RobotState state){
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
    
    public static double getQValueByStateAndAction(ArrayList<String> rawData, RobotState state, int action){
        int Qvalue = 0;
        ArrayList<String> data = rawData;
        for(int i = 0; i < data.size(); i++){
            String[] splited_row = data.get(i).split(" ");
            if (splited_row.length == 3) {
                RobotState state_temp = new RobotState(splited_row[0]);
                int action_temp = Integer.parseInt(splited_row[1]);
                if(state_temp.equal(state) && action_temp == action){
                    return Double.parseDouble(splited_row[2]);
                }
            }
        }
        return Qvalue;
    }
    
    public static boolean updateQValueByStateAndAction(ArrayList<String> rawData, RobotState state, int action, double newQValue){
        ArrayList<String> data = rawData;
        for(int i = 0; i < data.size(); i++){
            String[] splited_row = data.get(i).split(" ");
            if (splited_row.length == 3) {
                RobotState state_temp = new RobotState(splited_row[0]);
                int action_temp = Integer.parseInt(splited_row[1]);
                if(state_temp.equal(state) && action_temp == action){
                    String newRowString = state.toString() + " " + action + " " + Double.toString(newQValue);
                    data.set(i, newRowString);
                    return true;
                }
            }
        }
        return false;
    }
    
    
}
