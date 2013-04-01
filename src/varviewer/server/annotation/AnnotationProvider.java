package varviewer.server.annotation;

import java.util.Collection;
import java.util.List;

import varviewer.shared.variant.Variant;

/**
 * These objects are capable of annotating a variant
 * @author brendan
 *
 */
public interface AnnotationProvider {

	/**
	 * Obtain the set of all annotations this provider can provide
	 * @return
	 */
	public Collection<String> getAnnotationsProvided();
	
	public AnnotationKeyIndex[] getKeyIndices(List<String> annotations);
	
	/**
	 * Attempt to add the annotation associated with each key to the variant provided
	 * @param var
	 * @return
	 */
	public void annotateVariant(Variant var, AnnotationKeyIndex[] annotationKeys);
	
	
}
