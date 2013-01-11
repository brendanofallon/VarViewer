package varviewer.client;

import varviewer.shared.GeneInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GeneDetailServiceAsync {
	void getDetails(String geneID, AsyncCallback<GeneInfo> callback);
}
