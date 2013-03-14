package varviewer.server.variant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import varviewer.shared.Variant;

public class VCFReader extends AbstractVariantReader {

	private BufferedReader reader;
	private String[] formatToks = null; //Tokenized format string, produced as needed
	private int gtCol = -1; //Format column which contains genotype info
	private int gqCol = -1; //Format column which contains genotype quality info 
	private int adCol = -1; //Format column which contains allele depth info 
	private int dpCol = -1; //Format column which contains depth info
			
	private String sample = null; //Emit information for only this sample if specified (when not given, defaults to first sample)
	private int sampleColumn = -1; //Column that stores information for the given sample

	
	private String currentFormatStr = null;
	
	public VCFReader(File file, String sample) throws IOException {
		super(file);
		this.reader = new BufferedReader(new FileReader(file));
		this.sample = sample; //Sample must be specified before header is read
		readHeader();
	}
	
	public VCFReader(File file) throws IOException {
		super(file);
		this.reader = new BufferedReader(new FileReader(file));
		sampleColumn = 9; //First column with info, this is the default when no sample is specified
		readHeader();
	}
	
	
	private void readHeader() throws IOException {
		String currentLine = reader.readLine();
		while (currentLine != null && currentLine.startsWith("#")) {
			currentLine = reader.readLine();
			
			if (currentLine == null) {
				throw new IOException("Could not find start of data");
			}
			
			if (currentLine.toUpperCase().startsWith("#CHROM")) {
				String[] toks = currentLine.split("\t");
				if (sample == null) {
					sampleColumn = 9;
					if (toks.length > 9)
						sample = toks[9];
					else 
						sample = "unknown";
				}
				else {
					for(int col = 0; col<toks.length; col++) {
						if (toks[col].equals(sample)) {
							sampleColumn = col;
						}
					}
				}
				if (sampleColumn < 0) {
					throw new IllegalArgumentException("Cannot find column for sample " + sample);
				}
			}
			
			
		}
	}
	
	public String getSampleName() {
		return sample;
	}
	
	/**
	 * Convert current line into a variant record
	 * @param stripChr If true, strip 'chr' from contig name, if false do not alter contig name
	 * @return A new variant record containing the information in this vcf line
	 */
	protected Variant variantFromString(String str) {
		Variant rec = null;
		try {
			String[] lineToks = str.split("\t");
			String contig = getContig(lineToks);
			if (contig == null)
				return null;

			String ref = getRef(lineToks);
			String alt = getAlt(lineToks);
			int start = getStart(lineToks);
			int end = ref.length();

			if (alt.length() != ref.length()) {
				//Remove initial characters if they are equal and add one to start position
				if (alt.charAt(0) == ref.charAt(0)) {
					alt = alt.substring(1);
					ref = ref.substring(1);
					if (alt.length()==0)
						alt = "-";
					if (ref.length()==0)
						ref = "-";
					start++;
				}

				if (ref.equals("-"))
					end = start;
				else
					end = start + ref.length();
			}

			rec = new Variant(contig, start, ref, alt);
			Integer depth = getDepth(lineToks);
			if (depth != null)
				rec.addAnnotation("depth", new Double(depth));
			
			Double quality = getQuality(lineToks);
			if (quality != null) {
				rec.addAnnotation("quality", quality);
			}
			
			Integer varDepth = getVariantDepth(lineToks);
			if (varDepth != null) {
				rec.addAnnotation("var.depth", new Double(varDepth));
			}		
	
		}
		catch (Exception ex) {
			System.err.println("ERROR: could not parse variant from line : " + str + "\n Exception: " + ex.getCause() + " " + ex.getMessage());
			return null;
		}
		return rec;
		
	}
	


	public String getContig(String[] lineToks) {
		if (lineToks != null) {
			return lineToks[0];
		}
		else
			return null;
	}
	
	/**
	 * Return the (starting) position item for current line
	 * @return
	 */
	public int getPosition(String[] lineToks) {
		if (lineToks != null) {
			return Integer.parseInt(lineToks[1]);
		}
		else
			return -1;
	}
	
	/**
	 * Read depth from INFO column, tries to identify depth by looking for a DP string, then reading
	 * the following number
	 * @return
	 */
	public Integer getDepth(String[] lineToks) {
		String info = lineToks[7];
		
		String target = "DP";
		int index = info.indexOf(target);
		if (index < 0) {
			//Attempt to get DP from INFO tokens...
			Integer dp = getDepthFromInfo(lineToks);
			return dp;
		}
		
		//System.out.println( info.substring(index, index+10) + " ...... " + info.substring(index+target.length()+1, info.indexOf(';', index)));
		try {
			Integer value = Integer.parseInt(info.substring(index+target.length()+1, info.indexOf(';', index)));
			return value;
		}
		catch (NumberFormatException nfe) {

		}
		return null;
	}
	

	
	public int getStart(String[] lineToks) {
		return getPosition(lineToks);
	}
	
	/**
	 * Return the end of this variant
	 * @return
	 */
	public int getEnd(String[] lineToks) {
		if (lineToks != null) {
			return Integer.parseInt(lineToks[2]);
		}
		else
			return -1;
	}
	
	public Double getQuality(String[] lineToks) {
		if (lineToks != null) {
			return Double.parseDouble(lineToks[5]);
		}
		else
			return -1.0;
	}
	
	public String getRef(String[] lineToks) {
		if (lineToks != null) {
			return lineToks[3];
		}
		else
			return "?";
	}
	
	public String getAlt(String[] lineToks) {
		if (lineToks != null) {
			return lineToks[4];
		}
		else
			return "?";
	}
	
	private void updateFormatIfNeeded(String[] lineToks) {
		if (lineToks.length > 7) {
			if (formatToks == null) {
				createFormatString(lineToks);
			}
			else {
				if (! currentFormatStr.equals(lineToks[8]))
					createFormatString(lineToks);
			}
		}
	}
	
	/**
	 * 
	 */
	public boolean isHetero(String[] lineToks) {
		updateFormatIfNeeded(lineToks);
		
		if (formatToks == null)
			return false;
		
		String[] formatValues = lineToks[sampleColumn].split(":");
		String GTStr = formatValues[gtCol];
		
		if (GTStr.length() != 3) {
			throw new IllegalStateException("Wrong number of characters in string for is hetero... (got " + GTStr + ", but length should be 3)");
		}

		if (GTStr.charAt(1) == '/' || GTStr.charAt(1) == '|') {
			if (GTStr.charAt(0) != GTStr.charAt(2))
				 return true;
			else
				return false;
		}
		else {
			throw new IllegalStateException("Genotype separator char does not seem to be normal (found " + GTStr.charAt(1) + ")");
		}
		
	}
	
	public boolean isHomo(String[] lineToks) {
		return ! isHetero(lineToks);
	}

	
	/**
	 * Returns true if the phasing separator is "|" and not "/" 
	 * @return
	 */
	public boolean isPhased(String[] lineToks) {
		updateFormatIfNeeded(lineToks);
		
		if (formatToks == null)
			return false;
		
		String[] formatValues = lineToks[sampleColumn].split(":");
		String GTStr = formatValues[gtCol];
		if (GTStr.charAt(1) == '|') {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * True if the first item in the genotype string indicates an 'alt' allele
	 * @return
	 */
	public boolean firstIsAlt(String[] lineToks) {
		updateFormatIfNeeded(lineToks);
		if (formatToks == null)
			return false;
		
		String[] formatValues = lineToks[sampleColumn].split("\t");
		String GTStr = formatValues[gtCol];
		if (GTStr.charAt(0) == '1') {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * True if the second item in the genotype string indicates an 'alt' allele
	 * @return
	 */
	public boolean secondIsAlt(String[] lineToks) {
		updateFormatIfNeeded(lineToks);
		
		if (formatToks == null)
			return false;
		
		String[] formatValues = lineToks[sampleColumn].split(":");
		String GTStr = formatValues[gtCol];
		if (GTStr.charAt(2) == '1') {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Obtain the genotype quality score for this variant
	 * @return
	 */
	public Double getGenotypeQuality(String[] lineToks) {
		updateFormatIfNeeded(lineToks);
		
		if (formatToks == null || gqCol < 0)
			return 0.0;
		
		String[] formatValues = lineToks[sampleColumn].split(":");
		String GQStr = formatValues[gqCol];
		try {
			Double gq = Double.parseDouble(GQStr);
			return gq;
		}
		catch (NumberFormatException ex) {
			System.err.println("Could not parse genotype quality from " + GQStr);
			return null;
		}
		
	}
	
	private Double getVQSR(String[] lineToks) {
		String[] infoToks = lineToks[7].split(";");
		for(int i=0; i<infoToks.length; i++) {
			String tok = infoToks[i];
			if (tok.startsWith("VQSLOD=")) {
				Double val = Double.parseDouble(tok.replace("VQSLOD=", ""));
				return val;
			}
		}
				
		return null;
	}	
	
	private Double getStrandBiasScore(String[] lineToks) {
		String[] infoToks = lineToks[7].split(";");
		for(int i=0; i<infoToks.length; i++) {
			String tok = infoToks[i];
			if (tok.startsWith("FS=")) {
				Double val = Double.parseDouble(tok.replace("FS=", ""));
				return val;
			}
		}
				
		return null;

	}

	/**
	 * Depth may appear in format OR INFO fields, this searches the latter for depth
	 * @return
	 */
	public Integer getDepthFromInfo(String[] lineToks) {
		updateFormatIfNeeded(lineToks);
		
		if (formatToks == null)
			return 1;
		
		if (dpCol < 0)
			return null;
		
		String[] formatValues = lineToks[sampleColumn].split(":");
		String dpStr = formatValues[dpCol];
		return Integer.parseInt(dpStr);
	}
	
	
	/**
	 * Returns the depth of the first variant allele, as parsed from the INFO string for this sample
	 * @return
	 */
	public Integer getVariantDepth(String[] lineToks) {
		return getVariantDepth(0, lineToks);
	}
	
	/**
	 * Returns the depth of the whichth variant allele, as parsed from the INFO string for this sample
	 * @return
	 */
	public Integer getVariantDepth(int which, String[] lineToks) {
		updateFormatIfNeeded(lineToks);
		
		if (formatToks == null)
			return 1;
		
		if (adCol < 0)
			return null;
			
		
		String[] formatValues = lineToks[sampleColumn].split(":");
		String adStr = formatValues[adCol];
		try {
			String[] depths = adStr.split(",");
			if (depths.length==1)
				return 0;
			Integer altReadDepth = Integer.parseInt(depths[which+1]);
			return altReadDepth;
		}
		catch (NumberFormatException ex) {
			System.err.println("Could not parse alt depth from " + adStr);
			return null;
		}
	}
	
	/**
	 * Create the string array representing elements in the 'format' column, which
	 * we assume is always column 8. Right now we use this info to figure out which portion
	 * of the format string is the genotype and genotype quality part, and we ignore the
	 * rest
	 */
	private void createFormatString(String[] toks) {
		if (toks.length <= 8) {
			formatToks = null;
			return;
		}
		
		String formatStr = toks[8];
		
		formatToks = formatStr.split(":");
		for(int i=0; i<formatToks.length; i++) {
			if (formatToks[i].equals("GT")) {
				gtCol = i;
			}
			
			if (formatToks[i].equals("GQ")) {
				gqCol = i;
			}
			
			if (formatToks[i].equals("AD")) {
				adCol = i;
			}
			if (formatToks[i].equals("DP")) {
				dpCol = i;
			}

		}
		
		currentFormatStr = formatStr;
	}

	@Override
	public VariantCollection toVariantCollection() throws IOException {
		List<Variant> vars = new ArrayList<Variant>(2048);
		BufferedReader reader = new BufferedReader( new FileReader(varFile));
		String line = reader.readLine();
		initializeHeader(line);
		line = reader.readLine(); //read next line, don't try to parse a variant from the header
		while(line != null) {
			if (! line.startsWith("#")) {
				Variant var = variantFromString(line);
				if (var != null)
					vars.add(var);
			}
			line = reader.readLine();
		}
		reader.close();
		if (vars.size()>0)
			Logger.getLogger(getClass()).info("Read in " + vars.size() + " variants from " + varFile);
		else {
			Logger.getLogger(getClass()).warn("Read in " + vars.size() + " variants from " + varFile);
		}
		return new VariantCollection(vars);
	}
	
	
	public static void main(String[] args) throws IOException {
		VCFReader vr = new VCFReader(new File("/home/brendan/jobwrangler_samples/tiny.reviewdir/var/medtest17_all_variants.vcf"));
		VariantCollection col = vr.toVariantCollection();
		for(String contig : col.getContigs()) {
			for(Variant var : col.getVariantsForContig(contig)) {
				System.out.println(var + " quality: " + var.getAnnotation("quality") + " depth: "  + var.getAnnotation("depth"));
			}
		}
	}
}
