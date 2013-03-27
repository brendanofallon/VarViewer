package varviewer.server;


import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import varviewer.client.services.VarRequestService;
import varviewer.shared.VariantRequest;
import varviewer.shared.VariantRequestResult;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the variant request service. The 'variantSource' is basically a caching
 * wrapper for variants from a DirSampleSource, which reads variants from a file but
 * doesn't store them in memory. 
 * @author brendan
 *
 */
public class VarRequestServiceImpl extends RemoteServiceServlet implements VarRequestService {

	VariantRequestHandler reqHandler = null;
	

	@Override
	public VariantRequestResult queryVariant(VariantRequest req)
			throws IllegalArgumentException {

		LogFactory lf = null;
		
		if (reqHandler == null) {
			String springFullPath = "spring.xml";
			Logger.getLogger(getClass()).info("Loading spring config from " + springFullPath);
			ApplicationContext context = new FileSystemXmlApplicationContext(springFullPath);
			if (! context.containsBean("variantRequestHandler")) {
				throw new IllegalArgumentException("No VariantRequestHandler found in configuration");
			}
			reqHandler = (VariantRequestHandler) context.getBean("variantRequestHandler");
			
		}
		
		if (reqHandler != null)
			return reqHandler.queryVariant(req);
		else {
			return null;
		}
	}


	public VariantRequestHandler getReqHandler() {
		return reqHandler;
	}


	public void setReqHandler(VariantRequestHandler reqHandler) {
		this.reqHandler = reqHandler;
	}

	
	
}
