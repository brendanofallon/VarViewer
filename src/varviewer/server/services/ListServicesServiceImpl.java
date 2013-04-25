package varviewer.server.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import varviewer.client.services.ListServicesService;
import varviewer.shared.services.ServiceDescription;
import varviewer.shared.services.ServiceListResult;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ListServicesServiceImpl extends RemoteServiceServlet implements ListServicesService  {

	ServiceRepo services = new ServiceRepo();
	
	@Override
	public ServiceListResult listServicesForUser(String username) {
		ServiceListResult result = new ServiceListResult();
		List<ServiceDescription> authorizedServices = new ArrayList<ServiceDescription>();
		result.setServices(authorizedServices);
		
		if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (principal instanceof UserDetails) {
					UserDetails dets = (UserDetails)principal;
					System.out.println(dets.getUsername() + " : ");
					System.out.println("Not expired : " + dets.isAccountNonExpired() );
					
					//Only return list of services if account is enabled & not expired 
					if (dets.isAccountNonExpired() && dets.isEnabled() && dets.isAccountNonLocked()) {
						for(GrantedAuthority authority : dets.getAuthorities()) {
							String role = authority.getAuthority();
							System.out.println("\t" + role);
							List<ServiceDescription> servs = services.getServicesForRole(role);
							//Ensure each service only gets added once
							for(ServiceDescription service : servs) {
								if (! authorizedServices.contains(service)) {
									authorizedServices.add(service);
								}
							}
						}
					}
			}
		}
		
		return result;
	}

}
