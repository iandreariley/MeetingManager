package meetingmanager.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import meetingmanager.entity.TimeSlot;
import meetingmanager.exception.MissingPrimaryKeyException;

public class EmployeeScheduleDatabase extends DatabaseConnection<TimeSlot> {


	public EmployeeScheduleDatabase() throws SQLException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void init() throws SQLException {
		// TODO Auto-generated method stub
		updateDatabase(
			"CREATE TABLE IF NOT EXISTS employee_schedule_line ("
			+ "employee_id VARCHAR(50),"
			+ "startTime DATETIME,"
			+ "endTime DATETIME, visible BOOL,"
			+ "FOREIGN KEY (employee_id) REFERENCES employee(login_id) ON DELETE CASCADE ON UPDATE CASCADE,"
			+ "PRIMARY KEY (employee_id, startTime, endTime)); "
		);
	}
	
	@Override
	protected List<TimeSlot> toObject(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void checkPrimaryKey(TimeSlot obj)
			throws MissingPrimaryKeyException {
		// TODO Auto-generated method stub
		
	}

}
