package louis.driver.dookious.dmove.buffers;

import louis.driver.dookious.dmove.SurfLowBufferBase;
import louis.driver.dookious.utils.WaveIndexSet;

public class SurfLowBufferG extends SurfLowBufferBase {
	private double _binsLatDistAccel[][][][]; 

	public SurfLowBufferG(int bins) {
		super(bins);
		
		_binsLatDistAccel = new double
			[LATERAL_VELOCITY_SLICES.length + 1]
			[DISTANCE_SLICES.length + 1]
			[ACCEL_SLICES]
			[_bins + 1];
		
		_rollingDepth = 1;
	}
	
	public double[] getStatArray(WaveIndexSet s) {
		return _binsLatDistAccel
			[s.latVelIndex][s.distanceIndex][s.accelIndex];
	}

}
