package varviewer.client.varTable.pedigree;

import varviewer.client.varTable.VarAnnotation;
import varviewer.shared.PedigreeSample;
import varviewer.shared.Variant;

import com.google.gwt.user.cellview.client.TextColumn;

/**
 * Special type of VarAnnotation that is used to represent the zygosity of 
 * pedigree samples
 * @author brendan
 *
 */
public class PedigreeVarAnnotation extends VarAnnotation<String> {

	/**
	 * The worst code ever? This is an anonymous inner class embedded in a call to the super-constructor
	 * and the rest of the class does nothing!
	 * @param pedSample
	 */
	public PedigreeVarAnnotation(final PedigreeSample pedSample) {
		super(pedSample.getRelId() + "-zygosity", pedSample.getRelId(), new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotation(pedSample.getRelId() + "-zygosity");
			
				if (val == null || val.length()<2)
					return "?";
					
				return val;
			}
		});
	}

}
