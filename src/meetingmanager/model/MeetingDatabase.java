package meetingmanager.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import meetingmanager.entity.Meeting;
import meetingmanager.entity.Employee;
import meetingmanager.exception.EntityNotFoundException;
import meetingmanager.exception.MissingPrimaryKeyException;
import static meetingmanager.model.DatabaseConnection.connect;

public class MeetingDatabase extends DatabaseConnection<Meeting> {
    
        public static final String LOCATION = "location";
        public static final String START_TIME = "startTime";
        public static final String END_TIME = "endTime";
        public static final String OWNER = "owner";
        public static final String CONFIRMED = "confirmed";
        public static final String IS_UPDATE = "is_update";
        private static MeetingDatabase instance;
        
        static {
            try {
                instance = new MeetingDatabase();
            } catch(SQLException e) {
                System.err.println("Uh Oh! Meeting Database failed initialization!");
            }
        }
        
        public static MeetingDatabase getInstance() {
            return instance;
        }

	private MeetingDatabase() throws SQLException {
		super();
	}

	@Override
	protected void init() throws SQLException {
            updateDatabase(
                "CREATE TABLE IF NOT EXISTS meeting ( "
                + "owner VARCHAR(50), "
                + "startTime BIGINT, "
                + "endTime BIGINT, "
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
                "INSERT INTO meeting VALUES( "
                + stringify(meeting.getOwner().getLoginId()) + LINE_SEP
                + meeting.getStartTimeStamp() + LINE_SEP
                + meeting.getEndTimeStamp() + LINE_SEP
                + stringify(meeting.getLocation().getLocation()) + LINE_SEP
                + stringify(meeting.getTitle()) + ")"
            );
        }
        
        public void deleteMeeting(Meeting meeting) throws SQLException {
            updateDatabase(
               "DELETE FROM meeting WHERE ( "
               + keyValue(OWNER, meeting.getOwner().getLoginId()) + AND
               + keyValue(START_TIME, meeting.getStartTime()) + AND
               + keyValue(END_TIME, meeting.getEndTime())
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
        
        public Map<Meeting, Boolean> getUnconfirmedMeetings(Employee invitee) throws SQLException {
            String query = 
               "SELECT m." + LOCATION + ", i." + START_TIME + ", i." + END_TIME + ", i." + OWNER + " "
                + "FROM meeting AS m, invitation_status AS i "
                + "WHERE "
                + keyValue("m." + OWNER, "i." + OWNER) + AND
                + keyValue("m." + START_TIME, "i." + START_TIME) + AND
                + keyValue("m." + END_TIME, "i." + END_TIME) + AND
                + "i." + CONFIRMED + " IS NULL";
            
            
            Connection connection = connect();
            Statement statement = connection.createStatement();
            Map<Meeting, Boolean> ret = convertToMap(statement.executeQuery(query));
            connection.close();
            return ret;
        }
        
        public List<Meeting> getOwnedMeetings(Employee owner) throws SQLException {
            return queryDatabase(
                "SELECT * FROM meeting WHERE owner=" + stringify(owner.getLoginId())
            );
        }
        
        private Map<Meeting, Boolean> convertToMap(ResultSet rs) throws SQLException {
            Map<Meeting, Boolean> updateMap = new HashMap<>();

            try {
                while(rs.next()) {
                    Meeting next = new Meeting()
                        .setLocation(RoomDatabase.getInstance().getRoom(rs.getString(LOCATION)))
                        .setOwner(EmployeeDatabase.getInstance().getEmployee(rs.getString(OWNER)));
                    next.setStartTime(rs.getLong(START_TIME));
                    next.setEndTime(rs.getLong(END_TIME));
                    Boolean isUpdate = rs.getBoolean(IS_UPDATE);
                    updateMap.put(next, isUpdate);
                }

                return updateMap;
            } catch(EntityNotFoundException e) {
                System.err.println(e.getMessage());
            }
            return null;
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
                        .setLocation(RoomDatabase.getInstance().getRoom(rs.getString(LOCATION)))
                        .setOwner(EmployeeDatabase.getInstance().getEmployee(rs.getString(OWNER)));
                    next.setStartTime(rs.getLong(START_TIME));
                    next.setEndTime(rs.getLong(END_TIME));
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
