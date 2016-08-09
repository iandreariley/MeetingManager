package meetingmanager.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import meetingmanager.entity.TimeSlot;
import meetingmanager.exception.MissingPrimaryKeyException;

public class RoomScheduleDatabase extends TimeSlotDatabase {

	public RoomScheduleDatabase() throws SQLException {
		super();
	}

	@Override
	protected void init() throws SQLException {
		updateDatabase(
			"CREATE TABLE IF NOT EXISTS room_schedule_line("
			+ "location VARCHAR(100),"
			+ "startTime DATETIME,"
			+ "endTime DATETIME,"
			+ "FOREIGN KEY (location) REFERENCES room(location) ON DELETE CASCADE ON UPDATE CASCADE,"
			+ "PRIMARY KEY (location, startTime, endTime))"
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
