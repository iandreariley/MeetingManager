package meetingmanager.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import meetingmanager.entity.Employee;

import meetingmanager.entity.TimeSlot;
import meetingmanager.exception.MissingPrimaryKeyException;

public class EmployeeScheduleDatabase extends TimeSlotDatabase {

    public static final String EMPLOYEE = "employee_id";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    private static EmployeeScheduleDatabase instance;

    static {
        try {
            instance = new EmployeeScheduleDatabase();
        } catch(SQLException e) {
            System.err.println("Uh Oh! EmployeeScheduleDatabase failed initialization!");
        }
    }

    public static EmployeeScheduleDatabase getInstance() {
        return instance;
    }
            
	private EmployeeScheduleDatabase() throws SQLException {
		super();
	}

	@Override
	protected void init() throws SQLException {
		// TODO Auto-generated method stub
		updateDatabase(
			"CREATE TABLE IF NOT EXISTS employee_schedule_line ("
			+ "employee_id VARCHAR(50),"
                        + "title VARCHAR(200),"
			+ "startTime BIGINT,"
			+ "endTime BIGINT, "
                        + "visible BOOL,"
			+ "FOREIGN KEY (employee_id) REFERENCES employee(login_id) ON DELETE CASCADE ON UPDATE CASCADE,"
			+ "PRIMARY KEY (employee_id, startTime, endTime)); "
		);
	}
        
        public void addEmployeeScheduleItem(Employee employee, TimeSlot item) throws SQLException {
            updateDatabase(
                "INSERT INTO employee_schedule_line VALUES ( "
                + stringify(employee.getLoginId()) + LINE_SEP
                + stringify(item.getTitle()) + LINE_SEP
                + item.getStartTimeStamp() + LINE_SEP
                + item.getEndTimeStamp() + LINE_SEP
                + item.isVisible() + ")"
            );
        }
        
        public void deleteEmployeeScheduleItem(Employee employee, TimeSlot item) throws SQLException {
            updateDatabase(
                "DELETE FROM employee_schedule_line WHERE "
                + keyValue(EMPLOYEE, employee.getLoginId()) + AND
                + keyValue(START_TIME, item.getStartTime()) + AND
                + keyValue(END_TIME, item.getEndTime())
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
        
        @Override
        public List<TimeSlot> toObject(ResultSet rs) throws SQLException{
            List<TimeSlot> results = new ArrayList<>();
            while(rs.next()) {
                TimeSlot result = new TimeSlot()
                    .setTitle(rs.getString("title"))
                    .setStartTime(rs.getLong("startTime"))
                    .setEndTime(rs.getLong("endTime"))
                    .isVisible(rs.getBoolean("visible"));
                results.add(result);
            }
            return results;
        }

}
