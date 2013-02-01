package varviewer.shared;

import varviewer.server.variant.VariantCollection;

/**
 * A type of filter that excludes variants based on their presence and zygosity
 * in another sample. These are typically generated on the client, bundled with a 
 * VariantRequest, and the actual filtering is performed on the server
 * @author brendan
 *
 */
public class PedigreeFilter implements VariantFilter {

	private PedigreeSample sample = null;
	private transient VariantCollection relVars = null; //Don't try to serialize this
	
	public PedigreeFilter() {
		//required no-arg constructor
	}
	
	public PedigreeFilter(PedigreeSample sample) {
		this.sample = sample;
	}
	
	public String getPedSampleID() {
		return sample.getRelId();
	}
	
	
	
	@Override
	public boolean variantPasses(Variant var) {
		if (relVars == null) {
			initializeRelVars();
		}
		//TODO Query target variant collection and see if the variant 'passes' based on sample info
		return false;
	}

	private void initializeRelVars() {
		
	}

}
