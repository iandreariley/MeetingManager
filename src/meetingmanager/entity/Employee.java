package meetingmanager.entity;

public class Employee extends ScheduledEntity {
	
	private String name;
	private String loginId;
	private String password;
	private boolean isAdmin;
        
        public Employee(){}
        
        public Employee(Employee other) {
            this.name = other.name;
            this.loginId = other.loginId;
            this.password = other.password;
            this.isAdmin = other.isAdmin;
        }
	
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

	public Employee setPassword(String password) {
		this.password = password;
		return this;
	}
	
	public String getPassword() {
		return password;
	}
        
        @Override
        public boolean equals(Object obj) {
            if(this == obj)
                return true;
            if(obj == null || this.getClass() != obj.getClass())
                return false;
            
            Employee other = (Employee) obj;
            
            return this.loginId == other.loginId || (this.loginId != null && this.loginId.equals(other.loginId));
        }
        
        @Override
        public int hashCode() {
            int hash = 5;
            return 13 * hash + (loginId == null ? 0 : loginId.hashCode());
        }
}
