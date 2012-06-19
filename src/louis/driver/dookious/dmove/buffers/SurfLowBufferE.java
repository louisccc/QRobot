package louis.driver.dookious.dmove.buffers;

import louis.driver.dookious.dmove.SurfLowBufferBase;
import louis.driver.dookious.utils.WaveIndexSet;

public class SurfLowBufferE extends SurfLowBufferBase {
	private double _binsLatDistWalls[][][][]; 

	public SurfLowBufferE(int bins) {
		super(bins);
		
		_binsLatDistWalls = new double
			[LATERAL_VELOCITY_SLICES.length + 1]
			[DISTANCE_SLICES.length + 1]
			[WALL_DISTANCE_SLICES.length + 1]
			[_bins + 1];
		
		_rollingDepth = 1;
	}
	
	public double[] getStatArray(WaveIndexSet s) {
		return _binsLatDistWalls
			[s.latVelIndex][s.distanceIndex][s.wallDistanceIndex];
	}

}
