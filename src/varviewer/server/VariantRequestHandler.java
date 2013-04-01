package varviewer.server;

import varviewer.shared.variant.VariantRequest;
import varviewer.shared.variant.VariantRequestResult;

public interface VariantRequestHandler {

	/**
	 * Obtain a list of variants associated with the parameters outlined by the given
	 * request object (sample name, intervals, filters, and annotations)
	 * @param req
	 * @return
	 */
	public VariantRequestResult queryVariant(VariantRequest req);

}
