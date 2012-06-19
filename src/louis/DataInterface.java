package louis;

import louis.driver.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataInterface {
    private HashMap<String, HashMap<Integer, Double> > mHashMap;
    
    public DataInterface(){
        mHashMap = new HashMap<String, HashMap<Integer,Double>>();
    }
    
    public void initAllData(){
        ArrayList<String> permutationSet = RobotState.allpossibleState();
        for(String state : permutationSet){
            HashMap<Integer, Double> map = new HashMap<Integer, Double>();
            for(int i = 0; i < DriverManager.getNumberOfDriver(); i++){
                map.put(i, 0.0);
            }
            mHashMap.put(state, map);
        }
    }
    
    public void addDataRow(String row) {
        String[] splited = row.split(" ");
        if (splited.length == 3) {
            String state = splited[0];
            Integer action = Integer.parseInt(splited[1]);
            double Qvalue = Double.parseDouble(splited[2]);

            if (mHashMap.containsKey(state)) {
                mHashMap.get(state).put(action, Qvalue);
            } else {
                HashMap<Integer, Double> h = new HashMap<Integer, Double>();
                h.put(action, Qvalue);
                mHashMap.put(state, h);
            }
        }
    }
    
    public String toStringOfAllData(){
        String output = new String();
        Iterator<String> i = mHashMap.keySet().iterator();
        while(i.hasNext()){
            String key = i.next().toString();
            HashMap<Integer, Double> map = mHashMap.get(key);
            Iterator<Integer> i_2 = map.keySet().iterator();
            while(i_2.hasNext()){
                int key2 = i_2.next();
                double value = map.get(key2);
                output = output + key + " " + key2 + " " + Double.toString(value) + "\n";
            }
        }
        return output;
    }
        
    public double getMaxQValueUnderState(RobotState state){
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
    
    public Integer getMaxQValueKeyUnderState(RobotState state){
        HashMap<Integer, Double> map = mHashMap.get(state.toString()); 
        double maxQ = -1000;
        Integer maxKey = -1;

        Iterator<Integer> i = map.keySet().iterator();
        while(i.hasNext()){
            int key = i.next();
            double value = map.get(key);
            if(value > maxQ){
                maxQ = value;
                maxKey = key;
            }
        }
        return maxKey;
    }

    public double getQValueByStateAndAction(RobotState state, Integer action){
        double Qvalue = 0;
        if(mHashMap.containsKey(state.toString())){
            HashMap<Integer, Double> map = mHashMap.get(state.toString());
            if(map.containsKey(action)){
                Qvalue = map.get(action);
            }
        }
        return Qvalue;
    }
    
    public boolean updateQValueByStateAndAction(RobotState state, Integer action, Double newQValue){
        if(mHashMap.containsKey(state.toString())){
            HashMap<Integer, Double> map = mHashMap.get(state.toString());
            if(map.containsKey(action)){
                map.put(action, newQValue);
            }
        }
        return false;
    }
    
    public Integer decideStratgyFromEnvironmentState(RobotState state) {
        
        HashMap<Integer, Double> map = mHashMap.get(state.toString());
        
        // find max qvalue key and remove it .
        Integer maxKey = getMaxQValueKeyUnderState(state);
        
        double random_distribute_number = Math.random();
        if(random_distribute_number <= 0.8){
            return maxKey;
        }
        else{
            double sum = 0;
            ArrayList<Double> actionsQs = new ArrayList<Double>();
            ArrayList<Integer> actionsId = new ArrayList<Integer>();
            
            Iterator<Integer> i = map.keySet().iterator();
            while(i.hasNext()){
                int key = i.next();
                double value = map.get(key);
                if(key == maxKey){
                    continue;
                }
                if(value < 0){
                    value = 1;
                }
                else {
                    value = value + 1;
                }
                sum += value;
                actionsQs.add(sum);
                actionsId.add(key);
            }
            double random_action = Math.random()*(sum);
            for(Double value : actionsQs){
                if(random_action <= value){
                    return actionsId.get(actionsQs.indexOf(value));
                }
            }
        }
        
        System.out.println("ERROR: Can't decide action for given state: " + state.toString());

        return -1;
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
