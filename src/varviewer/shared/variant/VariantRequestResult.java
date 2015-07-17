package varviewer.shared.variant;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * These objects are returned as the result of a VariantRequest and do little
 * more than encapsulate a list of variants and their source
 * @author brendan
 *
 */
public class VariantRequestResult implements Serializable, IsSerializable {

	List<Variant> vars = null;
	String sampleID = null;
	
	public VariantRequestResult() {
		//required no-arg constructor
	}

	public List<Variant> getVars() {
		return vars;
	}

	public void setVars(List<Variant> vars) {
		this.vars = vars;
	}

	public String getSampleID() {
		return sampleID;
	}

	public void setSampleID(String sampleID) {
		this.sampleID = sampleID;
	}
	
	

}
