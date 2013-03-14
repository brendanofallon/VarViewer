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
	
}
