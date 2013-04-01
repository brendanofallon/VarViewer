package varviewer.client.services;

import varviewer.shared.variant.VariantRequest;
import varviewer.shared.variant.VariantRequestResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous implementation of variant request service
 * @author brendan
 *
 */
public interface VarRequestServiceAsync {
	void queryVariant(VariantRequest req, AsyncCallback<VariantRequestResult> callback)
			throws IllegalArgumentException;
}
