package varviewer.server.appContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Singleton class to provide controlled access to the application context
 * @author brendan
 *
 */
public class SpringContext {

	private static String contextPath = "spring.xml";
	
	private static ApplicationContext context = null;
	
	public static String getContextPath() {
		return contextPath;
	}
	
	public static void setContextPath(String path) {
		if (context != null) {
			throw new IllegalArgumentException("Context has already been created, cannot set path now");
		}
		contextPath = path;
	}
	
	public static ApplicationContext getContext() {
		if (context == null) {
			context = new ClassPathXmlApplicationContext(contextPath);
		}
		
		return context;
	}
}
