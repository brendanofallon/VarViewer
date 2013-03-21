package varviewer.util.concurrentBuffer;

public interface Producer<T> {
	
	/**
	 * Producers should return true when there are no more items to produce
	 * @return
	 */
	public boolean isFinishedProducing();
	
	/**
	 * Return the next item produced. May return null if there is no next item ready.  
	 * @return
	 */
	public T nextItem();

}
