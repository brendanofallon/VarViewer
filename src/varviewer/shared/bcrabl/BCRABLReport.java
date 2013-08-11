package varviewer.shared.bcrabl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BCRABLReport implements Serializable {

	String message;
	String sampleAccession;
	String analysisDate;
	List<String> reportText = new ArrayList<String>();
	
	
	public BCRABLReport() {
		//blah blah
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSampleAccession() {
		return sampleAccession;
	}

	public void setSampleAccession(String sampleAccession) {
		this.sampleAccession = sampleAccession;
	}

	public String getAnalysisDate() {
		return analysisDate;
	}

	public void setAnalysisDate(String analysisDate) {
		this.analysisDate = analysisDate;
	}

	public List<String> getReportText() {
		return reportText;
	}

	public void addReportTextLine(String line) {
		this.reportText.add(line);
	}
	
	
}
