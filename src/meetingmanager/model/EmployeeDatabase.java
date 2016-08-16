package meetingmanager.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import meetingmanager.entity.Employee;
import meetingmanager.exception.EntityNotFoundException;
import meetingmanager.exception.MissingPrimaryKeyException;

public class EmployeeDatabase extends DatabaseConnection<Employee> {
	
	public static final String LOGIN_ID = "login_id";
	public static final String NAME = "name";
	public static final String PASSWORD = "password";
	public static final String IS_ADMIN = "is_admin";
        private static EmployeeDatabase instance;
        
        static {
            try {
                instance = new EmployeeDatabase();
            } catch(SQLException e) {
                System.err.println("Uh Oh! Employee Database failed initialization!");
            }
        }
        
        public static EmployeeDatabase getInstance() {
            return instance;
        }
	
	private EmployeeDatabase() throws SQLException {
		super();
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
			"INSERT INTO employee VALUES (" +
			stringify(employee.getLoginId()) + LINE_SEP + 
			stringify(employee.getName()) +LINE_SEP +
			stringify(employee.getPassword()) + LINE_SEP +
			employee.isAdmin() + ")"
		);
	}
	
	public Employee getEmployee(String loginId) throws SQLException, EntityNotFoundException {
		List<Employee> results = queryDatabase("SELECT * FROM employee WHERE " + keyValue(LOGIN_ID, loginId));
		checkResultsNotEmpty(results);
		return results.get(0);
	}
	
	public void deleteEmployee(Employee employee) throws SQLException, MissingPrimaryKeyException {
		checkPrimaryKey(employee);
		updateDatabase("DELETE FROM employee WHERE login_id='" + employee.getLoginId() + "'");
	}
	
	public void updateEmployee(String loginId, Employee employee) throws SQLException {
		updateDatabase(
                    "UPDATE employee SET " +
                    keyValue(LOGIN_ID, employee.getLoginId()) + LINE_SEP +
                    keyValue(NAME, employee.getName()) + LINE_SEP +
                    keyValue(PASSWORD, employee.getPassword()) + LINE_SEP +
                    keyValue(IS_ADMIN, employee.isAdmin()) + " " +
                    "WHERE " + keyValue(LOGIN_ID, loginId)
		);
	}
	
        @Override
	public List<Employee> toObject(ResultSet rs) throws SQLException {
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

	@Override
	protected void checkPrimaryKey(Employee employee) throws MissingPrimaryKeyException {
		if(employee.getLoginId() == null || employee.getLoginId().length() < 1)
			throw new MissingPrimaryKeyException();
	}
}
