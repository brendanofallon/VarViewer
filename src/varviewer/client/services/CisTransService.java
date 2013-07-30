package varviewer.client.services;

import varviewer.shared.bcrabl.CisTransRequest;
import varviewer.shared.bcrabl.CisTransResult;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("cistrans")
public interface CisTransService extends RemoteService {
	CisTransResult computeCisTrans(CisTransRequest req);
}
