package varviewer.util.concurrentBuffer;

/**
 * Consumes elements off the head of a ConcurrentBuffer. See ConcurrentBuffer for details.
 * @author brendan
 *
 * @param <T>
 */
public interface Consumer<T> {

	/**
	 * Unimplemented
	 */
	public void done();
	
	/**
	 * 
	 * @param item
	 */
	public void processItem(T item);
	
}
