package louis.driver;

import robocode.AdvancedRobot;

public class DriverManager {
	public static Integer getNumberOfDriver() { return 7; }
	
	public static Driver getDriver(int driverId, AdvancedRobot robot) {
		switch (driverId) {
			case 0: return new SpinBotDriver(robot);
			case 1: return new RamFireDriver(robot);
			case 2: return new RaikoDriver(robot);
			case 3: return new AristoclesDriver(robot);
			case 4: return new WallsDriver(robot);
			case 5: return new TrackFireDriver(robot);
			case 6: return new AspirantDriver(robot);
			default: return null;
		}
	}
}
