package louis.driver.dookious.dmove.buffers;

import louis.driver.dookious.dmove.SurfLowBufferBase;
import louis.driver.dookious.utils.WaveIndexSet;

public class SurfLowBufferJ extends SurfLowBufferBase {
	private double _binsDist[][]; 

	public SurfLowBufferJ(int bins) {
		super(bins);
		
		_binsDist = new double
			[DISTANCE_SLICES.length + 1]
			[_bins + 1];
		
		_rollingDepth = 1;
	}
	
	public double[] getStatArray(WaveIndexSet s) {
		return _binsDist
			[s.distanceIndex];

	}

}
