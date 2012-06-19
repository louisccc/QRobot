package louis.driver.dookious.dmove.buffers;

import louis.driver.dookious.dmove.SurfHighBufferBase;
import louis.driver.dookious.utils.WaveIndexSet;

public class SurfHighBufferL extends SurfHighBufferBase {
	private double _binsLatWallsRWallsDltftSzt[][][][][][]; 

	public SurfHighBufferL(int bins) {
		super(bins);
		
		_binsLatWallsRWallsDltftSzt = new double
			[LATERAL_VELOCITY_SLICES.length + 1]
			[WALL_DISTANCE_SLICES.length + 1]
			[WALL_REVERSE_SLICES.length + 1]
			[DLTFT_SLICES.length + 1]
			[SINCEZERO_TIME_SLICES.length + 1]
			[_bins + 1];
		
		_rollingDepth = 10;

	}
	
	public double[] getStatArray(WaveIndexSet s) {
		return _binsLatWallsRWallsDltftSzt
			[s.latVelIndex][s.wallDistanceIndex][s.wallReverseIndex]
			[s.dltftIndex][s.sinceZeroIndex];
	}

}