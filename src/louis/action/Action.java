package louis.action;

import robocode.AdvancedRobot;
import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public abstract class Action {
	protected int id;
	
	public Action(String actionId) {
		id = Integer.valueOf(actionId);
	}
		
	public String getId() {
		return id + "";
	}
	
	public abstract void run(AdvancedRobot robot);
}