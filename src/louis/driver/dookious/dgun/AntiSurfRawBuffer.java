package louis.driver.dookious.dgun;

import louis.driver.dookious.utils.WaveIndexSet;

public class AntiSurfRawBuffer extends AntiSurfBufferBase {
	private double[] _binsRaw;
	
	public AntiSurfRawBuffer(int bins) {
		super(bins);
		
		_binsRaw = new double[_bins + 1];
	}
	
	public double[] getStatArray(WaveIndexSet s) {
		return _binsRaw;
	}
}
