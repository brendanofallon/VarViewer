package varviewer.client.services;

import varviewer.shared.TextFetchResult;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * A general service for grabbing lines of text from a server-side file
 * @author brendan
 *
 */
@RemoteServiceRelativePath("textfetch")
public interface TextFetchService extends RemoteService {

	TextFetchResult fetchText(String id);
	
}
