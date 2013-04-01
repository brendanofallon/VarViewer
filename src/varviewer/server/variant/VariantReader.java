package varviewer.server.variant;

import java.io.IOException;

/**
 * Things that can produce a VariantCollection should implement this interface
 * @author brendan
 *
 */
public interface VariantReader {

	/**
	 * Implementation-dependent, sets the source file / url / resource for the variant collection
	 * @param sourceURL
	 * @throws IOException
	 */
	public void setSource(String sourceURL) throws IOException;
	
	/**
	 * Produce a collection of variants from the given source
	 * @return
	 */
	public VariantCollection toVariantCollection();
	
}
