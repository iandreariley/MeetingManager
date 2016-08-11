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
        
        @Override
        public List<TimeSlot> toObject(ResultSet rs) throws SQLException{
            List<TimeSlot> results = new ArrayList<>();
            while(rs.next()) {
                TimeSlot result = new TimeSlot()
                    .setTitle(rs.getString("title"))
                    .setStartTime(rs.getDate("startTime"))
                    .setEndTime(rs.getDate("endTime"))
                    .isVisible(rs.getBoolean("visible"));
                results.add(result);
            }
            return results;
        }

}
