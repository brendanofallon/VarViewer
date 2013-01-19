package varviewer.server;

import java.io.File;
import java.io.IOException;

import varviewer.client.services.GeneDetailService;
import varviewer.server.genedb.DBNSFPGeneDB;
import varviewer.server.genedb.DBNSFPGeneDB.DBNSFPInfo;
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

	String dbNSFPPath = "/home/brendan/workspace/VarViewer/geneInfo.csv";
	DBNSFPGeneDB dbNSFP = null;
	
	@Override
	public GeneInfo getDetails(String geneID) {
		if (dbNSFP == null) {
			try {
				dbNSFP = DBNSFPGeneDB.getDB(new File(dbNSFPPath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		GeneInfo info = new GeneInfo();
		DBNSFPInfo dbInf = dbNSFP.getInfoForGene(geneID);
		
		if (dbInf != null) {
			String mimDisease = dbInf.mimDisease;
			if (mimDisease != null) {
				info.setOmimDiseaseIDs(mimDisease.split(";"));
			}

			String diseaseDesc = dbInf.diseaseDesc;
			if (diseaseDesc != null) {
				info.setDbNSFPDisease(diseaseDesc);
			}

			String summary = dbInf.summary;
			if (summary != null) {
				info.setSummary(summary);
			}
			
			String hgmdHits = dbInf.hgmdHits;
			if (hgmdHits != null) {
				String[] hgmdVars = hgmdHits.split(";");
				info.setHgmdVars( hgmdVars );
			}
			
			String omimPhenos = dbInf.omimPhenos;
			if (omimPhenos != null) {
				String[] phenos = omimPhenos.split(",");
				info.setOmimPhenos(phenos);
			}
			
			String omimInheritance = dbInf.omimInheritance;
			if (omimInheritance != null) {
				info.setOmimInheritance(new String[]{omimInheritance});
			}
		}
		return info;
	}

}
