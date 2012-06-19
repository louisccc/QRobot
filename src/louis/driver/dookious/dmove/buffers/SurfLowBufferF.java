package louis.driver.dookious.dmove.buffers;

import louis.driver.dookious.dmove.SurfLowBufferBase;
import louis.driver.dookious.utils.WaveIndexSet;

public class SurfLowBufferF extends SurfLowBufferBase {
	private double _binsLatDistVchange[][][][]; 

	public SurfLowBufferF(int bins) {
		super(bins);
		
		_binsLatDistVchange = new double
			[LATERAL_VELOCITY_SLICES.length + 1]
			[DISTANCE_SLICES.length + 1]
			[VCHANGE_TIME_SLICES.length + 1]
			[_bins + 1];
		
		_rollingDepth = 2;
	}
	
	public double[] getStatArray(WaveIndexSet s) {
		return _binsLatDistVchange
			[s.latVelIndex][s.distanceIndex][s.vChangeIndex];
	}

}
