package louis.driver.dookious.dmove.buffers;

import louis.driver.dookious.dmove.SurfHighBufferBase;
import louis.driver.dookious.utils.WaveIndexSet;

public class SurfHighBufferJ extends SurfHighBufferBase {
	private double _binsDistWallsDletAccelSzt[][][][][][]; 

	public SurfHighBufferJ(int bins) {
		super(bins);
		
		_binsDistWallsDletAccelSzt = new double
			[DISTANCE_SLICES.length + 1]
			[WALL_DISTANCE_SLICES.length + 1]
			[DLET_SLICES.length + 1]
			[ACCEL_SLICES]
			[SINCEZERO_TIME_SLICES.length + 1]
			[_bins + 1];
		
		_rollingDepth = 3;

	}
	
	public double[] getStatArray(WaveIndexSet s) {
		return _binsDistWallsDletAccelSzt
			[s.distanceIndex][s.wallDistanceIndex]
			[s.dletIndex][s.accelIndex][s.sinceZeroIndex];
	}

}