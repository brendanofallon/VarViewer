package varviewer.client.services;

import java.util.List;

import varviewer.shared.SampleInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("samplelist")
public interface SampleListService extends RemoteService {

	List<SampleInfo> getSampleList();
	
}
