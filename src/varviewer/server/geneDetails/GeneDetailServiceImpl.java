package varviewer.server.geneDetails;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import varviewer.client.services.GeneDetailService;
import varviewer.shared.GeneInfo;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the GeneDetail service, which looks up various pieces of info
 * regarding a Gene (including ref seq summary, omim diseases, etc.) and adds them
 * to a GeneInfo object for return. GeneInfo objects provide the data for the DetailsPanel
 * in the main window. 
 * @author brendan
 *
 */
public class GeneDetailServiceImpl extends RemoteServiceServlet implements GeneDetailService {


	GeneDetailHandler geneDetailHandler = null;
	
	@Override
	public GeneInfo getDetails(String geneID) {
		
		if (geneDetailHandler == null) {
			ApplicationContext context = new FileSystemXmlApplicationContext("spring.xml");
			if (! context.containsBean("geneDetailHandler")) {
				throw new IllegalArgumentException("No GeneDetailHandler found in configuration");
			}
			geneDetailHandler = (GeneDetailHandler) context.getBean("geneDetailHandler");
		}
		
		
		GeneInfo info = null;
		if (geneDetailHandler != null) {
			info = geneDetailHandler.getInfoForGene(geneID);
		}
		return info;
	}

	
}
