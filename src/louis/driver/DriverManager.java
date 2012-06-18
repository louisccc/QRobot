package louis.driver;

import robocode.AdvancedRobot;

public class DriverManager {
	public static Integer getNumberOfDriver() { return 1; }
	
	public static Driver getDriver(int driverId, AdvancedRobot robot) {
		switch (driverId) {
			case 0: return new SpinBotDriver(robot);
			default: return null;
		}
	}
}
