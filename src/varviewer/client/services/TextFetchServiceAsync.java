package varviewer.client.services;

import varviewer.shared.TextFetchResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TextFetchServiceAsync {
	
	void fetchText(String id, AsyncCallback<TextFetchResult> result);
	
}
