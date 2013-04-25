package varviewer.shared.services;

import java.io.Serializable;

/**
 * 
 * @author brendan
 *
 */
public class ServiceDescription implements Serializable {

	String serviceID; //Unique ID for this service
	String serviceUserText; //User-readable descriptive name for service
	String className;
	
	public ServiceDescription() {
		//Required no-arg constructor
	}
	
	public ServiceDescription(String id, String userText, String className) {
		this.serviceID = id;
		this.serviceUserText = userText;
		this.className = className;
	}

	public String getServiceID() {
		return serviceID;
	}

	public String getServiceUserText() {
		return serviceUserText;
	}

	public String getClassName() {
		return className;
	}
	
	
}
