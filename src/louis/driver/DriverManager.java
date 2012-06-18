package louis.driver;

import robocode.AdvancedRobot;

public class DriverManager {
	public static Integer getNumberOfDriver() { return 3; }
	
	public static Driver getDriver(int driverId, AdvancedRobot robot) {
		switch (driverId) {
			case 0: return new SpinBotDriver(robot);
			case 1: return new RamFireDriver(robot);
			case 2: return new RaikoDriver(robot);
			default: return null;
		}
	}
}
