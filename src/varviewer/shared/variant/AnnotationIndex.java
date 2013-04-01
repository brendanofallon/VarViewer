package varviewer.shared.variant;

public interface AnnotationIndex {

	/**
	 * Returns a unique integer associated with the given annotation key
	 * @param key
	 * @return
	 */
	public int getIndexForKey(String key);
	
	/**
	 * True if the annotation associated with the given key is numeric, false otw
	 * @param key
	 * @return
	 */
	public boolean isNumericForKey(String key);
	
	/**
	 * Return the number of keys in the index
	 * @return
	 */
	public int size();
	
}
