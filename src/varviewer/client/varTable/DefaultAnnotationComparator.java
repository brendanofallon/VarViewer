package varviewer.client.varTable;

import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import varviewer.shared.variant.Annotation;
import varviewer.shared.variant.Variant;

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
		
	    Logger logger = Logger.getLogger("varviewer_log");
	    logger.log(Level.SEVERE, "Key: "+key);
	    
		//varTypeIndex = index.getIndexForKey("variant.type");
		//exonFuncIndex = index.getIndexForKey("exon.function");
		String keyToUse = key;
		// With recent snpeff upgrade this exon.function column is a bunch of '-', so lets use the column that actually contains stuff
		if (key.equals("exon.function")) {
		    logger.log(Level.SEVERE, "keyToUse: "+keyToUse);
			keyToUse = "variant.type";
		}
		
		Annotation a0 = v0.getAnnotation(key);
		Annotation a1 = v1.getAnnotation(key);
//	    logger.log(Level.INFO, "Anno1: "+a0.toString());
//	    logger.log(Level.INFO, "Anno2: "+a1.toString());

		if (a0 == null && a1 == null) {
			return 0;
		}
		if (a0 == null && a1 != null) {
			return 1;
		}
		if (a1 == null && a0 != null) {
			return -1;
		}
		if (a0.getDoubleValue() != null && a1.getDoubleValue() != null) {
			return (a0.getDoubleValue()).compareTo(a1.getDoubleValue());
		}
		else {
			return a0.toString().compareTo(a1.toString());
		}
	}
}
