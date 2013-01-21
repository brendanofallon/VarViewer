package varviewer.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import varviewer.server.variant.AnnotatedCSVReader;
import varviewer.server.variant.VariantCollection;
import varviewer.shared.Variant;
import varviewer.shared.VariantFilter;
import varviewer.shared.VariantRequest;

/**
 * A VariantServer that maintains the most recently requested variants in memory
 * for faster response times. 
 * @author brendan
 *
 */
public class CachingVariantServer extends AbstractVariantServer {

	private String currentSampleID = null; //
	private VariantCollection currentVariants = null;
	private SampleSource source;
	
	public CachingVariantServer(SampleSource sampleSource) {
		this.source = sampleSource;
	}
	
	
	@Override
	public List<Variant> getVariants(VariantRequest req) {
		if (currentSampleID != null && req.getSampleIDs().contains(currentSampleID)) {
			List<Variant> vars = currentVariants.getVariantsInIntervals(req.getIntervals());
			if (req.getFilters() != null && req.getFilters().size()>0) {
				vars = applyFilters(vars, req.getFilters());
			}
			return vars;
		}
		else {
			loadVariants(req.getSampleIDs());
			
			//IO errors will cause currentVariants to be null, so check for this here
			if (currentVariants != null) {
				List<Variant> vars = currentVariants.getVariantsInIntervals(req.getIntervals());
				if (req.getFilters() != null && req.getFilters().size()>0) {
					vars = applyFilters(vars, req.getFilters());
				}
				return vars;
			}
			return null;
		}
		
	}

	private void loadVariants(List<String> ids) {
		for(String id : ids) {
			currentVariants = source.getVariantsForSample(id);
			currentSampleID = id;
			return;
		}
	}
	
	/**
	 * Returns a new list of variants containing only those variants that pass ALL 
	 * filters in the list
	 * @param vars
	 * @param filters
	 * @return
	 */
	private static List<Variant> applyFilters(List<Variant> vars, List<VariantFilter> filters) {
		List<Variant> passingVars = new ArrayList<Variant>(1024);
		for(Variant var : vars) {
			boolean passes = true;
			for(VariantFilter filter : filters) {
				if (! filter.variantPasses(var)) {
					passes = false;
					break;
				}
			}
			if (passes)
				passingVars.add(var);
		}
		
		return passingVars;
	}

}
