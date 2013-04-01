package varviewer.server;

import java.util.List;

import varviewer.server.variant.VariantCollection;
import varviewer.shared.variant.Variant;
import varviewer.shared.variant.VariantFilter;

/**
 * Basic interface for things that can actually execute one or more variant filters
 * @author brendan
 *
 */
public interface FilterExecutor {

	public List<Variant> filterAll(VariantCollection vars, List<VariantFilter> filters);

}
