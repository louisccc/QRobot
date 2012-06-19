package louis.driver.dookious.dmove.buffers;

import louis.driver.dookious.dmove.SurfLowBufferBase;
import louis.driver.dookious.utils.WaveIndexSet;

public class SurfLowBufferK extends SurfLowBufferBase {
	private double _binsLat[][]; 

	public SurfLowBufferK(int bins) {
		super(bins);
		
		_binsLat = new double
			[LATERAL_VELOCITY_SLICES.length + 1]
			[_bins + 1];
		
		_rollingDepth = 1;
	}
	
	public double[] getStatArray(WaveIndexSet s) {
		return _binsLat
			[s.latVelIndex];
	}

}
