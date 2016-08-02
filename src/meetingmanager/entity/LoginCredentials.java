package meetingmanager.entity;

public class LoginCredentials {
	
	private String id;
	private String password;
	
	public LoginCredentials(String id, String password) {
		this.id = id;
		this.password = password;
	}
	
	public String getId() {
		return id;
	}
	
	public String getPassword() {
		return password;
	}
	
	@Override
	public boolean equals(Object obj) {
		LoginCredentials other = (LoginCredentials) obj;
		return (other.id.equals(this.id) && other.password.equals(this.password));
	}
}
