package varviewer.server.sampleSource;

/**
 * Thrown when some type of error arises during sample info parsing
 * @author brendan
 *
 */
public class SampleParseException extends Exception {

	public SampleParseException(String message) {
		super(message);
	}

}
