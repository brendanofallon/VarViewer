package varviewer.client;

import java.util.List;

import varviewer.shared.Variant;
import varviewer.shared.VariantRequest;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("varrequest")
public interface VarRequestService extends RemoteService {
	List<Variant> queryVariant(VariantRequest req) throws IllegalArgumentException;
}
