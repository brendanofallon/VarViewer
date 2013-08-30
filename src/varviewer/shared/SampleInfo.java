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
 
	private String sampleID = null; //name of sample, often accession number for clinical samples
	private String analysisType = null; //exome, aortapathy, etc.
	private Date analysisDate = null; //When bioinformatics was performed
	private String submitter = null; //username associated with account that submitted job (e.g. marc, brendan, etc.)
	private String annotatedVarsFile = null; //relative path to annotated variants file
	private String vcfFile = null;  //relative path to variants vcf file
	private String vcfLink = null;  //location of link to vcf file
	private String bamFile = null;  //relative path to BAM file
	private String bamLink = null;  //location of html link to BAM file
	private String qcLink = null; //location of link to qc report
	
	public SampleInfo() {
		//required no-arg constructor
	}
	
	public SampleInfo(String sampleID, String analysisType, Date analysisDate, String submitter) {
		this.sampleID = sampleID;
		this.analysisType = analysisType;
		this.analysisDate = analysisDate;
		this.submitter = submitter;
	}

	public int getUniqueKey() {
		String str = getKeyText();
		return str.hashCode();
	}
	
	public String getKeyText() {
		String str = "" + sampleID + analysisType + analysisDate.getTime();
		return str;
	}
	
	public String getBamLink() {
		return bamLink;
	}

	public void setBamLink(String bamLink) {
		this.bamLink = bamLink;
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

	public String getQCLink() {
		return qcLink;
	}
	
	public void setQCLink(String linkLoc) {
		this.qcLink = linkLoc;
	}

	public String getVcfLink() {
		return vcfLink;
	}
	
	public void setVcfLink(String link) {
		this.vcfLink = link;
	}
	
	
	
}
