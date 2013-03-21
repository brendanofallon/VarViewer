package varviewer.util.concurrentBuffer;

/**
 * Creates elements to add to a ConcurrentBuffer. See ConcurrentBuffer for details. 
 * @author brendan
 *
 * @param <T>
 */
public interface Producer<T> {
	
	/**
	 * Producers should return true when there are no more items to produce
	 * @return
	 */
	public boolean isFinishedProducing();
	
	/**
	 * Return the next item produced. May return null if the next item is not ready yet.  
	 * @return
	 */
	public T nextItem();

}
