package varviewer.shared.bcrabl;

import java.io.Serializable;

public class CisTransResult implements Serializable {

	String message;

	public CisTransResult() {
		//must have no-arg constructor
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
