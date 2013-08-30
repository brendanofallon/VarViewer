package varviewer.server.bcrabl;
import java.io.File;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

import org.apache.log4j.Logger;

import varviewer.server.sampleSource.SampleSource;
import varviewer.shared.bcrabl.CisTransRequest;
import varviewer.shared.bcrabl.CisTransResult;
import varviewer.shared.variant.Variant;


/**
 * @author markebbert
 *
 */
public class CisTransClassifier implements CisTransHandler {

	private int permittedDist = 150;
	private int requiredMappingQual = 4;
	private int requiredBaseQual = 4;

	SampleSource sampleSource = null;
	
	public SampleSource getSampleSource() {
		return sampleSource;
	}



	public int getPermittedDist() {
		return permittedDist;
	}



	public void setPermittedDist(int permittedDist) {
		Logger.getLogger(getClass()).info("Cis/trans setting required max distance to " + permittedDist);
		this.permittedDist = permittedDist;
	}



	public int getRequiredMappingQual() {
		return requiredMappingQual;
	}



	public void setRequiredMappingQual(int requiredMappingQual) {
		Logger.getLogger(getClass()).info("Cis/trans setting required mapping quality to " + requiredMappingQual);
		this.requiredMappingQual = requiredMappingQual;
	}



	public int getRequiredBaseQual() {
		return requiredBaseQual;
	}



	public void setRequiredBaseQual(int requiredBaseQual) {
		Logger.getLogger(getClass()).info("Cis/trans setting required base quality to " + requiredBaseQual);
		this.requiredBaseQual = requiredBaseQual;
	}



	public void setSampleSource(SampleSource sampleSource) {
		this.sampleSource = sampleSource;
	}

	public boolean closeEnoughToCompute(CisTransRequest req) {
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

	public CisTransResult computeCisTransResult(CisTransRequest req) {
		
		Variant var1 = req.getVarA();
		Variant var2 = req.getVarB();
		File inputBAMFile = sampleSource.getBAMFileForSample(req.getSample());
		if (! inputBAMFile.exists()) {
			throw new IllegalArgumentException("Cannot find bam file for sampleID " + req.getSample().getSampleID() + " path is: " + inputBAMFile.getAbsolutePath());
		}
		
		//A few sanity checks:
		if (!var1.getChrom().equals(var2.getChrom())) {
			throw new IllegalArgumentException("Variants are not on the same chromosome");
		}

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

//		String newLine = System.getProperty("line.separator");
//		String leftAlignFormat = "| %-14s | %-10s | %13.2f%% | %-8.0f |" + newLine;
//		String leftAlignFormatSummary = "| %-10s | %-14s | %11.2f%% | %-8.0f |" + newLine;
//		//				String centerAlignFormat = "| %14s | %10s | %13.2f%% | %8.0f |" + newLine;
//		String totalCovFormat = "%-46s | %-8.0f |" + newLine;
//		System.out.format("+---------------------------------------------------------+" + newLine);
//		System.out.format("|    Min. Mapping Qual: " + requiredMappingQual +
//				"    |     Min. Base Qual " + requiredBaseQual + "     |" + newLine);
//		System.out.format("+----------------+------------+----------------+----------+" + newLine);
//		System.out.format("| Ref/Alt Combo  | Haplotype  | Perc. Reads    | n Reads  |" + newLine);
//		System.out.format("+----------------+------------+----------------+----------+" + newLine);
//
//		System.out.format(leftAlignFormat, "Ref / Ref", var1Ref + "..." + var2Ref,
//				new Double((bothRefs/readCov)*100), bothRefs);
//		System.out.format(leftAlignFormat, "Alt / Ref", var1Alt + "..." + var2Ref,
//				new Double((alt1Only/readCov)*100), alt1Only);
//		System.out.format(leftAlignFormat, "Ref / Alt", var1Ref + "..." + var2Alt,
//				new Double((alt2Only/readCov)*100), alt2Only);
//
//
//		System.out.format(leftAlignFormat, "Alt / Alt", var1Alt + "..." + var2Alt,
//				new Double((bothAlts/readCov)*100), bothAlts);
//		System.out.format(leftAlignFormat, "Misc.", "NA",
//				new Double((misc/readCov)*100), misc);
//
//		System.out.format("+----------------+------------+----------------+----------+" + newLine);
//		System.out.format(totalCovFormat, "", readCov);
//		System.out.format("                                               +----------+" + newLine);
//
//
//		System.out.println("\n");
//		System.out.format("+------------+----------------+--------------+----------+" + newLine);
//		System.out.format("| Cis/Trans  | Haplotype      | Perc. Reads  | n Reads  |" + newLine);
//		System.out.format("+------------+----------------+--------------+----------+" + newLine);			
//
//		System.out.format(leftAlignFormatSummary, "Trans", var1Alt + "..." + var2Ref + " or " +
//				var1Ref + "..." + var2Alt,
//				new Double(( (alt1Only+alt2Only)/readCov)*100), alt1Only+alt2Only);
//		System.out.format(leftAlignFormatSummary, "Cis", var1Alt + "..." + var2Alt,
//				new Double((bothAlts/readCov)*100), bothAlts);
//		System.out.format("+------------+----------------+--------------+----------+" + newLine);
		//				System.out.format(totalCovFormat, "", readCov);
		//				System.out.format("                                               +----------+" + newLine);			
		//			}
		//			else{
		//				throw new RuntimeException("ERROR: The two variants are too far apart to determine cis/trans relationship.");
		//			}

		
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


}
