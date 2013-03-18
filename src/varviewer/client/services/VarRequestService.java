package varviewer.client.services;

import varviewer.shared.VariantRequest;
import varviewer.shared.VariantRequestResult;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("varrequest")
public interface VarRequestService extends RemoteService {
	VariantRequestResult queryVariant(VariantRequest req) throws IllegalArgumentException;
}
