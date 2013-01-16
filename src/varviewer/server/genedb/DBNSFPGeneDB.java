package varviewer.server.genedb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Temporary interface for info from dbNSFP-gene
 * @author brendan
 *
 */
public class DBNSFPGeneDB {

	private static DBNSFPGeneDB db = null;
	private Map<String, DBNSFPInfo> map = null;

	public static DBNSFPGeneDB getDB() {	
		return db;
	}
	
	public static DBNSFPGeneDB getDB(File sourceFile) throws IOException {
		if (db == null) {
			db = new DBNSFPGeneDB(sourceFile);
		}
		
		return db;
	}

	private DBNSFPGeneDB(File sourceFile) throws IOException {
		readFile(sourceFile);
	}
	
	/**
	 * Obtain a geneInfo object for the gene with the given name
	 * @param geneName
	 * @return
	 */
	public DBNSFPInfo getInfoForGene(String geneName) {
		return map.get(geneName);
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
			
			if (info.summary.equals("null")) {
				info.summary = "-";
			}
			map.put(info.geneName, info);
			line = reader.readLine();
		}
	
		System.err.println("Initialized gene database with " + map.size() + " elements");
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
	}
}
