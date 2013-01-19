package varviewer.client.services;

import java.util.List;

import varviewer.shared.SampleInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SampleListServiceAsync {

	void getSampleList(AsyncCallback<List<SampleInfo>> asyncCallback);
}
