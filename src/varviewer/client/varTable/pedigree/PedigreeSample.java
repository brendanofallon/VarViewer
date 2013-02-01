package varviewer.client.varTable.pedigree;

public class PedigreeSample {

	enum ZygType {ALL, HETS, HOMS};
	enum OperationType {NONE, EXCLUDE, INTERSECT};
	
	String probandId = null; //Sample id of proband
	String relId = null; //Sample id of the sample for comparison
	String relUserLabel = null;
	ZygType zType = ZygType.HETS;	//Category of variants to include / exclude
	OperationType oType = OperationType.NONE; //Operation to perform, include, exclude, etc.
	
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
			return "Hets.";
		}
		if (zType == ZygType.HOMS) {
			return "Homs.";
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
		System.err.println("Error: unrecognized zygosity string : " +zygStr);
		return ZygType.HETS;
	}
	
}
