package varviewer.server.auth;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Interface to a password file. We use jBCrypt to encrypt passwords. This can also act as a spring
 * PasswordEncoder
 * @author brendan
 *
 */
public class PasswordStore implements PasswordEncoder {

	private static final String passwordFilename = "vv.passwd";
	private static final String pathToPasswordFile = System.getProperty("user.home") + "/" + passwordFilename;
	
	public PasswordStore() {
		
	}
	
	@Override
	public String encode(CharSequence pw) {
		return BCrypt.hashpw(pw.toString(), BCrypt.gensalt());
	}

	@Override
	public boolean matches(CharSequence raw, String encoded) {
		return BCrypt.checkpw(raw.toString(), encoded);
	}
	
	public static boolean checkPassword(String username, String candidatePassword) {
		try {
			Map<String, String> passwords = loadPasswordFile();
			String hashed = passwords.get(username);
			if (BCrypt.checkpw(candidatePassword, hashed)) {
				Logger.getLogger(PasswordStore.class).info("User " + username + " successful authentication");
				return true;
			}
			else {
				Logger.getLogger(PasswordStore.class).info("User " + username + " failed authentication");
				return false;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			Logger.getLogger(PasswordStore.class).error("Could not open password file : " + e.getMessage() );
			return false;
		}	
	}

	private static Map<String, String> loadPasswordFile() throws IOException {
		Logger.getLogger(PasswordStore.class).info("Loading password file from : " + pathToPasswordFile);
		BufferedReader reader = new BufferedReader(new FileReader(pathToPasswordFile));
		String line = reader.readLine();
		Map<String, String> map = new HashMap<String, String>();
		while(line != null) {
			int index = line.indexOf(":");
			if (index < 0) {
				Logger.getLogger(PasswordStore.class).warn("Invalid line found in password file : " + line );
			}
			else {
				String username = line.substring(0, index);
				String hashed = line.substring(index+1, line.length());
				map.put(username, hashed);
			}
			line = reader.readLine();
		}
		
		reader.close();
		return map;
	}
	


	
	public static void main(String[] args) {
		if (args.length==0) {
			System.out.println("Enter a password to hash");
			return;
		}
		String hashed = BCrypt.hashpw(args[0], BCrypt.gensalt());
		System.out.println( hashed );
	}



	

}

