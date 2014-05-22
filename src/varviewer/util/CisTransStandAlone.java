package varviewer.util;

import java.io.File;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import varviewer.server.bcrabl.MappedRead;
import varviewer.shared.bcrabl.CisTransRequest;
import varviewer.shared.bcrabl.CisTransResult;
import varviewer.shared.variant.Variant;

public class CisTransStandAlone {

	static int permittedDist = 150;
	static int requiredMappingQual = 4;
	static int requiredBaseQual = 4;
	
	final static String[] snpsToIgnore = new String[]{ "K247R", "Y320C", "E499E", "F311V", "T240T", "T315T" }; 
	
	public static CisTransResult compute(Variant var1, Variant var2, File inputBAMFile) {
		int var1Pos = var1.getPos();
		int var2Pos = var2.getPos();
		char var1Ref = var1.getRef().charAt(0);
		char var2Ref = var2.getRef().charAt(0);
		char var1Alt = var1.getAlt().charAt(0);
		char var2Alt = var2.getAlt().charAt(0);

		// test whether positions are within a certain distance (~100bp)
		// If not, we cannot tell because no reads will span both positions

		int dist = Math.abs(var1Pos - var2Pos);
		if (dist > permittedDist) {
			throw new IllegalArgumentException("Variants are not close enough to compute (" + dist + " bases separate them)");
		}

		int startPos = Math.min(var1Pos, var2Pos) - permittedDist;
		int endPos = Math.max(var1Pos, var2Pos) + permittedDist;
		if (startPos < 1) 
			startPos = 1;

		// open bam file
		SAMFileReader inputBAM = new SAMFileReader(inputBAMFile);
		SAMRecordIterator sit = inputBAM.query(var1.getChrom(), startPos, endPos, false);
		MappedRead mapped;
		char baseAtRef1, baseAtRef2;
		double bothAlts=0, bothRefs=0, alt1Only=0, alt2Only=0, misc=0,
				readCov=0;
		SAMRecord samRecord = sit.next();
		while(samRecord != null) {
			mapped = new MappedRead(samRecord);

			// If within distance, loop through reads and for each one that spans
			// both positions, count how many fit into one of four categories:
			// 1. Variants A and B on the read
			// 2. Variant A but not B on the read
			// 3. Variant B but not A on the read
			// 4. Neither variant on the read

			if(mapped.containsPosition(var1Pos) && mapped.containsPosition(var2Pos)
					&& mapped.getMappingQuality() >= requiredMappingQual
					&& mapped.getQualityAtReferencePos(var1Pos) >= requiredBaseQual
					&& mapped.getQualityAtReferencePos(var2Pos) >= requiredBaseQual){

				readCov++;

				baseAtRef1 = (char) mapped.getBaseAtReferencePos(var1Pos);
				baseAtRef2 = (char) mapped.getBaseAtReferencePos(var2Pos);

				if(baseAtRef1 == var1Ref && baseAtRef2 == var2Ref){
					bothRefs++;
				}
				else if(baseAtRef1 == var1Alt && baseAtRef2 == var2Alt){
					bothAlts++;
				}
				else if(baseAtRef1 == var1Alt && baseAtRef2 != var2Alt){
					alt1Only++;
				}
				else if(baseAtRef1 != var1Alt && baseAtRef2 == var2Alt){
					alt2Only++;
				}
				else{
					//							System.out.println("Base at Ref1: " + baseAtRef1 + "\t" + "Base at Ref2: " + baseAtRef2);
					misc++;
				}
			}

			if (sit.hasNext()) {
				samRecord = sit.next();
			}
			else {
				samRecord = null;
			}

		}
		inputBAM.close();

		CisTransResult result = new CisTransResult();
		if (readCov == 0) {
			result.setFailed(true);
			result.setMessage("No reads span both positions.");
		}
		else {
			result.setFailed(false);
			result.setCoverage((int)readCov);
			result.setBothRefs(new Double((bothRefs/readCov)*100) );
			result.setAlt1Only(new Double((alt1Only/readCov)*100) );
			result.setAlt2Only(new Double((alt2Only/readCov)*100) );
			result.setBothAlts(new Double((bothAlts/readCov)*100) );
			result.setMisc(new Double((misc/readCov)*100) );
			result.setTransFrac(new Double(( (alt1Only+alt2Only)/readCov)*100));
			result.setCisFrac(new Double((bothAlts/readCov)*100));
		}
		
		
		
		return result;

	}
	
	public static String createVarComment(Variant var) {
		//Generate ignore comment
		String pDot = var.getAnnotationStr("pdot").replace("p.", "");
		for(int i=0; i<snpsToIgnore.length; i++) {
			if (pDot.equals(snpsToIgnore[i])) {
				return pDot + " is a common SNP. Do not report.";
			}
		}
		
		//Generate resistance comment
		String known = var.getAnnotationStr("Known");
		boolean knownResistant = false;
		if (known != null) {
			knownResistant = Boolean.parseBoolean(known);
		}
		
		if (knownResistant) {
			return pDot + " has been reported to confer resistance to BCR-ABL1 tyrosine kinase inhibitors.";
		}
		else {
			return "No data on resistance for " + pDot;	
		}
	}
	
	public static String createLineForVariant(Variant var) {
		return var.toString();
	}

	public static String formatFreq(double freq) {
		String str = "" + (int)Math.round(freq*100.0);
		if (str.length() > 4) {
			str = str.substring(0, 4);
		}
		str = str + "%";
		return str;
	}
	
	public static boolean closeEnoughToCompute(CisTransRequest req) {
		Variant var1 = req.getVarA();
		Variant var2 = req.getVarB();
		int var1Pos = var1.getPos();
		int var2Pos = var2.getPos();
		int dist = Math.abs(var1Pos - var2Pos);
		if (dist > permittedDist) {
			return false;
		}
		return true;
	}
	
	public static String computeCisTransText(File bamFile, Variant focalVar, Variant otherVar) {
		if (focalVar == otherVar) {
			return " Vars are at the same position";
		}
		
		CisTransRequest req = new CisTransRequest();
		
		req.setVarA(focalVar);
		req.setVarB(otherVar);
		
		if (! closeEnoughToCompute(req)) {
			return " Not close enough";
		}
		
		CisTransResult result = compute(focalVar, otherVar, bamFile);
		if (result.getCoverage()<1) {
			return "";
		}
		else {
			
			double rawCis = result.getCisFrac();
			double rawTrans = result.getTransFrac();
			double normalizedCis = rawCis / (rawCis + rawTrans);
			
			System.out.println("Informative reads: " + result.getCoverage());
			System.out.println("Raw cis frac: " + rawCis);
			System.out.println("Raw trans frac: " + rawTrans);
			if (normalizedCis > 0.8) {
				return " in cis (normalized cis = " + normalizedCis + ")";
			}
			
			if (normalizedCis < 0.2) {
				return "in trans (normalized cis = " + normalizedCis + ")";
			}
			
			return " Unsure, normalized cis = " + normalizedCis;
		}
		
	}
	
	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println("Enter a bam file and variants in [pos] [ref] [alt] format, one after the other.");
			System.out.println("Example: <input.bam> 56 A G 129 T G");
			return;
		}

		File bamFile = new File(args[0]);
		Variant var1 = new Variant("ABL1", Integer.parseInt(args[1]), args[2], args[3]);
		Variant var2 = new Variant("ABL1", Integer.parseInt(args[4]), args[5], args[6]);

		String line = createLineForVariant(var1);
		//resistanceComments.add( createVarComment(var1));
		
		//Compute all possible cis/trans relationships
		String cisTransPhrase = "";
		String phrase = computeCisTransText(bamFile, var1, var2);
			
			if (phrase.length() > 0) {
				if (cisTransPhrase.length() > 0)
					cisTransPhrase = cisTransPhrase + ", ";
				cisTransPhrase = cisTransPhrase + phrase;
			}
		
		
		if (cisTransPhrase.length() > 0) {
			cisTransPhrase = cisTransPhrase.trim();
			if (cisTransPhrase.endsWith(",")) {
				cisTransPhrase = cisTransPhrase.substring(0, cisTransPhrase.length()-1);
			}
			line = line + "  (" + cisTransPhrase + ")";
		}
		
		System.out.println(line);
	}
}
