package varviewer.shared.varFilters;

import java.io.Serializable;
import java.util.List;

import varviewer.shared.HasSamples;
import varviewer.shared.HasVariants;
import varviewer.shared.PedigreeSample;
import varviewer.shared.SampleInfo;
import varviewer.shared.variant.Annotation;
import varviewer.shared.variant.AnnotationIndex;
import varviewer.shared.variant.Variant;
import varviewer.shared.variant.VariantFilter;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A type of filter that excludes variants based on their presence and zygosity
 * in another sample. These are typically generated on the client, bundled with a 
 * VariantRequest, and the actual filtering is performed on the server
 * @author brendan
 *
 */
public class PedigreeFilter implements VariantFilter, Serializable, IsSerializable {

	private PedigreeSample sample = null; 
	private transient HasVariants relVars = null; //Don't try to serialize this
	private transient HasSamples varSource = null; //Dont serialize this, either
	
	public PedigreeFilter() {
		//required no-arg constructor
	}
	
	public PedigreeFilter(PedigreeSample sample) {
		this.sample = sample;
	}
	
	public SampleInfo getPedSampleID() {
		return sample.getRelSample();
	}
	
	public PedigreeSample getPedSample() {
		return sample;
	}
	/**
	 * Set the sample source that will be used to 
	 * @param source
	 */
	public void setVariantSource(HasSamples source) {
		this.varSource = source;
	}
	
	/**
	 * PedigreeFilters can also create a special annotation for Variants 
	 * @param vars
	 */
	public void applyAnnotations(int index, List<Variant> vars) {
		if (relVars == null) {
			initializeRelVars();
		}
		
		for(Variant var : vars) {
			Variant relVar = relVars.getVariant(var.getChrom(), var.getPos());
			String relZyg = null;
			if (relVar != null) {
				relZyg = relVar.getAnnotationStr("zygosity");
				var.addAnnotation(index, new Annotation(relZyg));
			}
			else {
				var.addAnnotation(index, new Annotation("Ref"));
			}
		}
	}
	
	@Override
	public boolean variantPasses(Variant var) {
		if (relVars == null) {
			initializeRelVars();
		}
		
		Variant relVar = relVars.getVariant(var.getChrom(), var.getPos());
		String relZyg = null;
		if (relVar != null) {
			relZyg = relVar.getAnnotationStr("zygosity");
		}
		
		if (sample.getoType() == PedigreeSample.OperationType.EXCLUDE) {
			if (relVar == null)
				return true;
			
			if (sample.getzType() == PedigreeSample.ZygType.ALL) {
				//these are exclusions, so variant does NOT pass if relvar exists, it DOES pass if relvar is null
				return relVar == null;
			}
			
			if (sample.getzType() == PedigreeSample.ZygType.HETS) {
				//We want to exclude Hets, so return false if zygosity is het
				return !relZyg.equalsIgnoreCase("het");
			}
			if (sample.getzType() == PedigreeSample.ZygType.HOMS) {
				return !relZyg.equalsIgnoreCase("hom");
			}
		}
		
		if (sample.getoType() == PedigreeSample.OperationType.INTERSECT) {
			if (relVar == null)
				return false;
			
			if (sample.getzType() == PedigreeSample.ZygType.ALL) {
				//these are intersections, so variant passes only if relvar exists
				return relVar != null;
			}
			
			if (sample.getzType() == PedigreeSample.ZygType.HETS) {
				//Variant passes only if relZyg is a het
				return relZyg.equalsIgnoreCase("het");
			}
			if (sample.getzType() == PedigreeSample.ZygType.HOMS) {
				return relZyg.equalsIgnoreCase("hom");
			}
		}
		
		//When in doubt, it passes
		return true;
	}

	private void initializeRelVars() {
		if (varSource == null)
			throw new IllegalArgumentException("Variant Source not initialized for PedigreeFilter");
		relVars = varSource.getHasVariantsForSample(getPedSampleID());
	}

	public boolean equals(Object o) {
		if (o.getClass() != this.getClass()) {
			return false;
		}
		PedigreeFilter pf = (PedigreeFilter)o;
		return pf.getPedSample().equals(sample);
	}

	@Override
	public String getUserDescription() {
		//TODO : Should have a user-readable description for filtering text
		return null;
	}

	@Override
	public void setAnnotationIndex(AnnotationIndex index) {
		//I dont actually think we use this.
	}
}
