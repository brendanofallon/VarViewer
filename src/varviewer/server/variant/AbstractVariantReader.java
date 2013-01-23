package varviewer.server.variant;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import varviewer.shared.Variant;

public abstract class AbstractVariantReader {

	protected File varFile = null;
	private String[] headerToks = null;

	public AbstractVariantReader(File source) throws IOException {
		this.varFile = source;
		if (!varFile.exists()) {
			Logger.getLogger(getClass()).error("Cannot read variants from file " + varFile.getAbsolutePath() + ", it does not exist");
			throw new IllegalArgumentException("File " + varFile.getAbsolutePath() + " does not exist");
		}
		if (!varFile.canRead()) {
			Logger.getLogger(getClass()).error("Cannot read variants from file " + varFile.getAbsolutePath() + ", it is not readable");
			throw new IllegalArgumentException("File " + varFile.getAbsolutePath() + " exists but is not readable");
		}
	}
	
	

	/**
	 * Returns all variants in a VariantCollection
	 * @return
	 * @throws IOException 
	 */
	public abstract VariantCollection toVariantCollection() throws IOException;
	
	protected void initializeHeader(String header) {
		header = header.substring(1); //Trim off leading #
		headerToks = header.split("\t");
		for(int i=0; i<headerToks.length; i++) {
			headerToks[i] = headerToks[i].trim();
		}
	}
	
	
	
	
	/**
	 * Parse, create and return a variant from the given string. All items in columns five
	 * and greater are treated as annotations with key determined by the header 
	 * @param str
	 * @return
	 */
	protected Variant variantFromString(String str) {
		String[] toks = str.split("\t");
		if (toks.length != headerToks.length) {
			return null;
		}
		String contig = toks[0];
		Integer start = Integer.parseInt(toks[1]);
		String ref = toks[3];
		String alt = toks[4];
		Variant var = new Variant(contig, start, ref, alt);
		for(int i=5; i<toks.length; i++) {
			var.addAnnotation(headerToks[i], toks[i]);
			//System.out.println("Adding annotation '" + headerToks[i] + "' = " + toks[i]);
		}
		return var;
	}
}
