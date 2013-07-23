package varviewer.shared;

import java.io.Serializable;

/**
 * Result of attempted change in password. 
 * @author brendan
 *
 */
public class ChangePWResult implements Serializable {

	boolean resultOK = true;
	String message = null;
	
	public ChangePWResult() {
		
	}

	public boolean isResultOK() {
		return resultOK;
	}

	public void setResultOK(boolean resultOK) {
		this.resultOK = resultOK;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
