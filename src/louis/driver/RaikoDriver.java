package louis.driver;

import robocode.*;
import robocode.util.Utils;
import java.awt.geom.*;
import java.awt.Color;
import java.util.*;
import java.util.zip.*;
import java.io.*;

// This code is released under the RoboWiki Public Code Licence (RWPCL), datailed on:
// http://robowiki.net/?RWPCL
//
// Raiko by Jamougha.  Credit for the gun goes to PEZ, who wrote Tityus, upon which this is based.

public class RaikoDriver extends Driver {
    private static final double MAX_VELOCITY = 8;
    private static final int ACCEL_INDEXES = 3;
    private static final int DISTANCE_INDEXES = 5;
	private static final int VZERO_INDEXES = 3;
	private static final int V_INDEXES = 4;
	private static final int WALL_INDEXES = 4;
    private static final int AIM_FACTORS = 25;
    private static Point2D.Double robotLocation;
    private static Point2D.Double enemyLocation;
    private static double enemyAbsoluteBearing;
    private static double deltaBearing;
    private static int bearingDirection = 1;
    private static int[][][][][][] aimFactors;
	static int circleDir = 1;
	static final double BEST_DISTANCE = 525;
	double lastVChange = 0;
	double enemyVelocity = 0;
	static int[] currentAimFactors;
	private static String enemyName = "";
	//static final double modifiers[] = {13.0,11.5,10.0,8.5,7.5};
	static double lastReverseTime;
	static boolean flat = true;
	static double enemyDistance;
	static double numBadHits = 0; 
	static double enemyFirePower = 2;
	static double enemyEnergy;
	
	public RaikoDriver(AdvancedRobot targetRobot) {
		super(targetRobot);
	}

    public void init() {
        setColors(Color.red, Color.white, Color.white);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
		lastReverseTime = 0;
		robotLocation = new Point2D.Double();
	}
	
	public void loop() {
		turnRadarRightRadians(Double.POSITIVE_INFINITY); 
    }

    public void onScannedRobot(ScannedRobotEvent e) {
	
	    if (enemyName == "") {
            enemyName = e.getName();
            try {
            ZipInputStream zipin = new ZipInputStream(new
                FileInputStream(getDataFile(enemyName)));
            zipin.getNextEntry();
            ObjectInputStream in = new ObjectInputStream(zipin);
            aimFactors = (int[][][][][][])in.readObject();
            in.close();
        	}
        	catch (Exception en) {
	    		aimFactors = new int[ACCEL_INDEXES][DISTANCE_INDEXES][VZERO_INDEXES][V_INDEXES][WALL_INDEXES][AIM_FACTORS];
				flat = (getRoundNum() != 0);
					
        	}
        }

		/*-------- setup data -----*/
	    double bulletPower;
    	int distanceIndex;
		//double enemyDistance;
		double theta;
		Point2D.Double oldRobotLocation = robotLocation;
        robotLocation = new Point2D.Double(getX(), getY());
		double lastEnemyAbsBearing = enemyAbsoluteBearing;
        enemyAbsoluteBearing = getHeadingRadians() + e.getBearingRadians();
        enemyDistance = e.getDistance();
		enemyLocation = projectMotion(robotLocation, enemyAbsoluteBearing, enemyDistance);

		/* ---- Movement ----*/
	
        if ((enemyEnergy = (enemyEnergy - e.getEnergy())) >= 0.1 && enemyEnergy <= 3.0) 
            enemyFirePower = enemyEnergy;

        enemyEnergy = e.getEnergy();


		Point2D.Double newDestination;
		
		double distDelta = 0.02 + Math.PI/2 + (e.getDistance() > BEST_DISTANCE ? -.1 : .5);
		
		while (getDistanceToWalls((newDestination = projectMotion(robotLocation, enemyAbsoluteBearing + circleDir*(distDelta-=.02), 170))) < 18);
		
		//double modifiers[] = {8.0,10.3,10.5,11.0,10.6};
		//theta = modifiers[Math.min((int)(e.getDistance() / (800 / 5)), 4)]/e.getDistance();
		theta = 0.5952*bulletVelocity(enemyFirePower)/enemyDistance;
		
		if ( (Math.random() > Math.pow(theta, theta)  && flat)|| distDelta < Math.PI/4 || (distDelta < Math.PI/3 && enemyDistance < 400)){
			circleDir *= -1;
			lastReverseTime = getTime();
		}
			/*if (enemyDistance < 320)
				circleTime += 1.4*Math.random()*enemyDistance/11D;
			else
				while (Math.random() < Math.pow((enemyDistance < 300 ? 3D : 1D)*Math.pow(theta, theta))) circleTime++;
		}*/
			
		theta = Utils.normalRelativeAngle(absoluteBearing(robotLocation, newDestination) - getHeadingRadians());
		setAhead(Math.cos(theta)*100);
		setTurnRightRadians(Math.tan(theta));
		
	/* ------- Deal with segments ------*/
	
        double oldDeltaBearing = deltaBearing;
        deltaBearing = Utils.normalRelativeAngle(absoluteBearing(oldRobotLocation, enemyLocation) -
            lastEnemyAbsBearing);

	if (deltaBearing < 0) {
	    bearingDirection = -1;
	}
	else if (deltaBearing > 0) {
	    bearingDirection = 1;
	}

	distanceIndex = Math.min((int)(enemyDistance / (700D / DISTANCE_INDEXES)), DISTANCE_INDEXES - 1);
	
	double[] bulletPowers = { 3.0, 2.8, 2.4, 2.0, 1.7 };
        bulletPower = Math.min(getEnergy() / 5, Math.min(e.getEnergy() / 4, bulletPowers[distanceIndex]));
	
	Point2D.Double p = projectMotion(enemyLocation, e.getHeadingRadians(), .8*(enemyDistance/bulletVelocity(bulletPower))*e.getVelocity());

	
	if (Math.round(enemyVelocity - (enemyVelocity = e.getVelocity())) != 0)
		lastVChange = getTime();
		
	
        int delta = (int)(Math.round(200 * (Math.abs(deltaBearing) - Math.abs(oldDeltaBearing))));

		double t = Math.abs(enemyVelocity);
    	currentAimFactors = aimFactors[(delta < 0) ? 0 :((delta > 0) ? 2 : 1)] 
									[distanceIndex]
									[t < 2 ? 0 : (t < 6 ? 1 : 2)]
									[vSegment(enemyDistance/bulletVelocity(bulletPower))]
									[(getDistanceToWalls(p) < 18 ? 0 : 1)];

	/* ------------- Fire control ------- */
        setTurnGunRightRadians(Utils.normalRelativeAngle(
            enemyAbsoluteBearing + maxEscapeAngle(bulletVelocity(bulletPower)) *
		bearingDirection * mostVisitedFactor() - getGunHeadingRadians()));

        if ((getEnergy() > 0.3 || enemyDistance < 150) ){
	    	setFire(bulletPower);
			if (bulletPower == bulletPowers[distanceIndex])
				addCustomEvent(new Wave(bulletVelocity(bulletPower)));
        }

        setTurnRadarRightRadians(Utils.normalRelativeAngle(enemyAbsoluteBearing - getRadarHeadingRadians()) * 2);
    }

    private double mostVisitedFactor() {
        int mostVisited = (AIM_FACTORS - 1) / 2;
        for (int i = 0; i < AIM_FACTORS; i++) {
            if (currentAimFactors[i] > currentAimFactors[mostVisited]) {
                mostVisited = i;
            }
        }
	return ((mostVisited + 0.5) / AIM_FACTORS) * 2 - 1;
    }

	
	private int vSegment(double time){

		double t = (getTime() - lastVChange)/time;
		if (t < .1)
			return 0;
		if (t < .3)
			return 1;
		if (t < 1)
			return 2;	
		return 3;
	}
	
	
    public void onHitByBullet(HitByBulletEvent e) {
		/* 
		The infamous Axe-hack
	 	see: http://robowiki.net/?Musashi 
		*/
		if ((double)(getTime() - lastReverseTime) > enemyDistance/e.getVelocity() && enemyDistance > 200 && !flat) 
	    	flat = (++numBadHits/(getRoundNum()+1) > 1.1);
    }


	Point2D.Double projectMotion(Point2D.Double loc, double heading, double distance){
		
		return new Point2D.Double(loc.x + distance*Math.sin(heading), loc.y + distance*Math.cos(heading));			
	}
    private static double maxEscapeAngle(double bulletVelocity) {
		return 1.2*Math.asin(MAX_VELOCITY / bulletVelocity);
    }

	private double getDistanceToWalls(Point2D.Double location){
		
		return Math.min(Math.min(location.y, 600D - location.y), Math.min(location.x , 800D - location.x));
	}

    private static double bulletVelocity(double power) {
        return 20 - 3 * power;
    }

    private static double absoluteBearing(Point2D source, Point2D target) {
        return Math.atan2(target.getX() - source.getX(), target.getY() - source.getY());
    }

    public void onWin(WinEvent e) {
		//numWins++;
        saveFactors();
    }

    public void onDeath(DeathEvent e) {
		saveFactors();
    }

    void saveFactors() {
		//out.println(numWins/(getRoundNum() + 1));
        if (flat)
        try {
            ZipOutputStream zipout = new ZipOutputStream(new RobocodeFileOutputStream(getDataFile(enemyName)));
            zipout.putNextEntry(new ZipEntry("aimFactors"));
            ObjectOutputStream out = new ObjectOutputStream(zipout);
            out.writeObject(aimFactors);
            out.flush();
            zipout.closeEntry();
            out.close();
        }
        catch (Exception ex) {
        }
	}

    class Wave extends Condition {
	private long wTime;
	private double bulletVelocity;
	private double bearingDelta;
	private Point2D oldRLocation;
	private Point2D oldELocation;
	private double wBearingDirection;
	int[] wAimFactors;

	public Wave(double bulletVelocity) {
	    this.wTime = getTime();
	    this.bulletVelocity = bulletVelocity;
	    this.bearingDelta = deltaBearing;
	    this.oldRLocation = robotLocation;
	    this.oldELocation = enemyLocation;
	    this.wAimFactors = currentAimFactors;
	    this.wBearingDirection = bearingDirection;
	}

	public boolean test() {
	    if (bulletVelocity * (getTime() - wTime) > oldRLocation.distance(enemyLocation) - 10) {
		double bearingDiff = Utils.normalRelativeAngle(absoluteBearing(oldRLocation, enemyLocation) -
			absoluteBearing(oldRLocation, oldELocation));
		wAimFactors[(int)Math.min(Math.max(Math.round(((((wBearingDirection * bearingDiff) /
		    maxEscapeAngle(bulletVelocity)) + 1) / 2) * AIM_FACTORS - .5), 0), AIM_FACTORS - 1)]++;
		removeCustomEvent(this);
	    }
	    return false;
	}
    }
}
