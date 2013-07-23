package varviewer.server.auth;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import varviewer.client.services.AccountDetailsService;
import varviewer.shared.AccountDetails;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Retrieves account details from the UserDetailsService bean and returns the information in
 * an AccountDetails object. 
 * @author brendan
 *
 */
public class AccountDetailsServiceImpl extends RemoteServiceServlet implements AccountDetailsService {

	@Override
	public AccountDetails getAccountDetails(String username) {
		AccountDetails result = new AccountDetails();
		
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");			
	        UserDetailsService userService = (UserDetailsService) context.getBean("userService");
	        UserDetails details = userService.loadUserByUsername(username);
	        result.setUserName(username);
	        
	        List<String> roles = new ArrayList<String>();
	        for(GrantedAuthority role : details.getAuthorities()) {
	        	roles.add(role.toString());
	        }
	        result.setRoles(roles);
	        
	      } catch(UsernameNotFoundException e) {
	        Logger.getLogger(getClass()).info("Error retrieving account details for user " + username + " cause: " + e.getLocalizedMessage());
	      }
		
		return result;
	}

}
