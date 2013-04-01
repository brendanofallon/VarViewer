package varviewer.server;

import java.util.List;

import varviewer.shared.variant.Variant;
import varviewer.shared.variant.VariantRequest;

/**
 * Base class of things that can handle requests for list of Variants
 * @author brendan
 *
 */
public abstract class AbstractVariantServer {
	
	/**
	 * Retrieve all variants from the samples and intervals defined by the
	 * VariantRequest object and return them in a list. 
	 * @param req
	 * @return
	 */
	public abstract List<Variant> getVariants(VariantRequest req);

}
