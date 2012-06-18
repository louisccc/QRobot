/*
 Evolved in generation 124
Average fitness: 0.019238399846092803
Single fitness values:
 1. 0.09619199923046401
 2. 0.0
 3. 0.0
 4. 0.0
 5. 0.0
*/
package louis.driver;
import robocode.*;
import java.awt.Color;
import java.awt.geom.*;
import java.util.*;public class AspirantDriver extends Driver
{
 private double global1 = 3.0;
static double lastEnemyHeading;
static double radarturn=1;
static int shots=0;
static int misses=0;
static int hitBullet=0;
static int hitEnemy=0;
static int hitOwn=0;
static int enemyShots=0;
static double enemyFirePower;
static int i1=0;
static int i2=2;
static int i3=1;
static boolean b1=false;
static boolean b2=true;
static double x = 2.0;
static double y = 1.0;
static double z = 3.0;
static double enemyX = 0;
static double enemyY = 0;
static double distance = -1.0;
static double[] memory=new double[50];
static double[] memory2=new double[50];
static double[][] memory3=new double[10][10];
static Point2D.Double robotLocation = new Point2D.Double();
static Point2D.Double enemyLocation = new Point2D.Double();
static Point2D.Double tempLocation = new Point2D.Double();
static final double BATTLE_FIELD_WIDTH = 800;
static final double BATTLE_FIELD_HEIGHT = 600;
static final double WALL_MARGIN = 18;
static Rectangle2D.Double FIREFIELD = new Rectangle2D.Double(WALL_MARGIN, WALL_MARGIN,
  BATTLE_FIELD_WIDTH - WALL_MARGIN,BATTLE_FIELD_HEIGHT - WALL_MARGIN);
static byte moveDirection = 1;
	
	public AspirantDriver(AdvancedRobot targetRobot) {
		super(targetRobot);
	}
	
void toLocation(double angle, double length, Point2D sourceLocation, Point2D targetLocation) {targetLocation.setLocation(sourceLocation.getX() + Math.sin(angle) * length, sourceLocation.getY() + Math.cos(angle) * length);}double normalize(double angle) {
return angle - 2 * Math.PI * Math.floor((angle + Math.PI) / (2 * Math.PI));
}
static double absoluteBearing(Point2D source, Point2D target) {
 return Math.atan2(target.getX() - source.getX(),
 target.getY() - source.getY());
}
Point2D.Double projectMotion(Point2D.Double loc, double heading, double distance){
 return new Point2D.Double(loc.x + distance*Math.sin(heading), loc.y + distance*Math.cos(heading));
}
private double getDistanceToWalls(Point2D.Double location){
 return Math.min(Math.min(location.y, 600D - location.y), Math.min(location.x , 800D - location.x));
}
	public void init() {
setAdjustGunForRobotTurn(true);setAdjustRadarForGunTurn(true); // Initialization stuff
 setColors(Color.orange,Color.blue,Color.green);
 setAdjustGunForRobotTurn(true);
 setAdjustRadarForGunTurn(true);
for(int idx=0;idx<memory.length;idx++) {
   memory[idx] = 0.0d;}
for(int idx=0;idx<memory2.length;idx++) {
   memory2[idx] = 0.0d;}
for(int idx1=0;idx1<memory3.length;idx1++) {
for(int idx2=0;idx2<memory3[0].length;idx2++) {
   memory3[idx1][idx2] = 0.0d; }
}
  }
	public void loop() {
		ahead(100 * moveDirection);if (getRadarTurnRemainingRadians() == 0){setTurnRadarRightRadians(Math.PI / 4);}
	}
 public void onScannedRobot(ScannedRobotEvent e) {
 x = getHeading()+e.getBearing();
 y = getRadarTurnRemaining();
 distance = e.getDistance();
 robotLocation.setLocation(getX(), getY());
 Rectangle2D fieldRectangle = new Rectangle2D.Double(WALL_MARGIN,
 WALL_MARGIN, BATTLE_FIELD_WIDTH - WALL_MARGIN * 2,
 BATTLE_FIELD_HEIGHT - WALL_MARGIN * 2);
 double enemyAbsoluteBearing = getHeadingRadians() + e.getBearingRadians();
 toLocation(enemyAbsoluteBearing, distance, robotLocation, enemyLocation);
 enemyX = enemyLocation.getX();
 enemyY = enemyLocation.getY();
{
setTurnRadarLeft(e.getDistance());
setBack(e.getBearing());
shots++;setFire(getRadarHeading());

// TurnGunRightEnemy
setTurnGunRight(getHeading() + e.getBearing() - getGunHeading());
}

}
public void onHitByBullet(HitByBulletEvent e) {
hitOwn++;enemyShots++;
enemyFirePower = e.getPower();
{
shots++;setFire((3.141592653589793));
setTurnRadarRight((int)getTime());
}

}
 public void onHitWall(HitWallEvent e) {
moveDirection *= -1;
{
shots++;setFire((18.39826202392578d));
setBack(getHeading());
}

}
 public void onHitRobot(HitRobotEvent e) {
moveDirection *= -1;
;
}
public void onSkippedTurn(SkippedTurnEvent event) {

}
public void onBulletHit(BulletHitEvent e){
hitEnemy++;
}
public void onBulletMissed(BulletMissedEvent e){
misses++;
}
public void onBulletHitBullet(BulletHitBulletEvent e){
misses++;
hitBullet++;
enemyShots++;
}
}
