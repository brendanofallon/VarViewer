package varviewer.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Static access to some basic properties
 * @author brendan
 *
 */
public class VVProps {
	
	public static final String PROPS_FILENAME = "varviewerprops.txt";
	
	private static boolean initialized = false;
	private static Map<String, String> props = new HashMap<String, String>();
	
	public static void loadProperties() throws IOException {

		//Look for a file to add more properties
		File propsFile = new File(PROPS_FILENAME);
		System.err.println("Looking for properties file at path: " + propsFile.getAbsolutePath());
		if (! propsFile.exists()) {
			
			Logger.getLogger(VVProps.class).info("No properties file on path: " + propsFile.getAbsolutePath());
			propsFile = new File("../" + PROPS_FILENAME);
		}
		
		System.err.println("Looking for properties file at path: " + propsFile.getAbsolutePath());
		if (! propsFile.exists()) {
			Logger.getLogger(VVProps.class).info("No properties file on path: " + propsFile.getAbsolutePath());
			propsFile = new File(System.getProperty("user.dir") + "/" + PROPS_FILENAME);
		}
		
		System.err.println("Looking for properties file at path: " + propsFile.getAbsolutePath());
		if (! propsFile.exists()) {
			Logger.getLogger(VVProps.class).info("No properties file on path: " + propsFile.getAbsolutePath());
			propsFile = new File(System.getProperty("user.home") + "/" + PROPS_FILENAME);
		}
		
		System.err.println("Looking for properties file at path: " + propsFile.getAbsolutePath());
		if (! propsFile.exists()) {
			Logger.getLogger(VVProps.class).info("No properties file on path: " + propsFile.getAbsolutePath());
			propsFile = new File("/usr/share/tomcat6/webapps/VarViewer/" + PROPS_FILENAME);
		}
		
		if (!propsFile.exists()) {
			Logger.getLogger(VVProps.class).info("No properties file on path: " + propsFile.getAbsolutePath());
			Logger.getLogger(VVProps.class).error("Could not find properties file on any path!");
			return;
		}
		
		Logger.getLogger(VVProps.class).info("Reading properties file on path: " + propsFile.getAbsolutePath());
		BufferedReader reader;
		reader = new BufferedReader(new FileReader(propsFile));
		String line = reader.readLine();
		while(line != null) {
			String[] toks = line.split("=");
			if (toks.length==2) {
				props.put(toks[0], toks[1]);
				Logger.getLogger(VVProps.class).info("Adding property " + toks[0] + "=" + toks[1]);
			}
			line = reader.readLine();
		}
		
		reader.close();
		initialized = true;
	}
	
	public static String getProperty(String key) {
		if (! initialized) {
			try {
				loadProperties();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return props.get(key);
	}
	
}
