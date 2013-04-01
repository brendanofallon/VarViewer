package varviewer.shared.variant;

import java.io.Serializable;

public class Annotation implements Serializable {

	private String strValue = null;
	private Double doubleValue = null;
	
	public Annotation() {
		//required no-arg constructor
	}
	
	public Annotation(String str) {
		this.strValue = str;
	}
	
	public Annotation(Double dub) {
		this.doubleValue = dub;
	}
	
	public Double getDoubleValue() {
		return doubleValue;
	}
	
	public String getStringValue() {
		return strValue;
	}
	
	public String toString() {
		if (strValue != null) {
			return strValue;
		}
		else {
			if (doubleValue != null) {
				return doubleValue.toString();
			}
			return null;
		}
	}

}
