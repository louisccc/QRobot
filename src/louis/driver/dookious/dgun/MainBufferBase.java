package louis.driver.dookious.dgun;

import louis.driver.dookious.utils.StatBuffer;
import louis.driver.dookious.utils.WaveIndexSet;

abstract public class MainBufferBase extends StatBuffer {

	public MainBufferBase(int bins) {
		super(bins);
		
		_rollingDepth = 10000;
		_firingWaveWeight = 1;
		_nonFiringWaveWeight = 0.2;
	}
	
	public double getBinScore(WaveIndexSet s, int gfIndex) {
		double[] thisBuffer = getStatArray(s);
		return thisBuffer[gfIndex+1] * Math.min(25, thisBuffer[0]);
	}
}
