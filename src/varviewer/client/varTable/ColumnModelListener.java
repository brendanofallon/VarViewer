package varviewer.client.varTable;

public interface ColumnModelListener {

	/**
	 * Called when a column model has had any columns removed or added
	 * @param model
	 */
	public void columnStateChanged(ColumnModel model);
	
}
