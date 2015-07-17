package varviewer.shared.variant;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Annotation implements Serializable, IsSerializable {

	private String strValue = null;
	private Double doubleValue = null;
	private boolean numeric = false;
	
	public Annotation() {
		//required no-arg constructor
	}
	
	public boolean isNumeric() {
		return numeric;
	}
	
	public Annotation(String str) {
		this.strValue = str;
	}
	
	public Annotation(Double dub) {
		this.doubleValue = dub;
		numeric = true;
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
