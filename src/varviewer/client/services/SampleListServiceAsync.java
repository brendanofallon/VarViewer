package varviewer.client.services;

import varviewer.shared.SampleListResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SampleListServiceAsync {

	void getSampleList(AsyncCallback<SampleListResult> asyncCallback);
}
