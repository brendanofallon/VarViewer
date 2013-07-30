package varviewer.shared.bcrabl;

import java.io.Serializable;

import varviewer.shared.variant.Variant;

public class CisTransRequest implements Serializable {

	Variant varA;
	Variant varB;
	
	public CisTransRequest() {
		//must have no arg constructor
	}
	
}
