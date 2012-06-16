package louis.action;

import louis.action.Action;
import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import static robocode.util.Utils.normalRelativeAngleDegrees;

public class OnHitByBulletAction extends Action {
	private static String[] allPossibleActionIds = {"101"};
	protected HitByBulletEvent event;
	
	public static String[] getAllPossibleActions() {
		return allPossibleActionIds;
	}
	
	public OnHitByBulletAction(String actionId, HitByBulletEvent e) {
		super(actionId);
		event = e;
	}
	
	public void run(AdvancedRobot robot) {
		switch (id) {
			case 101:
				double absoluteBearing = robot.getHeading() + event.getBearing();
				double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - robot.getGunHeading());
				robot.turnLeft(event.getBearing() + 100);
				robot.ahead(200);
				robot.turnRight(360);
				robot.turnGunRight(bearingFromGun);
				if (robot.getGunHeat() == 0) {
/*					if (robot.hitEnemy) {
						this.bulletPower += 0.5;
					} else {
						this.bulletPower -= 0.5;
					}*/
//					robot.fire(this.bulletPower);
					robot.fire(1);
				}
				robot.ahead(50);
				break;
/*			case 1: {
				//track fire's strategy
				turnRight(normalRelativeAngleDegrees(90 - (getHeading() - event.getHeading())));
				
				ahead(dist);
				dist *= -1;
				scan();
				break;
			}*/

			default: System.out.println("Error: Unrecognize action ID for OnHitByBulletAction: " + id);
				break;
		}
	}
}