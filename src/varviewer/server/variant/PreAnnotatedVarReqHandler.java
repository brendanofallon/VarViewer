package varviewer.server.variant;

import java.util.Date;
import java.util.List;

import varviewer.server.CachingSampleSource;
import varviewer.server.FilterExecutor;
import varviewer.server.SampleSource;
import varviewer.server.SimpleFilterExecutor;
import varviewer.server.VariantRequestHandler;
import varviewer.shared.Variant;
import varviewer.shared.VariantRequest;
import varviewer.shared.VariantRequestResult;

public class PreAnnotatedVarReqHandler implements VariantRequestHandler {

	SampleSource variantSource = null;
	
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
		Date begin = new Date();
		
		
		Date init = new Date();
		System.out.println("Time to initialize: " + (init.getTime()-begin.getTime())/1000.0);
		
		//First: Obtain the "raw" list of variants, unfiltered and (mostly) unannotated
		VariantCollection vars = variantSource.getVariantsForSample(req.getSampleIDs().get(0));

		Date readVars = new Date();
		System.out.println("Time to read: " + (readVars.getTime()-init.getTime())/1000.0);
	
		Date annotate = new Date();
		System.out.println("Time to annotate: " + (annotate.getTime()-readVars.getTime())/1000.0);
		
		//Third : Apply filters to the variants and return only those passing all filters
		FilterExecutor filterExec = new SimpleFilterExecutor();
		List<Variant> passingVars = filterExec.filterAll(vars, req.getFilters());
		
		Date filter = new Date();
		System.out.println("Time to filter: " + (filter.getTime()-annotate.getTime())/1000.0);
		
		VariantRequestResult result = new VariantRequestResult();
		result.setSampleID(req.getSampleIDs().get(0));
		result.setVars(passingVars);
		return result;
	}


}
