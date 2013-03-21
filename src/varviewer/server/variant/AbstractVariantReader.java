package varviewer.server.variant;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import varviewer.server.VVProps;
import varviewer.shared.Variant;

/**
 * Base class for things that read (create) Variants from files. This tends to be something of
 * a performance bottleneck so has undergone a bit of optimization that can make the code confusing.
 * 
 * @author brendan
 *
 */
public abstract class AbstractVariantReader {

	protected File varFile = null;
	protected String[] headerToks = null;
	protected boolean[] numericFlags = null; //Indicates which annotations are numeric, is initialized with header

	//Stores which annotations should be treated as numeric. This is read in from
	//the properties file every time an AbstractVariantReader is created. 
	//protected Set<String> numericAnnotations = new HashSet<String>();
	
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
		Set<String> numericAnnotations = new HashSet<String>();
		headerToks = header.split("\t");
		numericFlags = new boolean[headerToks.length];
		
		String numAnnosStr = VVProps.getProperty("numeric.annotations");
		if (numAnnosStr != null) {
			String[] annos = numAnnosStr.split(":");
			for(int i=0; i<annos.length; i++) {
				numericAnnotations.add(annos[i].trim());
			}
		}
		
		for(int i=0; i<headerToks.length; i++) {
			headerToks[i] = headerToks[i].trim();
			if (numericAnnotations.contains(headerToks[i])) {
				numericFlags[i] = true;
			}
			else {
				numericFlags[i]  = false;
			}
		}
		
	}
	
	
//	public boolean annotationIsNumeric(String annoKey) {
//		
//	}
	
	/**
	 * Parse, create and return a variant from the given string. All items in columns five
	 * and greater are treated as annotations with key determined by the header 
	 * @param str
	 * @return
	 */
	protected static Variant variantFromString(String[] toks, String[] headerTokens, boolean[] numericFlags) {
		if (toks.length != headerTokens.length) {
			return null;
		}
		
		String contig = toks[0];
		Integer start = Integer.parseInt(toks[1]);
		String ref = toks[3];
		String alt = toks[4];
		Variant var = new Variant(contig, start, ref, alt);
		for(int i=5; i<toks.length; i++) {
			if (numericFlags[i]) {
				try {
					Double val = Double.parseDouble( toks[i]);
					var.addAnnotation(headerTokens[i], val);
				}
				catch (NumberFormatException nfe) {
					//Don't sweat it, no annotation added
				}
			}
			else {
				var.addAnnotation(headerTokens[i], toks[i]);
			}
		}
		return var;
	}
}
