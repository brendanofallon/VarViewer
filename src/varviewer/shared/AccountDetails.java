package varviewer.shared;

import java.io.Serializable;
import java.util.List;

public class AccountDetails implements Serializable {

	String userName;
	List<String> roles;
	String lastLogin;
	String accountCreationDate;
	
	public AccountDetails() {
		
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getAccountCreationDate() {
		return accountCreationDate;
	}

	public void setAccountCreationDate(String accountCreationDate) {
		this.accountCreationDate = accountCreationDate;
	}
	
	
}
