package varviewer.server.auth;

import java.io.File;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Loads user info from a text file. 
 * @author brendan
 *
 */
public class FileSystemUserDetails implements UserDetailsService {

	File sourceFile = null;
		
	
	public File getSourceFile() {
		return sourceFile;
	}


	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}


	@Override
	public UserDetails loadUserByUsername(String arg0)
			throws UsernameNotFoundException {
		
		
		return null;
	}

}
