package meetingmanager.entity;

public class Employee extends ScheduledEntity {
	
	private String name;
	private String loginId;
	private boolean isAdmin;
	
	public String getName() {
		return name;
	}
	
	public Employee setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getLoginId() {
		return loginId;
	}
	
	public Employee setLoginId(String loginId) {
		this.loginId = loginId;
		return this;
	}
	
	public boolean isAdmin() {
		return isAdmin;
	}
	
	public Employee isAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
		return this;
	}
	
	public boolean isVisible(TimeSlot slot, boolean visible) {
		//TODO
		return true;
	}
}
