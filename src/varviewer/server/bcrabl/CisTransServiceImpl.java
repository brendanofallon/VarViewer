package varviewer.server.bcrabl;

import org.springframework.context.ApplicationContext;

import varviewer.client.services.CisTransService;
import varviewer.server.appContext.SpringContext;
import varviewer.shared.bcrabl.CisTransRequest;
import varviewer.shared.bcrabl.CisTransResult;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Receives remote calls from the client regarding CisTrans
 * @author brendan
 *
 */
public class CisTransServiceImpl extends RemoteServiceServlet implements CisTransService {

	CisTransHandler reqHandler = null;
	
	@Override
	public CisTransResult computeCisTrans(CisTransRequest req) {

		if (reqHandler == null) {
			ApplicationContext context = SpringContext.getContext(); 
			reqHandler = (CisTransHandler) context.getBean("cisTransHandler");	
		}	
		
		return reqHandler.computeCisTransResult(req);
	}

}
