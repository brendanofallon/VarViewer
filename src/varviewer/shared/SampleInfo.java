package varviewer.shared;


import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
	private String absolutePath = null;
	private Map<String, String> allItems = new HashMap<String, String>();
	
	public SampleInfo() {
		//required no-arg constructor
	}
	
	public SampleInfo(String sampleID, String analysisType, Date analysisDate, String submitter, String absolutePath) {
		this.sampleID = sampleID;
		this.analysisType = analysisType;
		this.analysisDate = analysisDate;
		this.submitter = submitter;
		this.setAbsolutePath(absolutePath);
	}

	public void addItem(String key, String value) {
		allItems.put(key, value);
	}
	
	public String getItem(String key) {
		return allItems.get(key);
	}
	
	public boolean containsItem(String key) {
		return allItems.containsKey(key);
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
	
	public boolean equals(Object o) {
		if (! (o instanceof SampleInfo)) {
			return false;
		}
		
		SampleInfo i = (SampleInfo)o;
		return (i.getAbsolutePath().equals(this.getAbsolutePath()) 
				&& i.sampleID.equals(this.sampleID)
				&& i.analysisDate.equals(this.analysisDate));
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}
	
	
}
