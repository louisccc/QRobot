package louis.driver.dookious.dmove.buffers;

import louis.driver.dookious.dmove.SurfHighBufferBase;
import louis.driver.dookious.utils.WaveIndexSet;

public class SurfHighBufferV extends SurfHighBufferBase {
	private double _binsWallsRWallsDlftVchangeSmst[][][][][][]; 

	public SurfHighBufferV(int bins) {
		super(bins);
		
		_binsWallsRWallsDlftVchangeSmst = new double
			[WALL_DISTANCE_SLICES.length + 1]
			[WALL_REVERSE_SLICES.length + 1]
			[DLFT_SLICES.length + 1]
			[VCHANGE_TIME_SLICES.length + 1]
			[SINCEMAX_TIME_SLICES.length + 1]
			[_bins + 1];
	
		_rollingDepth = 0.5;

	}
	
	public double[] getStatArray(WaveIndexSet s) {
		return _binsWallsRWallsDlftVchangeSmst
			[s.wallDistanceIndex][s.wallReverseIndex][s.dlftIndex]
			[s.vChangeIndex][s.sinceMaxIndex];
	}

}