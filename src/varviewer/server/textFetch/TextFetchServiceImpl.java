package varviewer.server.textFetch;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import varviewer.client.services.TextFetchService;
import varviewer.shared.TextFetchResult;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TextFetchServiceImpl extends RemoteServiceServlet implements TextFetchService {

	TextFetcherHandler handler = null;
	
	@Override
	public TextFetchResult fetchText(String id) {
		
		if (handler == null) {
			String springFullPath = "spring.xml";
			ApplicationContext context = new FileSystemXmlApplicationContext(springFullPath);
			if (! context.containsBean("textFetcherHandler")) {
				throw new IllegalArgumentException("No textFetcherHandler found in configuration");
			}
			handler = (TextFetcherHandler) context.getBean("textFetcherHandler");
			
		}
		
		if (handler != null)
			return handler.fetchText(id);
		else {
			return null;
		}
		
	}


}
