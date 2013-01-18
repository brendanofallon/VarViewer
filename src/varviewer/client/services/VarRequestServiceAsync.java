package varviewer.client.services;

import java.util.List;

import varviewer.shared.Variant;
import varviewer.shared.VariantRequest;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous implementation of variant request service
 * @author brendan
 *
 */
public interface VarRequestServiceAsync {
	void queryVariant(VariantRequest req, AsyncCallback<List<Variant>> callback)
			throws IllegalArgumentException;
}
