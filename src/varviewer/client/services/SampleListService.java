package varviewer.client.services;

import varviewer.shared.SampleListResult;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("samplelist")
public interface SampleListService extends RemoteService {

	SampleListResult getSampleList();
	
}
