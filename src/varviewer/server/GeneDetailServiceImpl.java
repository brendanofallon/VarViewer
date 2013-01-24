package varviewer.server;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

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

	String dbNSFPPath = "geneInfo.csv";
	DBNSFPGeneDB dbNSFP = null;
	
	@Override
	public GeneInfo getDetails(String geneID) {
		if (dbNSFP == null) {
			String paths = VVProps.getProperty("genedb.path");
			if (paths != null) {
				String[] pathList = paths.split(":");
				for(int i=0; i<pathList.length; i++) {
					boolean ok = attemptCreateDBFromPath(pathList[i]);
					if (ok) {
						Logger.getLogger(getClass()).info("Successfully created GeneInfoDB from path " + pathList[i]);
						break;
					}
				}
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
		else {
			Logger.getLogger(getClass()).debug("No gene details info found for gene : " + geneID);
		}
		return info;
	}

	
	private boolean attemptCreateDBFromPath(String path) {
		try {
			File dbFile = new File(path);
			Logger.getLogger(getClass()).info("Trying to create GeneInfoDB from path " + dbFile.getAbsolutePath());
			dbNSFP = DBNSFPGeneDB.getDB(dbFile);
			return dbNSFP != null;
		} catch (IOException e) {
			return false;
		}
	}
}
