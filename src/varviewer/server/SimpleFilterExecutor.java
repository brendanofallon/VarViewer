package varviewer.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import varviewer.server.variant.VariantCollection;
import varviewer.shared.variant.Variant;
import varviewer.shared.variant.VariantFilter;

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
		
		Logger.getLogger(getClass()).info(passingVars.size() + " of " + vars.size() + " total vars passed filters");
		return passingVars;
	}


}
