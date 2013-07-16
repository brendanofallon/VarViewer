package varviewer.server.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import varviewer.shared.services.ServiceDescription;

public class ServiceRepo {

	private List<ServiceEntry> availableServices = new ArrayList<ServiceEntry>();
	
	public ServiceRepo() {
		addService("view.samples", "View samples", "varviewer.client.serviceUI.SampleViewUI", Arrays.asList(new String[]{"ROLE_USER"}));
		addService("account.settings", "Account settings", "varviewer.client.serviceUI.AccountSettingsUI", Arrays.asList(new String[]{"ROLE_USER"}));
//		addService("admin.users", "Administer users", "varviewer.client.serviceUI.AdminUsers", Arrays.asList(new String[]{"ROLE_ADMIN"}));
//		addService("admin.all", "Administer everything", "varviewer.client.serviceUI.AdminUsers", Arrays.asList(new String[]{"ROLE_ADMIN"}));
	}
	
	public void addService(String id, String userText, String className, List<String> roles) {
		if (getEntryForID(id) != null) {
			throw new IllegalArgumentException("A service with id " + id + " already exists");
		}
		ServiceEntry entry = new ServiceEntry(new ServiceDescription(id, userText, className), roles);
		availableServices.add(entry);
	}
	
	public ServiceEntry getEntryForID(String id) {
		for(ServiceEntry entry : availableServices) {
			if (entry.serviceDesc.getServiceID().equals(id)) {
				return entry;
			}
		}
		
		return null;
	}
	
	public List<ServiceDescription> getServicesForRole(String role) {
		List<ServiceDescription> okServices = new ArrayList<ServiceDescription>();
		for(ServiceEntry entry : availableServices) {
			if (serviceOKForRole(role, entry)) {
				okServices.add(entry.serviceDesc);
			}
		}
		return okServices;
	}
	
	private boolean serviceOKForRole(String role, ServiceEntry entry) {
		if (entry.roles.contains(role)) {
			return true;
		}
		return false;
	}
	
	class ServiceEntry {
		
		ServiceDescription serviceDesc;
		List<String> roles;
		
		public ServiceEntry(ServiceDescription desc, List<String> roles) {
			this.serviceDesc = desc;
			this.roles = roles;
		}
	}
}
