package varviewer.client;

import com.google.gwt.user.client.Window;

/**
 * A simple class to provide an interface to IGV.
 * See http://www.broadinstitute.org/software/igv/ControlIGV for details on how
 * this works
 * @author brendan
 *
 */
public class IGVInterface {

	public static final String baseURL = "http://localhost:60151/"; 
	private static String loadedURL = null;
	
	/**
	 * Load a new sample into IGV and jump to locus specified
	 * @param url
	 * @param locus
	 * @param name
	 */
	public static void loadBAM(String url, String locus, String name) {
		loadedURL = url;
		String fullURL = baseURL + "load?file=" + url + "&locus=" + locus + "&name=" + name;
		Window.open(fullURL, "_self", "");
	}
	
	/**
	 * Go to locus specified in current sample
	 * @param url
	 * @param locus
	 * @param name
	 */
	public static void goToLocus(String locus) {
		if (loadedURL != null) {
			String fullURL = baseURL + "goto?locus=" + locus;
			Window.open(fullURL, "_self", "");
		}
	}
	
	/**
	 * GoToLocus, but load BAM if the url has changed from the previous call (or has not  been set)
	 * @param url
	 * @param locus
	 * @param name
	 */
	public static void loadAndGo(String url, String locus, String name) {
		if (loadedURL != null && loadedURL.equals(url)) {
			goToLocus(locus);
		}
		else {
			loadBAM(url, locus, name);
		}
	}
}
