package louis.driver.dookious.utils;

import robocode.Bullet;
import louis.driver.dookious.utils.Wave;

public interface BulletHitRegister {
	public void registerBulletHit(Bullet bullet, Wave closestWave,
		long hitTime);
}
