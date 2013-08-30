package varviewer.client.varTable.pedigree;

import varviewer.client.varTable.VarAnnotation;
import varviewer.shared.PedigreeSample;
import varviewer.shared.variant.Variant;

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
		super(pedSample.getRelSample().getSampleID() + "-zygosity", pedSample.getRelSample().getSampleID(), new TextColumn<Variant>() {

			@Override
			public String getValue(Variant var) {
				String val = var.getAnnotationStr(pedSample.getRelSample().getSampleID() + "-zygosity");
			
				if (val == null || val.length()<2)
					return "?";
					
				return val;
			}
		});
	}

}
