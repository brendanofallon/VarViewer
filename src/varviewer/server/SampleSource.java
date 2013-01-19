package varviewer.server;

import java.util.List;

import varviewer.shared.SampleInfo;

/**
 * Objects which generate a list of SampleInfo objects should implement this interface
 * @author brendan
 *
 */
public interface SampleSource {

	/**
	 * True if this source contains a sample with the given id
	 * @param sampleID
	 * @return
	 */
	public boolean containsSample(String sampleID);
	
	/**
	 * Retrieve a list of all SampleInfo objects representing samples available through this source
	 * @return
	 */
	public List<SampleInfo> getSampleInfos();
	
	/**
	 * Obtain sampleInfo for the given sample 
	 * @param sampleID
	 * @return
	 */
	public SampleInfo getInfoForSample(String sampleID);
	
	/**
	 * Obtain sampleInfo for the given sample 
	 * @param sampleID
	 * @return
	 */
	public AbstractVariantServer getVariantServerForSample(String sampleID);
}
