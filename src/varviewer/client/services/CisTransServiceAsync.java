package varviewer.client.services;

import varviewer.shared.bcrabl.CisTransRequest;
import varviewer.shared.bcrabl.CisTransResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CisTransServiceAsync {
	void computeCisTrans(CisTransRequest req, AsyncCallback<CisTransResult> resultCallback);
}
