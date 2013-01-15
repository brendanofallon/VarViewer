package varviewer.server;

import java.util.List;

import varviewer.client.VarRequestService;
import varviewer.shared.Variant;
import varviewer.shared.VariantRequest;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the variant request service. All requests are currently honored
 * by a 'SimpleVariantServer' which is used for testing
 * @author brendan
 *
 */
public class VarRequestServiceImpl extends RemoteServiceServlet implements VarRequestService {

	AbstractVariantServer variantSource = new CachingVariantServer();
	
	@Override
	public List<Variant> queryVariant(VariantRequest req)
			throws IllegalArgumentException {

		return variantSource.getVariants(req);
	}

}
