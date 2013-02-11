package varviewer.shared;

import java.io.Serializable;
import java.util.List;

/**
 * A single node in a tree of samples / sample containers. These contain either a single sampleInfo
 * object and are thus a leaf, or a list of SampleTreeNodes and are not a leaf.
 * @author brendanofallon
 *
 */
public class SampleTreeNode implements Serializable {

	private String userTitle = null;
	private List<SampleTreeNode> children = null;
	private SampleInfo info = null;
	
	
	public SampleTreeNode() {
		//required no-arg constructor, not used
	}
	
	public SampleTreeNode(String title, List<SampleTreeNode> children) {
		this.userTitle = title;
		this.children = children;
	}
	
	public SampleTreeNode(SampleInfo child) {
		this.userTitle = child.getSampleID();
		this.info = child;
	}
	
	public void setChildren(String title, List<SampleTreeNode> children) {
		this.userTitle = title;
		this.children = children;
	}
	
	public SampleInfo getSampleInfo() {
		return info;
	}
	
	public List<SampleTreeNode> getChildren() {
		return children;
	}
	
	public String getTitle() {
		return userTitle;
	}
	
	public boolean isLeaf() {
		return info != null;
	}
}
