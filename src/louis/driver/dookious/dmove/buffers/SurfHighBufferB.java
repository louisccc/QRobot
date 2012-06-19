package louis.driver.dookious.dmove.buffers;

import louis.driver.dookious.dmove.SurfHighBufferBase;
import louis.driver.dookious.utils.WaveIndexSet;

public class SurfHighBufferB extends SurfHighBufferBase {
	private double _binsAdvLatDistAccelWallsRWallsVchange[][][][][][][][]; 

	public SurfHighBufferB(int bins) {
		super(bins);
		
		_binsAdvLatDistAccelWallsRWallsVchange = new double
			[ADVANCING_VELOCITY_SLICES.length + 1]
			[LATERAL_VELOCITY_SLICES.length + 1]
			[DISTANCE_SLICES.length + 1]
			[ACCEL_SLICES]
			[WALL_DISTANCE_SLICES.length + 1]
			[WALL_REVERSE_SLICES.length + 1]
			[VCHANGE_TIME_SLICES.length + 1]
			[_bins + 1];

		_rollingDepth = 0.7;
		
	}
	
	public double[] getStatArray(WaveIndexSet s) {
		return _binsAdvLatDistAccelWallsRWallsVchange
			[s.advVelIndex][s.latVelIndex][s.distanceIndex][s.accelIndex]
			[s.wallDistanceIndex][s.wallReverseIndex][s.vChangeIndex];
	}

}