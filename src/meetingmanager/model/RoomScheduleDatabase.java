package meetingmanager.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import meetingmanager.entity.Room;

import meetingmanager.entity.TimeSlot;
import meetingmanager.exception.MissingPrimaryKeyException;

public class RoomScheduleDatabase extends TimeSlotDatabase {

    public static final String ROOM = "location";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    private static RoomScheduleDatabase instance;

    static {
        try {
            instance = new RoomScheduleDatabase();
        } catch(SQLException e) {
            System.err.println("Uh Oh! RoomScheduleDatabase failed initialization!");
        }
    }

    public static RoomScheduleDatabase getInstance() {
        return instance;
    }

    private RoomScheduleDatabase() throws SQLException {
        super();
    }

    @Override
    protected void init() throws SQLException {
        updateDatabase(
            "CREATE TABLE IF NOT EXISTS room_schedule_line("
            + "location VARCHAR(100),"
            + "title VARCHAR(200),"
            + "startTime DATETIME,"
            + "endTime DATETIME,"
            + "FOREIGN KEY (location) REFERENCES room(location) ON DELETE CASCADE ON UPDATE CASCADE,"
            + "PRIMARY KEY (location, startTime, endTime))"
        );
    }

    public void addRoomScheduleItem(Room room, TimeSlot item) throws SQLException {
        updateDatabase(
            "INSERT INTO room_schedule_line VALUES ( "
            + stringify(room.getLocation()) + LINE_SEP
            + stringify(item.getTitle()) + LINE_SEP
            + stringify(item.getSQLFormattedStartTime()) + LINE_SEP
            + stringify(item.getSQLFormattedEndTime()) + ")"
        );
    }

    public void deleteRoomScheduleItem(Room room, TimeSlot item) throws SQLException {
        updateDatabase(
            "DELETE FROM room_schedule_line WHERE "
            + keyValue(ROOM, room.getLocation()) + AND
            + keyValue(START_TIME, item.getSQLFormattedStartTime()) + AND
            + keyValue(END_TIME, item.getSQLFormattedEndTime())
        );
    }
    
    public List<TimeSlot> getRoomSchedule(Room room) throws SQLException {
        return queryDatabase(
            "SELECT * FROM room_schedule_line WHERE "
            + keyValue(ROOM, room.getLocation())
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
                .setStartTime(rs.getDate("startTime"))
                .setEndTime(rs.getDate("endTime"))
                .isVisible(true);
            results.add(result);
        }
        return results;
    }

}
