package varviewer.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import varviewer.server.variant.VariantCollection;
import varviewer.shared.Variant;
import varviewer.shared.VariantFilter;

/**
 * The simplest type of filter, just look at each variant and, if it passes, add it to the outgoing list.
 * Other filter executors may use multiple threads or filter prioritizatoin to speed things up
 * @author brendan
 *
 */
public class SimpleFilterExecutor implements FilterExecutor {

	@Override
	public List<Variant> filterAll(VariantCollection vars,	List<VariantFilter> filters) {
		List<Variant> passingVars = new ArrayList<Variant>(1024);
		
		//Kind of a hack here... pedigree-based filters need to be 'initialized' with a SampleSource
		//before they work, right now we do this here. 
//		for(VariantFilter filter : filters) {
//			if (filter instanceof PedigreeFilter) {
//				PedigreeFilter pedFilter = (PedigreeFilter)filter;
//				pedFilter.setVariantSource(source);
//			}
//		}
		
		for(String contig : vars.getContigs()) {
			for(Variant var : vars.getVariantsForContig(contig)) {
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
		}
		
		//Similar hack here, PedigreeFilters also apply an annotation, but they need to be told
		//to do so to a given list of variants. We do this here so they don't waste time annotating
		//variants that will be filtered out, but this functionality should be encapsulated somewhere
		//else at some point
//		for(VariantFilter filter : filters) {
//			if (filter instanceof PedigreeFilter) {
//				PedigreeFilter pedFilter = (PedigreeFilter)filter;
//				pedFilter.applyAnnotations(passingVars);
//			}
//		}
		
		Logger.getLogger(getClass()).info(passingVars.size() + " of " + vars.size() + " total vars passed filters");
		return passingVars;
	}


}
