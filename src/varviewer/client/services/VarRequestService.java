package varviewer.client.services;

import varviewer.shared.variant.VariantRequest;
import varviewer.shared.variant.VariantRequestResult;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("varrequest")
public interface VarRequestService extends RemoteService {
	VariantRequestResult queryVariant(VariantRequest req) throws IllegalArgumentException;
}
