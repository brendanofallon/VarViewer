package varviewer.shared;

import java.io.Serializable;
import java.util.List;

/**
 * These are the result of a TextFetch service operation
 * @author brendan
 *
 */
public class TextFetchResult implements Serializable {

	List<String> linesOfText = null;

	public List<String> getLinesOfText() {
		return linesOfText;
	}

	public void setLinesOfText(List<String> linesOfText) {
		this.linesOfText = linesOfText;
	}
	
	
}
