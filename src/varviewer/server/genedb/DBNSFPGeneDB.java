package varviewer.server.genedb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import varviewer.server.geneDetails.GeneDetailHandler;
import varviewer.shared.GeneInfo;

/**
 * Temporary interface for info from dbNSFP-gene
 * @author brendan
 *
 */
public class DBNSFPGeneDB implements GeneDetailHandler {

	private Map<String, DBNSFPInfo> map = null;
	File sourceFile = null;
		
	public DBNSFPGeneDB(File sourceFile) throws IOException {
		if (! sourceFile.exists()) {
			Logger.getLogger(DBNSFPGeneDB.class).warn("GeneDB file " + sourceFile.getAbsolutePath() + " does not exist");
		}
		this.sourceFile = sourceFile;
		readFile(sourceFile);	
	}
	
	public DBNSFPGeneDB() {
		//OK to use setter for source file
	}
	
	
	
	public File getSourceFile() {
		return sourceFile;
	}



	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
		try {
			readFile(sourceFile);
		} catch (IOException e) {
			Logger.getLogger(DBNSFPGeneDB.class).warn("Error reading geneDB source file: " + sourceFile.getAbsolutePath() + " : " + e.getMessage());
		}
	}



	/**
	 * Obtain a geneInfo object for the gene with the given name
	 * @param geneName
	 * @return
	 */
	public DBNSFPInfo getDBNSFPInfoForGene(String geneName) {
		return map.get(geneName);
	}
	
	public GeneInfo getInfoForGene(String geneName) {
		DBNSFPInfo dbInfo = getDBNSFPInfoForGene(geneName);
		GeneInfo geneInfo = new GeneInfo();
		if (dbInfo != null) {
			String mimDisease = dbInfo.mimDisease;
			if (mimDisease != null) {
				geneInfo.setOmimDiseaseIDs(mimDisease.split(";"));
			}

			String diseaseDesc = dbInfo.diseaseDesc;
			if (diseaseDesc != null) {
				geneInfo.setDbNSFPDisease(diseaseDesc);
			}

			String summary = dbInfo.summary;
			if (summary != null) {
				geneInfo.setSummary(summary);
			}
			
			String hgmdHits = dbInfo.hgmdHits;
			if (hgmdHits != null) {
				String[] hgmdVars = hgmdHits.split(";");
				geneInfo.setHgmdVars( hgmdVars );
			}
			
			String omimPhenos = dbInfo.omimPhenos;
			if (omimPhenos != null) {
				String[] phenos = omimPhenos.split(",");
				geneInfo.setOmimPhenos(phenos);
			}
			
			String omimInheritance = dbInfo.omimInheritance;
			if (omimInheritance != null) {
				geneInfo.setOmimInheritance(new String[]{omimInheritance});
			}
		}
		
		return geneInfo;
	}
	

	private void readFile(File sourceFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
		
		map = new HashMap<String, DBNSFPInfo>();
		
		String line = reader.readLine();
		line = reader.readLine();
		
		
		while(line != null) {			
			String[] toks = line.split("\t");
			DBNSFPInfo info = new DBNSFPInfo();
			info.geneName = toks[0];
			
			
			info.mimDisease = toks[16];
			info.diseaseDesc = toks[15];
			info.functionDesc = toks[14];
			info.expression = toks[19] + ";" + toks[20];
			info.summary = toks[26];
			info.hgmdHits = toks[27];
			info.omimPhenos = toks[28];
			info.omimInheritance = toks[29];
			
			if (info.summary.equals("null")) {
				info.summary = "-";
			}
			map.put(info.geneName, info);
			line = reader.readLine();
		}
	
		Logger.getLogger(getClass()).info("Initialized gene database with " + map.size() + " elements");
		reader.close();
	}
	
	
	public class DBNSFPInfo {
		public String geneName = null;
		public String mimDisease =  null; //Column 16
		public String diseaseDesc = null; //Column 15
		public String functionDesc = null; //Column 14
		public String expression = null; //Columns 19 and 20
		public String summary = null;
		public String hgmdHits = null;
		public String omimPhenos = null;
		public String omimInheritance = null;
	}
}
