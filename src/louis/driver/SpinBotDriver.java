/*******************************************************************************
 * Copyright (c) 2001-2012 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial implementation
 *     Flemming N. Larsen
 *     - Maintainance
 *******************************************************************************/
package louis.driver;

import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

import java.awt.*;


/**
 * SpinBot - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
 * <p/>
 * Moves in a circle, firing hard when an enemy is detected
 */
public class SpinBotDriver extends Driver {
	
	public SpinBotDriver(AdvancedRobot targetRobot) {
		super(targetRobot);
	}
	
	/**
	 * SpinBot's run method - Circle
	 */
	public void init() {
		// Set colors
		setBodyColor(Color.blue);
		setGunColor(Color.blue);
		setRadarColor(Color.black);
		setScanColor(Color.yellow);
		
		resetAngle();
	}

	public void loop() {
		// Tell the game that when we take move,
		// we'll also want to turn right... a lot.
		setTurnRight(1000);
		// Limit our speed to 5
		setMaxVelocity(5);
		// Start moving (and turning)
		ahead(100);
		// Repeat.
	}

	/**
	 * onScannedRobot: Fire hard!
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		fire(3);
	}

	/**
	 * onHitRobot:  If it's our fault, we'll stop turning and moving,
	 * so we need to turn again to keep spinning.
	 */
	public void onHitRobot(HitRobotEvent e) {
		if (e.getBearing() > -10 && e.getBearing() < 10) {
			fire(3);
		}
		if (e.isMyFault()) {
			turnRight(10);
		}
	}
}
