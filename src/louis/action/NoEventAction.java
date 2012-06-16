package louis.action;

import louis.action.Action;
import robocode.AdvancedRobot;
import static robocode.util.Utils.normalRelativeAngleDegrees;

public class NoEventAction extends Action {
	private static String[] allPossibleActionIds = {"1"};
	
	public static String[] getAllPossibleActions() {
		return allPossibleActionIds;
	}
	
	public NoEventAction(String actionId) {
		super(actionId);
	}
	
	public void run(AdvancedRobot robot) {
		switch (id) {
			case 1:
				robot.turnGunRight(90);
			default: System.out.println("Error: Unrecognize action ID for NoEventAction: " + id);
				break;
		}
	}
}