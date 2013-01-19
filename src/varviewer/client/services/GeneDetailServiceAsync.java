package varviewer.client.services;

import varviewer.shared.GeneInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GeneDetailServiceAsync {
	void getDetails(String geneID, AsyncCallback<GeneInfo> callback);
}
