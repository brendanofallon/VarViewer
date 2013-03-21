package varviewer.util.concurrentBuffer;

public interface Consumer<T> {

	/**
	 * Gets called when the Producer is done producing and the buffer size is zero.
	 */
	public void done();
	
	public void processItem(T item);
	
}
