package louis.driver.dookious.dmove.buffers;

import louis.driver.dookious.dmove.SurfLowBufferBase;
import louis.driver.dookious.utils.WaveIndexSet;
import louis.driver.dookious.utils.DUtils;

public class SurfLowBufferL extends SurfLowBufferBase {
	private double _binsRaw[]; 

	public SurfLowBufferL(int bins) {
		super(bins);
		
		_binsRaw = new double
			[_bins + 1];
		
        int middleBin = (_bins-1)/2;
        for (int x = 0; x < _bins; x++) {
        	_binsRaw[x+1] = 0.6 / (DUtils.square(x-middleBin) + 1);
        }
		_binsRaw[0]++;
		
		_rollingDepth = 1;
	}
	
	public double[] getStatArray(WaveIndexSet s) {
		return _binsRaw;
	}

}
