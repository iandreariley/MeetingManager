package meetingmanager.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import meetingmanager.entity.Employee;

import meetingmanager.entity.TimeSlot;
import meetingmanager.exception.MissingPrimaryKeyException;

public class EmployeeScheduleDatabase extends TimeSlotDatabase {

    public static final String EMPLOYEE = "employee_id";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
            
	public EmployeeScheduleDatabase() throws SQLException {
		super();
	}

	@Override
	protected void init() throws SQLException {
		// TODO Auto-generated method stub
		updateDatabase(
			"CREATE TABLE IF NOT EXISTS employee_schedule_line ("
			+ "employee_id VARCHAR(50),"
                        + "title VARCHAR(200),"
			+ "startTime DATETIME,"
			+ "endTime DATETIME, visible BOOL,"
			+ "FOREIGN KEY (employee_id) REFERENCES employee(login_id) ON DELETE CASCADE ON UPDATE CASCADE,"
			+ "PRIMARY KEY (employee_id, startTime, endTime)); "
		);
	}
        
        public void addEmployeeScheduleItem(Employee employee, TimeSlot item) throws SQLException {
            updateDatabase(
                "INSERT INTO employee_schedule_line VALUES ( "
                + stringify(employee.getLoginId()) + LINE_SEP
                + stringify(item.getTitle()) + LINE_SEP
                + stringify(item.getStartTime()) + LINE_SEP
                + stringify(item.getEndTime()) + ")"
            );
        }
        
        public void deleteEmployeeScheduleItem(Employee employee, TimeSlot item) throws SQLException {
            updateDatabase(
                "DELETE FROM employee_schedule_line WHERE "
                + keyValue(EMPLOYEE, employee.getLoginId()) + AND
                + keyValue(START_TIME, item.getStartTime()) + AND
                + keyValue(END_TIME, item.getEndTime()) + AND
            );
        }

        public List<TimeSlot> getEmployeeSchedule(Employee employee) throws SQLException {
            return queryDatabase(
                "SELECT * FROM employee_schedule_line WHERE "
                + keyValue(EMPLOYEE, employee.getLoginId())
            );
        }
	@Override
	protected void checkPrimaryKey(TimeSlot obj)
			throws MissingPrimaryKeyException {
		// TODO Auto-generated method stub
		
	}

}
