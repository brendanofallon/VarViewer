package varviewer.client.services;

import varviewer.shared.services.ServiceListResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ListServicesServiceAsync {
	void listServicesForUser(String username, AsyncCallback<ServiceListResult> result);
}
