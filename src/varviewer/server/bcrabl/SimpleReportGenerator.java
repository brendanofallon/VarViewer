package varviewer.server.bcrabl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import varviewer.server.sampleSource.SampleSource;
import varviewer.server.variant.VariantCollection;
import varviewer.shared.SampleInfo;
import varviewer.shared.bcrabl.BCRABLReport;
import varviewer.shared.bcrabl.CisTransRequest;
import varviewer.shared.bcrabl.CisTransResult;
import varviewer.shared.variant.Variant;


/**
 * This is a basic implementation of the BCR-ABL report generator
 * @author brendan
 *
 */
public class SimpleReportGenerator implements ReportHandler {

	public static final String NEGATIVE_RESULT_MESSAGE = "No variants were detected by this assay.";
	
	SampleSource sampleSource = null;
	CisTransHandler cisTransHandler = new CisTransClassifier();
	QualityChecker qChecker = new QualityChecker();
	
	
	final String[] snpsToIgnore = new String[]{ "K247R", "Y320C", "E499E", "F311V", "T240T", "T315T" }; 
	
	public CisTransHandler getCisTransHandler() {
		return cisTransHandler;
	}

	public void setCisTransHandler(CisTransHandler cisTransHandler) {
		this.cisTransHandler = cisTransHandler;
	}

	public SampleSource getSampleSource() {
		return sampleSource;
	}

	public void setSampleSource(SampleSource sampleSource) {
		this.sampleSource = sampleSource;
	}


	
	@Override
	public BCRABLReport getReportForSample(SampleInfo info) {
		BCRABLReport report = new BCRABLReport();
		
		
		String meanCoverage = info.getItem("mean.depth");
		if (meanCoverage != null) {
			try {
				Double cov = Double.parseDouble(meanCoverage);
				report.setMeanCoverage(cov);
			}
			catch(Exception ex) {
				Logger.getLogger(getClass()).warn("Error computing mean coverage for sample " + info.getSampleID() + " : " + ex.getMessage());
			}
		}
		else {
			Logger.getLogger(getClass()).warn("No mean coverage info for sample " + info.getSampleID() );
		}
		
		VariantCollection vars = sampleSource.getVariantsForSample(info);
		if (vars == null) {
			report.setMessage(NEGATIVE_RESULT_MESSAGE);
			return report;
		}
		
		//Sanity check
		if (vars.getContigCount() > 1) {
			Logger.getLogger(getClass()).error("Incorrect number of contigs found for BCR-ABL sample: " + info.getSampleID());
			report.setMessage("There was an error generating this report: An incorrect number of contigs (" + vars.getContigCount() + ") was found");
			return report;
		}
		
//		try {
//			QualityCheckResult qualityResult = qChecker.verifyQuality(info, sampleSource.getBAMFileForSample(info));
//			report.setPassedQualityCheck(qualityResult.passed);
//			report.setQualityMessage(qualityResult.message);
//		}
//		catch (Exception ex) {
//			report.setQualityMessage("Error running quality procedure: " + ex.getMessage() + "\n Unable to determine sample quality");
//		}
		
		List<Variant> varList = vars.getVariantsForContig( vars.getContigs().iterator().next() );
		
		if (varList.size()==0) {
			report.setMessage(NEGATIVE_RESULT_MESSAGE);
			return report;
		}

		List<String> resistanceComments = new ArrayList<String>();
		
		if (varList.size() == 1) {
			report.setMessage(varList.size() + " mutation was detected.");
			String line = createLineForVariant( varList.get(0));
			report.addReportTextLine(line);
			resistanceComments.add( createVarComment(varList.get(0)));
		}
		else {
			report.setMessage(varList.size() + " mutations were detected.");
			
			for(Variant var : varList) {
				String line = createLineForVariant(var);
				resistanceComments.add( createVarComment(var));
				
				//Compute all possible cis/trans relationships
				String cisTransPhrase = "";
				for(Variant var2 : varList) {
					String phrase = computeCisTransText(info, var, var2);
					
					if (phrase.length() > 0) {
						if (cisTransPhrase.length() > 0)
							cisTransPhrase = cisTransPhrase + ", ";
						cisTransPhrase = cisTransPhrase + phrase;
					}
				}
				
				if (cisTransPhrase.length() > 0) {
					cisTransPhrase = cisTransPhrase.trim();
					if (cisTransPhrase.endsWith(",")) {
						cisTransPhrase = cisTransPhrase.substring(0, cisTransPhrase.length()-1);
					}
					line = line + "  (" + cisTransPhrase + ")";
				}

				
				report.addReportTextLine( line );
			}
			
			
		}

		report.addReportTextLine("");
		for(String comment : resistanceComments) {
			report.addReportTextLine(comment);
		}

		return report;
	}
	
	private String createVarComment(Variant var) {
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
			return "No data on resistance for " + pDot + " to BCR-ABL1 tyrosine kinase inhibitors. The clinical significance of this finding is uncertain.";	
		}
	}
	
	private String createLineForVariant(Variant var) {
		String freqStr = "Error computing frequency";
		String depthStr = "?";
		try {
			Double depth = var.getAnnotationDouble("depth");
			Double varDepth = var.getAnnotationDouble("var.depth");
			
			if (depth == null || Double.isNaN(depth)) {
				depthStr = "?";
			}
			else {
				depthStr = "" + (int)Math.round(depth);
			}
			
			if (varDepth == null) {
				Logger.getLogger(getClass()).error("Could not read var.depth annotation for variant: " + var);
			}
			
			if (depth > 0) {
				freqStr = formatFreq(varDepth / depth);	
			}
			else {
				freqStr = "Error computing frequency (unknown read depth)";
			}
			
		}
		catch (Exception ex) {
			Logger.getLogger(getClass()).error("Error computing alt.freq for BCR-ABL sample with variant: " + var + " Exception: " + ex);	
		}
		
		return var.getAnnotationStr("pdot").replace("p.",  "") + "  (" + var.getAnnotationStr("cdot") + "); " + freqStr + " Coverage: " + depthStr;
	}

	/**
	 * Use the Cis/Trans classifier to generate a short string stating whether the variants are in cis, trans, or too far apart to tell
	 * @param focalVar
	 * @param otherVar
	 * @return
	 */
	private String computeCisTransText(SampleInfo sample, Variant focalVar, Variant otherVar) {
		if (focalVar == otherVar) {
			return "";
		}
		
		CisTransRequest req = new CisTransRequest();
		req.setSample(sample);
		req.setVarA(focalVar);
		req.setVarB(otherVar);
		
		if (! cisTransHandler.closeEnoughToCompute(req)) {
			return "";
		}
		
		CisTransResult result = cisTransHandler.computeCisTransResult(req);
		if (result.getCoverage()<1) {
			return "";
		}
		else {
			
			
			double cisFrac = result.getNewCisFrac();
			double transFrac = result.getNewTransFrac();
			
			if (cisFrac > 0.8) {
				return "in cis with " + otherVar.getAnnotationStr("pdot").replace("p.", "");
			}
			
			if (transFrac > 0.8) {
				return "in trans with " + otherVar.getAnnotationStr("pdot").replace("p.", "");
			}
			
//			double rawCis = result.getCisFrac();
//			double rawTrans = result.getTransFrac();
//			double normalizedCis = rawCis / (rawCis + rawTrans);
//			
//			if (normalizedCis > 0.8) {
//				return "in cis with " + otherVar.getAnnotationStr("pdot").replace("p.", "");
//			}
//			
//			if (normalizedCis < 0.2) {
//				return "in trans with " + otherVar.getAnnotationStr("pdot").replace("p.", "");
//			}
			
			return "";
		}
		
	}
	
	private String formatFreq(double freq) {
		String str = "" + (int)Math.round(freq*100.0);
		if (str.length() > 4) {
			str = str.substring(0, 4);
		}
		str = str + "%";
		return str;
	}

}
