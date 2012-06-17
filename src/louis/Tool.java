package louis;

import java.util.ArrayList;
import louis.action.*;

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
    
    public static double getQValueByStateAndAction(ArrayList<String> rawData, RobotState state, Action action){
        int Qvalue = 0;
        ArrayList<String> data = rawData;
        for(int i = 0; i < data.size(); i++){
            String[] splited_row = data.get(i).split(" ");
            if (splited_row.length == 3) {
                RobotState state_temp = new RobotState(splited_row[0]);
                String action_temp = splited_row[1];
                if(state_temp.equal(state) && action_temp.equals(action.getId())){
                    return Double.parseDouble(splited_row[2]);
                }
            }
        }
        return Qvalue;
    }
    
    public static boolean updateQValueByStateAndAction(ArrayList<String> rawData, RobotState state, Action action, double newQValue){
        ArrayList<String> data = rawData;
        for(int i = 0; i < data.size(); i++){
            String[] splited_row = data.get(i).split(" ");
            if (splited_row.length == 3) {
                RobotState state_temp = new RobotState(splited_row[0]);
                String action_temp = splited_row[1];
                if(state_temp.equal(state) && action_temp.equals(action.getId())){
                    String newRowString = state.toString() + " " + action.getId() + " " + Double.toString(newQValue);
                    data.set(i, newRowString);
                    return true;
                }
            }
        }
        return false;
    }
    
	public static String decideStratgyFromEnvironmentState(ArrayList<String> rawData, RobotState state) {
        ArrayList<Double> actionsQs = new ArrayList<Double>();
		ArrayList<String> actionsId = new ArrayList<String>();
		double sum = 0;
		
//		System.out.println(rawData.size());
		
		ArrayList<String> actionsUnderState = getQValueDataRows(rawData, state);
        for(String row : actionsUnderState){
            String[] splited = row.split(" ");
            if(splited.length == 3){
                double qvalue = Double.parseDouble(splited[2]);
				if(qvalue < 0){
					qvalue = 1;
				}
				else{
					qvalue = qvalue + 1;
				}
				sum += qvalue;
				actionsQs.add(sum);
				actionsId.add(splited[1]);
            }
        }
		
        double random_action = Math.random()*(sum);
        for(Double value : actionsQs){
            if(random_action <= value){
//				System.out.println(value + " " + actionsId.get(actionsQs.indexOf(value)));
                return actionsId.get(actionsQs.indexOf(value));
			}
        }
		System.out.println("ERROR: Can't decide action for given state: " + state.toString());

		return null;
    }
}
