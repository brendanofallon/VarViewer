package varviewer.client;

import varviewer.shared.GeneInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("genedetails")
public interface GeneDetailService extends RemoteService {
	GeneInfo getDetails(String geneID);
}
