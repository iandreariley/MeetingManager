package meetingmanager.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import meetingmanager.entity.Meeting;
import meetingmanager.entity.Employee;
import meetingmanager.exception.EntityNotFoundException;
import meetingmanager.exception.MissingPrimaryKeyException;

public class MeetingDatabase extends DatabaseConnection<Meeting> {
    
        public static final String LOCATION = "location";
        public static final String START_TIME = "startTime";
        public static final String END_TIME = "endTime";
        public static final String OWNER = "owner";
        public static final String CONFIRMED = "confirmed";
        public static final String IS_UPDATE = "is_update";
    
        private final RoomDatabase roomDatabase;
        private final EmployeeDatabase employeeDatabase;

	public MeetingDatabase() throws SQLException {
		super();
                roomDatabase = new RoomDatabase();
                employeeDatabase = new EmployeeDatabase();
	}

	@Override
	protected void init() throws SQLException {
            updateDatabase(
                "CREATE TABLE IF NOT EXISTS meeting ( "
                + "owner VARCHAR(50), "
                + "startTime DATETIME, "
                + "endTime DATETIME, "
                + "location VARCHAR(100), "
                + "title VARCHAR(100), "
                + "FOREIGN KEY (owner) REFERENCES employee(login_id) "
                + "ON DELETE CASCADE ON UPDATE CASCADE, "
                + "FOREIGN KEY (location) REFERENCES room(location) "
                + "ON DELETE SET NULL ON UPDATE CASCADE, "
                + "PRIMARY KEY (owner, startTime, endTime));"
            );
	}
        
        public void addMeeting(Meeting meeting) throws SQLException {
            updateDatabase(
                "INSERT INTO TABLE meeting ( "
                + stringify(meeting.getOwner().getLoginId()) + LINE_SEP
                + stringify(meeting.getStartTime().toString()) + LINE_SEP
                + stringify(meeting.getEndTime().toString()) + LINE_SEP
                + stringify(meeting.getLocation().getLocation()) + ")"
            );
        }
        
        public void updateInvitationTime(Meeting meeting, Date newStartTime, Date newEndTime) throws SQLException {
            updateDatabase(
                "UPDATE meeting SET "
                + keyValue(START_TIME, newStartTime) + LINE_SEP
                + keyValue(END_TIME, newEndTime) + " "
                + "WHERE "
                + keyValue(OWNER, meeting.getOwner().getLoginId()) + AND
                + keyValue(START_TIME, meeting.getStartTime()) + AND
                + keyValue(END_TIME, meeting.getEndTime())
            );
        }
        
        public void updateLocation(Meeting meeting) throws SQLException {
            updateDatabase(
                "UPDATE meeting SET "
                + keyValue(LOCATION, meeting.getLocation().getLocation()) + " "
                + "WHERE "
                + keyValue(OWNER, meeting.getOwner().getLoginId()) + AND
                + keyValue(START_TIME, meeting.getStartTime()) + AND
                + keyValue(END_TIME, meeting.getEndTime())
            );
        }
        
        public List<Meeting> getUnconfirmedMeetings(Employee invitee) throws SQLException {
            return queryDatabase(
                "SELECT m." + LOCATION + ", i." + START_TIME + ", i." + END_TIME + ", i." + OWNER + " "
                + "FROM meeting AS m, invitation_status AS i "
                + "WHERE "
                + keyValue("m." + OWNER, "i." + OWNER) + AND
                + keyValue("m." + START_TIME, "i." + START_TIME) + AND
                + keyValue("m." + END_TIME, "i." + END_TIME) + AND
                + "i." + CONFIRMED + " IS NULL"
            );
        }
        
        public List<Meeting> getUnconfirmedUpdates(Employee invitee) throws SQLException {
            return queryDatabase(
                "SELECT m." + LOCATION + ", i." + START_TIME + ", i." + END_TIME + ", i." + OWNER + " "
                + "FROM meeting AS m, invitation_status AS i "
                + "WHERE "
                + keyValue("m." + OWNER, "i." + OWNER) + AND
                + keyValue("m." + START_TIME, "i." + START_TIME) + AND
                + keyValue("m." + END_TIME, "i." + END_TIME) + AND
                + keyValue("i." + IS_UPDATE, true) + AND
                + "i." + CONFIRMED + " IS NULL"
            );
        }

	@Override
	protected List<Meeting> toObject(ResultSet rs) throws SQLException {
            List<Meeting> results = new ArrayList<>();

            try {
                while(rs.next()) {
                    Meeting next = new Meeting()
                        .setLocation(roomDatabase.getRoom(rs.getString(LOCATION)))
                        .setOwner(employeeDatabase.getEmployee(rs.getString(OWNER)));
                    next.setStartTime(rs.getDate(START_TIME));
                    next.setEndTime(rs.getDate(END_TIME));
                    results.add(next);
                }

                return results;
            } catch(EntityNotFoundException e) {
                System.err.println(e.getMessage());
            }
            return null;
	}

	@Override
	protected void checkPrimaryKey(Meeting obj)
			throws MissingPrimaryKeyException {
		
	}

}
