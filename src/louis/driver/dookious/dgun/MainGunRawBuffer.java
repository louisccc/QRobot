package louis.driver.dookious.dgun;

import louis.driver.dookious.utils.WaveIndexSet;

public class MainGunRawBuffer extends MainBufferBase {
	private double[] _binsRaw;
	
	public MainGunRawBuffer(int bins) {
		super(bins);
		
		_binsRaw = new double[_bins + 1];
	}
	
	public double[] getStatArray(WaveIndexSet s) {
		return _binsRaw;
	}
}
