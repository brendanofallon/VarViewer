package varviewer.server;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import varviewer.client.services.VarRequestService;
import varviewer.server.appContext.SpringContext;
import varviewer.shared.variant.VariantRequest;
import varviewer.shared.variant.VariantRequestResult;

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
		
		//If the requestHandler has not been initialized, try to initialize it. 
		if (reqHandler == null) {
			ApplicationContext context = SpringContext.getContext(); 
			reqHandler = (VariantRequestHandler) context.getBean("variantRequestHandler");
			
		}	
		
		
		if (reqHandler != null){
			VariantRequestResult result = reqHandler.queryVariant(req);
			Logger.getLogger(getClass()).info("Returning variant result with " + result.getVars().size() + " variants");
			return result;
		}
		else {
			Logger.getLogger(getClass()).error("Could not initialize variant request handler!");
			System.err.println("Could not initialize request handler!");
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
