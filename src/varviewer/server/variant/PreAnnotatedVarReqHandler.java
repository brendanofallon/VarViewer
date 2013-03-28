package varviewer.server.variant;

import java.util.Date;
import java.util.List;

import varviewer.server.CachingSampleSource;
import varviewer.server.FilterExecutor;
import varviewer.server.SampleSource;
import varviewer.server.SimpleFilterExecutor;
import varviewer.server.VariantRequestHandler;
import varviewer.shared.PedigreeFilter;
import varviewer.shared.Variant;
import varviewer.shared.VariantFilter;
import varviewer.shared.VariantRequest;
import varviewer.shared.VariantRequestResult;

public class PreAnnotatedVarReqHandler implements VariantRequestHandler {

	SampleSource variantSource = null;
	boolean profile = false;
	
	public SampleSource getVariantSource() {
		return variantSource;
	}

	public void setVariantSource(SampleSource variantSource) {
		if (! (variantSource instanceof CachingSampleSource)) {
			this.variantSource = new CachingSampleSource(variantSource);
		}
		else {
			this.variantSource = variantSource;
		}
	}

	@Override
	public VariantRequestResult queryVariant(VariantRequest req) {
		if (profile) {
			Date begin = new Date();

			//First: Obtain the "raw" list of variants, unfiltered 
			VariantCollection vars = variantSource.getVariantsForSample(req.getSampleIDs().get(0));

			Date readVars = new Date();
			System.out.println("Time to read: " + (readVars.getTime()-begin.getTime())/1000.0);

			//Third : Apply filters to the variants and return only those passing all filters
			FilterExecutor filterExec = new SimpleFilterExecutor();
			List<Variant> passingVars = filterExec.filterAll(vars, req.getFilters());

			Date filter = new Date();
			System.out.println("Time to filter: " + (filter.getTime()-readVars.getTime())/1000.0);

			VariantRequestResult result = new VariantRequestResult();
			result.setSampleID(req.getSampleIDs().get(0));
			result.setVars(passingVars);
			return result;
		}
		else {
			VariantCollection vars = variantSource.getVariantsForSample(req.getSampleIDs().get(0));

			//Kind of a hack here... pedigree-based filters need to be 'initialized' with a SampleSource
			//before they work, right now we do this here. 
			for(VariantFilter filter : req.getFilters()) {
				if (filter instanceof PedigreeFilter) {
					PedigreeFilter pedFilter = (PedigreeFilter)filter;
					pedFilter.setVariantSource(variantSource);
				}
			}
			
			FilterExecutor filterExec = new SimpleFilterExecutor();
			List<Variant> passingVars = filterExec.filterAll(vars, req.getFilters());

			//Similar hack here, PedigreeFilters also apply an annotation, but they need to be told
			//to do so to a given list of variants. We do this here so they don't waste time annotating
			//variants that will be filtered out, but this functionality should be encapsulated somewhere
			//else at some point
			for(VariantFilter filter : req.getFilters()) {
				if (filter instanceof PedigreeFilter) {
					PedigreeFilter pedFilter = (PedigreeFilter)filter;
					pedFilter.applyAnnotations(passingVars);
				}
			}
			
			VariantRequestResult result = new VariantRequestResult();
			result.setSampleID(req.getSampleIDs().get(0));
			result.setVars(passingVars);
			return result;
		}
	}


}
