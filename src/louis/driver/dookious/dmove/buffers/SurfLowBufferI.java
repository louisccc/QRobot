package louis.driver.dookious.dmove.buffers;

import louis.driver.dookious.dmove.SurfLowBufferBase;
import louis.driver.dookious.utils.WaveIndexSet;

public class SurfLowBufferI extends SurfLowBufferBase {
	private double _binsLatWalls[][][]; 

	public SurfLowBufferI(int bins) {
		super(bins);
		
		_binsLatWalls = new double
			[LATERAL_VELOCITY_SLICES.length + 1]
			[WALL_DISTANCE_SLICES.length + 1]
			[_bins + 1];
		
		_rollingDepth = 1.25;
	}
	
	public double[] getStatArray(WaveIndexSet s) {
		return _binsLatWalls
			[s.latVelIndex][s.wallDistanceIndex];
	}

}
