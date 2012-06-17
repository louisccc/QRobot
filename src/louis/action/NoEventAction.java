package louis.action;

import louis.action.Action;
import robocode.AdvancedRobot;
import static robocode.util.Utils.normalRelativeAngleDegrees;

public class NoEventAction extends Action {
	private static String[] allPossibleActionIds = {"1", "2", "3", "4", "5", "6"};
	
	public static String[] getAllPossibleActions() {
		return allPossibleActionIds;
	}
	
	public NoEventAction(String actionId) {
		super(actionId);
	}
	
	public void run(AdvancedRobot robot) {
		switch (id) {
			case 1:
				robot.turnGunRight(50);
				break;
			case 2:
				robot.turnGunLeft(50);
				break;
			case 3:
				robot.ahead(100);
				break;
			case 4:
				robot.back(100);
				break;
			case 5:
				robot.turnRight(45);
				break;
			case 6:
				robot.turnLeft(45);
				break;


			default: System.out.println("Error: Unrecognize action ID for NoEventAction: " + id);
				break;
		}
	}
}