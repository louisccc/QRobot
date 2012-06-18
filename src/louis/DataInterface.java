package louis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import louis.action.Action;

public class DataInterface {
    private HashMap<String, HashMap<Integer, Double> > mHashMap;
    public DataInterface(){
        mHashMap = new HashMap<String, HashMap<Integer,Double>>();
    }
    
    public void addDataRow(String row){
        String[] splited = row.split(" ");
        String state = splited[0];
        int action = Integer.parseInt(splited[1]);
        double Qvalue = Double.parseDouble(splited[2]);
        if(mHashMap.containsKey(state)){
            HashMap<Integer, Double> h = mHashMap.get(state);
            h.put(action, Qvalue);
        }
        else {
            HashMap<Integer, Double> h = new HashMap<Integer, Double>();
            h.put(action, Qvalue);
            mHashMap.put(state, h);
        }
        
    }
    
    public static ArrayList<String> getQValueDataRowsUnderState(ArrayList<String> rawData, RobotState state){
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
    
    public double getMaxQValueUnderState(ArrayList<String> rawData, RobotState state){
        //ArrayList<String> actionsUnderState = getQValueDataRows(rawData, state);
        HashMap<Integer, Double> map = mHashMap.get(state.toString()); 
        double maxQ = -1000;

        Iterator<Integer> i = map.keySet().iterator();
        while(i.hasNext()){
            int key = i.next();
            double value = map.get(key);
            if(value > maxQ){
                maxQ = value;
            }
        }
        return maxQ;
    }

    public double getQValueByStateAndAction(RobotState state, Action action){
        double Qvalue = 0;
        if(mHashMap.containsKey(state.toString())){
            HashMap<Integer, Double> map = mHashMap.get(state.toString());
            if(map.containsKey(Integer.parseInt(action.getId()))){
                Qvalue = map.get(Integer.parseInt(action.getId()));
            }
        }
        return Qvalue;
    }
    
    public boolean updateQValueByStateAndAction(ArrayList<String> rawData, RobotState state, Action action, double newQValue){
        if(mHashMap.containsKey(state.toString())){
            HashMap<Integer, Double> map = mHashMap.get(state.toString());
            if(map.containsKey(Integer.parseInt(action.getId()))){
                map.put(Integer.parseInt(action.getId()), newQValue);
            }
        }
        return false;
    }
    
    public String decideStratgyFromEnvironmentState(ArrayList<String> rawData, RobotState state) {
        ArrayList<Double> actionsQs = new ArrayList<Double>();
        ArrayList<String> actionsId = new ArrayList<String>();
        double sum = 0;
        
        HashMap<Integer, Double> map = mHashMap.get(state.toString());
        
        Iterator<Integer> i = map.keySet().iterator();
        while(i.hasNext()){
            int key = i.next();
            double value = map.get(key);
            if(value < 0){
                value = 1;
            }
            else {
                value = value + 1;
            }
            sum += value;
            actionsQs.add(sum);
            actionsId.add(i.toString());
            //System.out.println(key + " " + key + " " + value);
        }
        
        double random_action = Math.random()*(sum);
        for(Double value : actionsQs){
            if(random_action <= value){
//              System.out.println(value + " " + actionsId.get(actionsQs.indexOf(value)));
                return actionsId.get(actionsQs.indexOf(value));
            }
        }
        System.out.println("ERROR: Can't decide action for given state: " + state.toString());

        return null;
    }
    
    public void printAllData(){
        int counter = 0;
        Iterator<String> i = mHashMap.keySet().iterator();
        while(i.hasNext()){
            String key = i.next().toString();
            HashMap<Integer, Double> map = mHashMap.get(key);
            Iterator<Integer> i_2 = map.keySet().iterator();
            while(i_2.hasNext()){
                int key2 = i_2.next();
                double value = map.get(key2);
                System.out.println(key + " " + key2 + " " + value);
                counter++;
            }
        }
        System.out.println("all " + counter);
    }
    
}