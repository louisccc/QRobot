package louis.driver.dookious.dmove.buffers;

import louis.driver.dookious.dmove.SurfNonFiringBufferBase;
import louis.driver.dookious.utils.WaveIndexSet;

public class SurfNonFiringBufferC extends SurfNonFiringBufferBase {
	private double _binsLatDistAccelVchange[][][][][]; 

	public SurfNonFiringBufferC(int bins) {
		super(bins);
		
		_binsLatDistAccelVchange = new double
			[LATERAL_VELOCITY_SLICES.length + 1]
			[DISTANCE_SLICES.length + 1]
			[ACCEL_SLICES]
			[VCHANGE_TIME_SLICES.length + 1]
			[_bins + 1];
		
	}
	
	public double[] getStatArray(WaveIndexSet s) {
		return _binsLatDistAccelVchange
			[s.latVelIndex][s.distanceIndex][s.accelIndex][s.vChangeIndex];
	}

}