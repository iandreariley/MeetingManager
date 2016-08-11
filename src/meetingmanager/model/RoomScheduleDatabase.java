package meetingmanager.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import meetingmanager.entity.Room;

import meetingmanager.entity.TimeSlot;
import meetingmanager.exception.MissingPrimaryKeyException;

public class RoomScheduleDatabase extends TimeSlotDatabase {

    public static final String ROOM = "location";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";

    public RoomScheduleDatabase() throws SQLException {
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
            + stringify(item.getStartTime()) + LINE_SEP
            + stringify(item.getEndTime()) + ")"
        );
    }

    public void deleteRoomScheduleItem(Room room, TimeSlot item) throws SQLException {
        updateDatabase(
            "DELETE FROM room_schedule_line WHERE "
            + keyValue(ROOM, room.getLocation()) + AND
            + keyValue(START_TIME, item.getStartTime()) + AND
            + keyValue(END_TIME, item.getEndTime()) + AND
        );
    }

    @Override
    protected void checkPrimaryKey(TimeSlot obj)
                    throws MissingPrimaryKeyException {
            // TODO Auto-generated method stub

    }

}
