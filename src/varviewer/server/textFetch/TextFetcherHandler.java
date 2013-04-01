package varviewer.server.textFetch;

import varviewer.shared.TextFetchResult;

/**
 * These objects can be the implementation for the TextFetchServiceImpl
 * @author brendan
 *
 */
public interface TextFetcherHandler {

	public TextFetchResult fetchText(String id);
	
}
