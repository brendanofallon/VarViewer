package varviewer.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PedigreeSample implements Serializable, IsSerializable {

	public enum ZygType {ALL, HETS, HOMS};
	public enum OperationType {NONE, EXCLUDE, INTERSECT};
	
	private SampleInfo probandId = null; //Sample id of proband
	private SampleInfo relId = null; //Sample id of the sample for comparison
	//String relUserLabel = null; //not currently used
	private ZygType zType = ZygType.ALL;	//Category of variants to include / exclude
	private OperationType oType = OperationType.NONE; //Operation to perform, include, exclude, etc.
	
	public PedigreeSample() {
		//required no-arg constructor
	}
	
	public SampleInfo getProbandId() {
		return probandId;
	}

	public void setProbandId(SampleInfo probandId) {
		this.probandId = probandId;
	}

	public SampleInfo getRelSample() {
		return relId;
	}

	public void setRelSample(SampleInfo relInfo) {
		this.relId = relInfo;
	}

	public ZygType getzType() {
		return zType;
	}

	public void setzType(ZygType zType) {
		this.zType = zType;
	}

	public OperationType getoType() {
		return oType;
	}

	public void setoType(OperationType oType) {
		this.oType = oType;
	}

	/**
	 * A user-friendly label for the given zygosity type
	 * @param zType
	 * @return
	 */
	public static String getUserString(ZygType zType) {
		if (zType == ZygType.ALL) {
			return "All";
		}
		if (zType == ZygType.HETS) {
			return "Hets";
		}
		if (zType == ZygType.HOMS) {
			return "Homs";
		}
		
		//Should never get here
		return "?";
	}
	
	/**
	 * Convert from a string to a ZygType - this should be the reverse of getUserString(zType)
	 * @param zygStr
	 * @return
	 */
	public static ZygType getZygTypeForString(String zygStr) {
		if (zygStr.equals("All")) {
			return ZygType.ALL;
		}
		if (zygStr.equals("Hets")) {
			return ZygType.HETS;
		}
		if (zygStr.equals("Homs")) {
			return ZygType.HOMS;
		}
		
		//Should never get here
		throw new IllegalArgumentException("Unrecognized zygosity string : " +zygStr);
	}
	
	public boolean equals(Object o) {
		if (! (o instanceof PedigreeSample)) {
			return false;
		}
		PedigreeSample p = (PedigreeSample)o;
		if (p.getRelSample().equals(relId) && p.getoType().equals(oType)) {
			return true;
		}
		return false;
	}
}
