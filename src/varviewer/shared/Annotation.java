package varviewer.shared;

import java.io.Serializable;

/**
 * A single bit of information for a variant. Can be either a string or numeric, 
 * with toString reporting just one or the other. 
 * @author brendan
 *
 */
public class Annotation implements Serializable {

	String stringVal = null;
	Double doubleVal = null;
	boolean isNumeric = false;
	
	public Annotation() {
		//Required no-arg constructor
	}
	
	public Annotation(String str) {
		this.stringVal = str;
		isNumeric = false;
	}
	
	public Annotation(Double dub) {
		this.doubleVal = dub;
		isNumeric = true;
	}
	
	public String toString() {
		if (isNumeric)
			return doubleVal != null ? doubleVal.toString() : null;
		else 
			return stringVal;
	}
	
	public boolean isNumeric() {
		return isNumeric;
	}
	
	public Double getDoubleValue() {
		return doubleVal;
	}
	
	public String getStringValue() {
		return stringVal;
	}
}
