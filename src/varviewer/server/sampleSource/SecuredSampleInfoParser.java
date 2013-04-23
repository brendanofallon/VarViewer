package varviewer.server.sampleSource;

import java.io.File;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import varviewer.shared.SampleInfo;

/**
 * Very similar to DefaultSampleInfoParser, this reads a 'reviewdir' to retrieve sample info,
 * but examines the sampleManifest for security info for ALLOWED_ROLES, and only returns the
 * info if the current user (from the SecurityContextHolder.getContext()...) has a role matching
 * one of the roles in ALLOWED_ROLES
 * @author brendan
 *
 */
public class SecuredSampleInfoParser extends DefaultSampleInfoParser {

	public static final String ALLOWED_ROLES = "allowed.roles";
	
	protected SampleInfo parseSampleInfo(File sampleRoot, Map<String, String> properties) {
		
		//First check to see if there's even a security context in place. If not, no sample info. 
		if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
			return null;
		}
		
		String allowedRoles = properties.get(ALLOWED_ROLES);
		if (allowedRoles == null) {
			return DirSampleSource.prohibitedInfo;
		}
		
		boolean canRead = false;
		String[] roles = allowedRoles.split(",");
		for(GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
			for(int i=0; i<roles.length; i++) {
				if (authority.getAuthority().equals(roles[i].trim())) {
					canRead = true;
					break;
				}
			}
		}
		
		if (canRead) {
			return super.parseSampleInfo(sampleRoot, properties);
		}
		
		return DirSampleSource.prohibitedInfo;
	}
}
