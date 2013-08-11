package varviewer.server.bcrabl;

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

	SampleSource sampleSource = null;
	CisTransHandler cisTransHandler = new CisTransClassifier();
	
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
		
		VariantCollection vars = sampleSource.getVariantsForSample(info.getSampleID());
		//Sanity check
		if (vars.getContigCount() > 1) {
			Logger.getLogger(getClass()).error("Incorrect number of contigs found for BCR-ABL sample: " + info.getSampleID());
			report.setMessage("There was an error generating this report: An incorrect number of contigs (" + vars.getContigCount() + ") was found");
			return report;
		}
		
		List<Variant> varList = vars.getVariantsForContig( vars.getContigs().iterator().next() );
		
		if (varList.size()==0) {
			report.setMessage("No variants were detected for this sample");
		}
		else {
			if (varList.size() == 1) {
				report.setMessage(varList.size() + " mutation was detected.");
				String line = createLineForVariant( varList.get(0));
				report.addReportTextLine(line);
		
			}
			else {
				report.setMessage(varList.size() + " mutations were detected.");
				for(Variant var : varList) {
					String line = createLineForVariant(var);
					//TODO: Add info for cis/trans for all other variants!
					report.addReportTextLine( line );
				}
			}
			
		}
		
		return report;
	}
	
	private String createLineForVariant(Variant var) {
		String freqStr = "Error computing frequency";
		try {
			Double depth = var.getAnnotationDouble("depth");
			Double varDepth = var.getAnnotationDouble("var.depth");
			if (depth > 0) {
				freqStr = formatFreq(varDepth / depth);	
			}
			else {
				freqStr = "Error computing frequency (unknown read depth)";
			}
			
		}
		catch (Exception ex) {
			Logger.getLogger(getClass()).error("Error computing alt.freq for BCR-ABL sample with variant: " + var);	
		}
		
		return var.getAnnotationStr("pdot").replace("p.",  "") + "  (" + var.getAnnotationStr("cdot") + "); " + freqStr;
	}

	/**
	 * Use the Cis/Trans classifier to generate a short string stating whether the variants are in cis, trans, or too far apart to tell
	 * @param focalVar
	 * @param otherVar
	 * @return
	 */
	private String computeCisTransText(String sampleID, Variant focalVar, Variant otherVar) {
		if (focalVar == otherVar) {
			return "";
		}
		
		CisTransRequest req = new CisTransRequest();
		req.setSampleID(sampleID);
		req.setVarA(focalVar);
		req.setVarB(otherVar);
		
		CisTransResult result = cisTransHandler.computeCisTransResult(req);
		if (result.getReads()<1) {
			return "";
		}
		else {
			
			double rawCis = result.getCisFrac();
			double rawTrans = result.getTransFrac();
			double normalizedCis = rawCis / (rawCis + rawTrans);
			
			if (normalizedCis > 0.75) {
				return "in cis with " + otherVar.getAnnotationStr("pdot").replace("p.", "");
			}
			
			if (normalizedCis < 0.25) {
				return "in trans with " + otherVar.getAnnotationStr("pdot").replace("p.", "");
			}
			
			return " cis/trans uncertain for " + otherVar.getAnnotationStr("pdot").replace("p.", "");
		}
		
	}
	
	private String formatFreq(double freq) {
		String str = "" + freq*100.0;
		if (str.length() > 4) {
			str = str.substring(0, 4);
		}
		str = str + "%";
		return str;
	}

}
