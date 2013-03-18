package varviewer.server.annotation;

public interface AnnotationKeyIndex {

	/**
	 * Unique string describing this annotation key ("pop.freq", "mt.score", etc)
	 * @return
	 */
	public String getKey();
	
	/**
	 * Numeric index for this key, enabling quick lookup of annotation value for this key
	 * @return
	 */
	public int getIndex();
	
	/**
	 * Whether or no the Annotation should be treated as a (floating point) number. 
	 * If not, the Annotation will be a String
	 * @return
	 */
	public boolean isNumeric();
	
}
