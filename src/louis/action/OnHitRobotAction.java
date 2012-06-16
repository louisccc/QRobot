package louis.action;

import louis.action.Action;
import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import static robocode.util.Utils.normalRelativeAngleDegrees;

public class OnHitRobotAction extends Action {
	private static String[] allPossibleActionIds = {"151"};
	protected HitRobotEvent event;
	
	public static String[] getAllPossibleActions() {
		return allPossibleActionIds;
	}
	
	public OnHitRobotAction(String actionId, HitRobotEvent e) {
		super(actionId);
		event = e;
	}
	
	public void run(AdvancedRobot robot) {
		switch (id) {
			case 151:
				//Conservative strategy
				if (event.getBearing() > -90 && event.getBearing() < 90) {
					robot.back(100);
				} // else he's in back of us, so set ahead a bit.
				else {
					robot.ahead(100);
				}
				break;
/*			case 152:
				if (trackName != null && !trackName.equals(e.getName())) {
					out.println("Tracking " + e.getName() + " due to collision");
				}
				// Set the target
				trackName = e.getName();
				// Back up a bit.
				// Note:  We won't get scan events while we're doing this!
				// An AdvancedRobot might use setBack(); execute();
				gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
				turnGunRight(gunTurnAmt);
				fire(3);
				back(50);
				break;*/
			default: System.out.println("Error: Unrecognize action ID for OnHitRobotAction: " + id);
				break;
		}
	}
}