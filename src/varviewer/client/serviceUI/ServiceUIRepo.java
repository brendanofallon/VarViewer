package varviewer.client.serviceUI;


import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author brendan
 *
 */
public class ServiceUIRepo {

	private List<Entry> services = new ArrayList<Entry>();
	
	public ServiceUIRepo() {
		addServiceUI("view.samples", new ViewSamples());
	}
	
	protected void addServiceUI(String id, ServiceUI service) {
		services.add(new Entry(id, service));
	}
	
	public ServiceUI getService(String id) {
		for(Entry entry : services) {
			if (entry.id.equals(id)) {
				return entry.service;
			}
		}
		return null;
	}
	
	class Entry {
		final String id;
		final ServiceUI service;
		
		public Entry(String id, ServiceUI service) {
			this.id = id;
			this.service = service;
		}
		
	}
}
