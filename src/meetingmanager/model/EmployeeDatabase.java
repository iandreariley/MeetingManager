package meetingmanager.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import meetingmanager.entity.Employee;

public class EmployeeDatabase extends DatabaseConnection<Employee> {
	
	public static final String LOGIN_ID = "login_id";
	public static final String NAME = "name";
	public static final String PASSWORD = "password";
	public static final String IS_ADMIN = "is_admin";
	
	public EmployeeDatabase() throws SQLException {
		init();
	}
	
	public void init() throws SQLException {
		updateDatabase(
			"CREATE TABLE IF NOT EXISTS employee (" +
			"login_id VARCHAR(50) PRIMARY KEY, " +
			"name VARCHAR(100), " +
			"password VARCHAR(50), " +
			"is_admin BOOL)"
		);
	}

	public void addEmployee(Employee employee) throws SQLException {
		updateDatabase(
			"INSERT INTO employee VALUES ('" +
			employee.getLoginId() + "', '" + 
			employee.getName() +"', " +
			employee.isAdmin()
		);
	}
	
	protected List<Employee> toObject(ResultSet rs) throws SQLException {
		List<Employee> results = new ArrayList<>();
		
		while(rs.next()) {
			Employee next = new Employee()
				.setLoginId(rs.getString(LOGIN_ID))
				.setName(rs.getString(NAME))
				.setPassword(rs.getString(PASSWORD))
				.isAdmin(rs.getBoolean(IS_ADMIN));
			results.add(next);
		}
		
		return results;
	}
}
