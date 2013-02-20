package varviewer.client.varTable;

import java.util.Comparator;

import varviewer.shared.Annotation;
import varviewer.shared.Variant;

/**
 * This is the comparator used by default to compare two rows in the variant table. 
 * It compares Annotations associated with the given key using either the string or 
 * double values of the annotations, whichever the Annotation provides.  
 * @author brendan
 *
 */
public class DefaultAnnotationComparator implements Comparator<Variant> {
	public final String key;
	
	public DefaultAnnotationComparator(String key) {
		this.key = key;
	}

	@Override
	public int compare(Variant v0, Variant v1) {
		if (v0 == v1) {
			return 0;
		}
		
		Annotation a0 = v0.getAnnotation(key);
		Annotation a1 = v1.getAnnotation(key);
		
		if (a0 == null && a1 == null) {
			return 0;
		}
		if (a0 == null && a1 != null) {
			return 1;
		}
		if (a1 == null && a0 != null) {
			return -1;
		}
		
		if (a0.isNumeric()) {
			Double val0 = a0.getDoubleValue();
			Double val1 = a1.getDoubleValue();
			
			if (val0 == null && val1 == null) {
				return 0;
			}
			if (val0 == null && val1 != null) {
				return 1;
			}
			if (val1 == null && val0 != null) {
				return -1;
			}
			
			return val0.compareTo(val1);
		}
		
		
		String s0 = a0.getStringValue();
		String s1 = a1.getStringValue();
		
		if (s0 == null && s1 == null) {
			return 0;
		}
		if (s0 == null && s1 != null) {
			return 1;
		}
		if (s1 == null && s0 != null) {
			return -1;
		}
		
		return s0.compareTo(s1);
	}
}
