package louis.driver;

import robocode.AdvancedRobot;

public class DriverManager {
	public static Integer getNumberOfDriver() { return 2; }
	
	public static Driver getDriver(int driverId, AdvancedRobot robot) {
		switch (driverId) {
			case 0: return new SpinBotDriver(robot);
			case 1: return new RamFireDriver(robot);
			default: return null;
		}
	}
}
