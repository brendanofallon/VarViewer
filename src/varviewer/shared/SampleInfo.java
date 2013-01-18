package varviewer.shared;


import java.io.Serializable;
import java.util.Date;

/**
 * Information about a particular sample that is available on the server. Various SampleSource
 * classes generate lists of these to represent the samples they have available. 
 * @author brendan
 *
 */
public class SampleInfo implements Serializable {

	private String sampleID = null;
	private String analysisType = null;
	private Date analysisDate = null;
	private String submitter = null;
	private String annotatedVarsFile = null;
	private String vcfFile = null;
	private String bamFile = null;

	
	
	public SampleInfo() {
		//required no-arg constructor
	}
	
	public SampleInfo(String sampleID, String analysisType, Date analysisDate, String submitter) {
		this.sampleID = sampleID;
		this.analysisType = analysisType;
		this.analysisDate = analysisDate;
		this.submitter = submitter;
	}

	public String getSampleID() {
		return sampleID;
	}

	public String getAnalysisType() {
		return analysisType;
	}

	public Date getAnalysisDate() {
		return analysisDate;
	}

	public String getSubmitter() {
		return submitter;
	}

	public String getAnnotatedVarsFile() {
		return annotatedVarsFile;
	}

	public void setAnnotatedVarsFile(String annotatedVarsFile) {
		this.annotatedVarsFile = annotatedVarsFile;
	}

	public String getVcfFile() {
		return vcfFile;
	}

	public void setVcfFile(String vcfFile) {
		this.vcfFile = vcfFile;
	}

	public String getBamFile() {
		return bamFile;
	}

	public void setBamFile(String bamFile) {
		this.bamFile = bamFile;
	}

	public void setSampleID(String sampleID) {
		this.sampleID = sampleID;
	}

	public void setAnalysisType(String analysisType) {
		this.analysisType = analysisType;
	}

	public void setAnalysisDate(Date analysisDate) {
		this.analysisDate = analysisDate;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}
	
	
	
}
