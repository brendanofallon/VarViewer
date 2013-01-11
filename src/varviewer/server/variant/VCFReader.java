package varviewer.server.variant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import varviewer.shared.Variant;

public class VCFReader {

	private BufferedReader reader;
	private int currentLineNumber = -1;
	private String currentLine = null;
	protected String[] lineToks = null;
	private String[] formatToks = null; //Tokenized format string, produced as needed
	private int gtCol = -1; //Format column which contains genotype info
	private int gqCol = -1; //Format column which contains genotype quality info 
	private int adCol = -1; //Format column which contains allele depth info 
	private int dpCol = -1; //Format column which contains depth info
			
	private String sample = null; //Emit information for only this sample if specified (when not given, defaults to first sample)
	private int sampleColumn = -1; //Column that stores information for the given sample
	protected final File sourceFile;
	
	private String currentFormatStr = null;
	
	public VCFReader(File file, String sample) throws IOException {
		this.reader = new BufferedReader(new FileReader(file));
		this.sourceFile = file;
		currentLine = reader.readLine();
		this.sample = sample; //Sample must be specified before header is read
		readHeader();
	}
	
	/**
	 * Create a VCFLineReader to read variants from the given input stream
	 * @param stream
	 * @throws IOException
	 */
	public VCFReader(InputStream stream) throws IOException {
		this.reader = new BufferedReader(new InputStreamReader(stream));
		sourceFile = null;
		currentLine = reader.readLine();
		sampleColumn = 9; //First column with info, this is the default when no sample is specified
		readHeader();
	}
	
	public VCFReader(File file) throws IOException {
		this.reader = new BufferedReader(new FileReader(file));
		this.sourceFile = file;
		currentLine = reader.readLine();
		sampleColumn = 9; //First column with info, this is the default when no sample is specified
		readHeader();
	}
	
	public String getHeader() throws IOException {
		if (sourceFile == null) {
			return null;
		}
		
		BufferedReader headReader = new BufferedReader(new FileReader(sourceFile));
		String line = headReader.readLine();
		StringBuilder strB = new StringBuilder();
		while(line != null && line.trim().startsWith("#")) {
			strB.append(line + "\n");
			line = headReader.readLine();
		}
		headReader.close();
		return strB.toString();
	}
	
	private void readHeader() throws IOException {
		while (currentLine != null && currentLine.startsWith("#")) {
			advanceLine();
			
			if (currentLine == null) {
				throw new IOException("Could not find start of data");
			}
			
			if (currentLine.startsWith("#CHROM")) {
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
	 * Advance the current line until the contig found is the given contig. If
	 * already at the given contig, do nothing
	 * @param contig
	 * @throws IOException 
	 */
	public void advanceToContig(String contig) throws IOException {
		while (hasLine() && (!getContig().equals(contig))) {
			advanceLine();
		}
		if (! hasLine()) {
			throw new IllegalArgumentException("Could not find contig " + contig + " in vcf");
		}
	}
	
	/**
	 * Advance the current line until we reach a contig whose name matches the contig arg,
	 * and we find a variant whose position is equal to or greater than the given position
	 * @throws IOException 
	 */
	public void advanceTo(String contig, int pos) throws IOException {
		advanceToContig(contig);
		while(hasLine() && getPosition() < pos) {
			advanceLine();
			if (! hasLine()) {
				throw new IllegalArgumentException("Advanced beyond end file looking for pos: " + pos);
			}
			if (! getContig().equals(contig)) {
				throw new IllegalArgumentException("Could not find position " + pos + " in contig " + contig);
			}
		}
	}
	
	public boolean isPassing() {
		return currentLine.contains("PASS");
	}
	
	/**
	 * Convert current line into a variant record
	 * @param stripChr If true, strip 'chr' from contig name, if false do not alter contig name
	 * @return A new variant record containing the information in this vcf line
	 */
	public Variant toVariant() {
		if (currentLine == null || currentLine.trim().length()==0)
			return null;
		else {
			
			Variant rec = null;
			try {
				String contig = getContig();
				if (contig == null)
					return null;

				String ref = getRef();
				String alt = getAlt();
				int start = getStart();
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
//				Integer depth = getDepth();
//				if (depth != null)
//					rec.addProperty(Variant.DEPTH, new Double(depth));
//
//				Integer altDepth = getVariantDepth();
//				if (altDepth != null) {
//					rec.addProperty(Variant.VAR_DEPTH, new Double(altDepth));
//				}
//
//				if (rec.isMultiAllelic()) {
//					Integer altDepth2 = getVariantDepth(1);
//					if (altDepth2 != null) {
//						rec.addProperty(Variant.VAR2_DEPTH, new Double(altDepth2));
//					}
//				}
//				
//				Double genotypeQuality = getGenotypeQuality();
//				if (genotypeQuality != null) 
//					rec.addProperty(Variant.GENOTYPE_QUALITY, genotypeQuality);


			}
			catch (Exception ex) {
				System.err.println("ERROR: could not parse variant from line : " + currentLine + "\n Exception: " + ex.getCause() + " " + ex.getMessage());
				
				return null;
			}
			return rec;
		}
	}
	




	/**
	 * Read one more line of input, returns false if line cannot be read
	 * @return
	 * @throws IOException
	 */
	public boolean advanceLine() throws IOException {
		currentLine = reader.readLine();
		while(currentLine != null && currentLine.startsWith("#")) {
			currentLine = reader.readLine();
			currentLineNumber++;
		}
		
		if (currentLine == null)
			lineToks = null;
		else
			lineToks = currentLine.split("\\t");

		return currentLine != null;
	}

	/**
	 * Returns true if the current line is not null. 
	 * @return
	 */
	public boolean hasLine() {
		return currentLine != null;
	}
	
	public String getContig() {
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
	public int getPosition() {
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
	public Integer getDepth() {
		String info = lineToks[7];
		
		String target = "DP";
		int index = info.indexOf(target);
		if (index < 0) {
			//Attempt to get DP from INFO tokens...
			Integer dp = getDepthFromInfo();
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
	

	
	public int getStart() {
		return getPosition();
	}
	
	/**
	 * Return the end of this variant
	 * @return
	 */
	public int getEnd() {
		if (lineToks != null) {
			return Integer.parseInt(lineToks[2]);
		}
		else
			return -1;
	}
	
	public Double getQuality() {
		if (lineToks != null) {
			return Double.parseDouble(lineToks[5]);
		}
		else
			return -1.0;
	}
	
	public String getRef() {
		if (lineToks != null) {
			return lineToks[3];
		}
		else
			return "?";
	}
	
	public String getAlt() {
		if (lineToks != null) {
			return lineToks[4];
		}
		else
			return "?";
	}
	
	public int getLineNumber() {
		return currentLineNumber;
	}
	
	private void updateFormatIfNeeded() {
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
	public boolean isHetero() {
		updateFormatIfNeeded();
		
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
	
	public boolean isHomo() {
		return ! isHetero();
	}

	public String getCurrentLine() {
		return currentLine;
	}
	
	/**
	 * Returns true if the phasing separator is "|" and not "/" 
	 * @return
	 */
	public boolean isPhased() {
		updateFormatIfNeeded();
		
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
	public boolean firstIsAlt() {
		updateFormatIfNeeded();
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
	public boolean secondIsAlt() {
		updateFormatIfNeeded();
		
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
	public Double getGenotypeQuality() {
		updateFormatIfNeeded();
		
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
	
	private Double getVQSR() {
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
	
	private Double getLogFSScore() {
		String[] infoToks = lineToks[7].split(";");
		for(int i=0; i<infoToks.length; i++) {
			String tok = infoToks[i];
			if (tok.startsWith("LOGFS=")) {
				Double val = Double.parseDouble(tok.replace("LOGFS=", ""));
				return val;
			}
		}
		return null;
	}

	
	private Double getTauFPScore() {
		String[] infoToks = lineToks[7].split(";");
		for(int i=0; i<infoToks.length; i++) {
			String tok = infoToks[i];
			if (tok.startsWith("TAUFP=")) {
				Double val = Double.parseDouble(tok.replace("TAUFP=", ""));
				return val;
			}
		}
				
		return null;
	}
	
	
	private Double getStrandBiasScore() {
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
	public Integer getDepthFromInfo() {
		updateFormatIfNeeded();
		
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
	public Integer getVariantDepth() {
		return getVariantDepth(0);
	}
	
	/**
	 * Returns the depth of the whichth variant allele, as parsed from the INFO string for this sample
	 * @return
	 */
	public Integer getVariantDepth(int which) {
		updateFormatIfNeeded();
		
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
}
