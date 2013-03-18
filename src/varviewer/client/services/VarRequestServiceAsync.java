package varviewer.client.services;

import varviewer.shared.VariantRequest;
import varviewer.shared.VariantRequestResult;

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
