package meetingmanager.model;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import meetingmanager.entity.TimeSlot;

public abstract class TimeSlotDatabase extends DatabaseConnection<TimeSlot> {

	public TimeSlotDatabase() throws SQLException {
		super();
		// TODO Auto-generated constructor stub
	}

}
