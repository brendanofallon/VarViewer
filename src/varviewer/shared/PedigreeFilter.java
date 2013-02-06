package varviewer.shared;

import varviewer.server.SampleSource;
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
	private transient SampleSource varSource = null; //Dont serialize this, either
	
	public PedigreeFilter() {
		//required no-arg constructor
	}
	
	public PedigreeFilter(PedigreeSample sample) {
		this.sample = sample;
	}
	
	public String getPedSampleID() {
		return sample.getRelId();
	}
	
	/**
	 * Set the sample source that will be used to 
	 * @param source
	 */
	public void setVariantSource(SampleSource source) {
		this.varSource = source;
	}
	
	@Override
	public boolean variantPasses(Variant var) {
		if (relVars == null) {
			initializeRelVars();
		}
		
		Variant relVar = relVars.getVariant(var.getChrom(), var.getPos());
		String relZyg = null;
		if (relVar != null) {
			relZyg = relVar.getAnnotation("zygosity");
			var.addAnnotation(sample.getRelId() + "-zygosity", relZyg);
		}
		else {
			var.addAnnotation(sample.getRelId() + "-zygosity", "Ref");
		}
		
//		if (sample.getoType() == PedigreeSample.OperationType.EXCLUDE) {
//			if (sample.getzType() == PedigreeSample.ZygType.ALL) {
//				//these are exclusions, so variant does NOT pass if relvar exists, it DOES pass if relvar is null
//				return relVar == null;
//			}
//			
//			if (sample.getzType() == PedigreeSample.ZygType.HETS) {
//				//We want to exclude Hets, so return false if zygosity is het
//				return !relZyg.equalsIgnoreCase("het");
//			}
//			if (sample.getzType() == PedigreeSample.ZygType.HOMS) {
//				return !relZyg.equalsIgnoreCase("hom");
//			}
//		}
//		
//		if (sample.getoType() == PedigreeSample.OperationType.INTERSECT) {
//			if (sample.getzType() == PedigreeSample.ZygType.ALL) {
//				//these are intersections, so variant passes only if relvar exists
//				return relVar != null;
//			}
//			
//			if (sample.getzType() == PedigreeSample.ZygType.HETS) {
//				//Variant passes only if relZyg is a het
//				return relZyg.equalsIgnoreCase("het");
//			}
//			if (sample.getzType() == PedigreeSample.ZygType.HOMS) {
//				return relZyg.equalsIgnoreCase("hom");
//			}
//		}
		
		//When in doubt, it passes
		return true;
	}

	private void initializeRelVars() {
		if (varSource == null)
			throw new IllegalArgumentException("Variant Source not initialized for PedigreeFilter");
		relVars = varSource.getVariantsForSample(getPedSampleID());
	}

}
