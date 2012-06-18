package louis.action;

import louis.action.Action;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import static robocode.util.Utils.normalRelativeAngleDegrees;

public class OnScannedRobotAction extends Action {
	private static String[] allPossibleActionIds = {"51", "52", "53"/*, "54", "55", "56", "57", "58", "59"*/};
	protected ScannedRobotEvent event;
	
	public static String[] getAllPossibleActions() {
		return allPossibleActionIds;
	}
	
	public OnScannedRobotAction(String actionId, ScannedRobotEvent e) {
		super(actionId);
		event = e;
	}
	
	public void run(AdvancedRobot robot) {
		switch (id) {
			case 51: case 52: case 53:
				double absoluteBearing = robot.getHeading() + event.getBearing();
				double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - robot.getGunHeading());
				
				// If it's close enough, fire!
				if (Math.abs(bearingFromGun) <= 3) {
					robot.turnGunRight(bearingFromGun);
					// We check gun heat here, because calling fire()
					// uses a turn, which could cause us to lose track
					// of the other robot.
					if (robot.getGunHeat() == 0) {
						robot.fire(Math.min(3 - Math.abs(bearingFromGun), robot.getEnergy() - .1));
					}
				} // otherwise just set the gun to turn.
				// Note:  This will have no effect until we call scan()
				else {
					robot.turnGunRight(bearingFromGun);
				}
				// Generates another scan event if we see a robot.
				// We only need to call this if the gun (and therefore radar)
				// are not turning.  Otherwise, scan is called automatically.
				if (bearingFromGun == 0) {
					robot.scan();
				}
				break;
			case 54: 
				/* crazy's robot */
//				robot.fire(1);
				robot.fire(3);
				break;
			case 55: 
				/* fire's robot */
				if (event.getDistance() < 50 && robot.getEnergy() > 50) {
					robot.fire(3);
				} // otherwise, fire 1.
				else {
					robot.fire(1);
				}
				// Call scan again, before we turn the gun
				robot.scan();
				break;
/*			case 54:
				robot.turnGunRight(10);
				break;
			case 55:
				robot.turnGunLeft(10);
				break;
			case 56:
				robot.ahead(10);
				break;
			case 57:
				robot.back(10);
				break;
			case 58:
				robot.turnRight(10);
				break;
			case 59:
				robot.turnLeft(10);
				break;*/

				
			default: System.out.println("Error: Unrecognize action ID for OnScannedRobotAction: " + id);
				break;
		}
	}
}