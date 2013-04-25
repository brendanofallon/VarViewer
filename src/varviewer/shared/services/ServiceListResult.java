package varviewer.shared.services;

import java.io.Serializable;
import java.util.List;

/**
 * Result of a ListServices service call
 * @author brendan
 *
 */
public class ServiceListResult implements Serializable {

	List<ServiceDescription> services = null;
	
	public ServiceListResult() {
		
	}

	public List<ServiceDescription> getServices() {
		return services;
	}

	public void setServices(List<ServiceDescription> services) {
		this.services = services;
	}
	
	
}
