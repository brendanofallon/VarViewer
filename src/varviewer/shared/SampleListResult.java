package varviewer.shared;

import java.io.Serializable;

/**
 * These objects are returned after a request to the SampleListService. They're mostly
 * (entirely?) a thin wrapper for a TreeViewModel that contains a tree structure of SampleInfo objects
 * , but may someday be expanded to handle more complex scenarios 
 * @author brendanofallon
 *
 */
public class SampleListResult implements Serializable {

	SampleTreeNode rootNode = null;
	
	public SampleListResult() {
		//required no-arg constructor
	}
	
	public SampleListResult(SampleTreeNode root) {
		this.rootNode = root;
	}
	
	public SampleTreeNode getRootNode() {
		return rootNode;
	}
}
