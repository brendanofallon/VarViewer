package varviewer.shared.bcrabl;

import java.io.Serializable;

import varviewer.shared.variant.Variant;

public class CisTransRequest implements Serializable {

	Variant varA;
	Variant varB;
	String sampleID;
	
	public CisTransRequest() {
		//must have no arg constructor
	}

	public Variant getVarA() {
		return varA;
	}

	public void setVarA(Variant varA) {
		this.varA = varA;
	}

	public Variant getVarB() {
		return varB;
	}

	public void setVarB(Variant varB) {
		this.varB = varB;
	}

	public String getSampleID() {
		return sampleID;
	}

	public void setSampleID(String sampleID) {
		this.sampleID = sampleID;
	}
	
	
	
}
