package louis.driver;

import robocode.AdvancedRobot;

public class DriverManager {
	public static Integer getNumberOfDriver() { return 8; }
	
	public static Driver getDriver(int driverId, AdvancedRobot robot) {
		switch (driverId) {
/*			case 0: return new SpinBotDriver(robot);
			case 1: return new RamFireDriver(robot);
			case 2: return new RaikoDriver(robot);
			case 3: return new AristoclesDriver(robot);
			case 4: return new WallsDriver(robot);
			case 5: return new TrackFireDriver(robot);
			case 6: return new AspirantDriver(robot);
			case 7: return new DookiousDriver(robot);
			case 8: return new SuperRamFireDriver(robot);
			case 9: return new SuperWallsDriver(robot);*/
				
			case 0: return new SpinBotDriver(robot);
			case 1: return new RamFireDriver(robot);
			case 2: return new WallsDriver(robot);
			case 3: return new TrackFireDriver(robot);
			case 4: return new TrackerDriver(robot);
			case 5: return new CrazyDriver(robot);
			case 6: return new CornersDriver(robot);
			case 7: return new FireDriver(robot);
				
			default: return null;
		}
	}
}
