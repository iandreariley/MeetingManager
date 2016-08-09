package meetingmanager.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import meetingmanager.entity.Room;
import meetingmanager.exception.MissingPrimaryKeyException;

public class RoomDatabase extends DatabaseConnection<Room> {
	
	public static final String LOCATION = "location";
	public static final String CAPACITY = "capacity";
	
	public RoomDatabase() throws SQLException {
		super();
	}

	@Override
	protected void init() throws SQLException {
		updateDatabase("CREATE TABLE IF NOT EXISTS room (location VARCHAR(100) PRIMARY KEY, capacity INT)");
	}
	
	public void addRoom(Room room) throws SQLException {
		updateDatabase(
			"INSERT INTO room VALUES (" +
			stringify(room.getLocation()) + LINE_SEP +
			room.getCapacity() + ")"
		);
	}
	
	public void updateRoom(Room room) throws SQLException, MissingPrimaryKeyException {
		checkPrimaryKey(room);
		updateDatabase(
			"UPDATE room SET "
			+ keyValue(CAPACITY, room.getCapacity()) + " "
			+ "WHERE " + keyValue(LOCATION, room.getLocation())
		);
	}
	
	public void deleteRoom(Room room) throws SQLException, MissingPrimaryKeyException {
		checkPrimaryKey(room);
		updateDatabase("DELETE FROM room WHERE " + keyValue(LOCATION, room.getLocation()));
	}

	@Override
	protected List<Room> toObject(ResultSet rs) throws SQLException {
		List<Room> results = new ArrayList<>();
		
		while(rs.next()) {
			Room next = new Room()
				.setLocation(rs.getString(LOCATION))
				.setCapacity(rs.getInt(CAPACITY));
			results.add(next);
		}
		
		return results;
	}

	@Override
	protected void checkPrimaryKey(Room room) throws MissingPrimaryKeyException {
		if(room.getLocation() == null || room.getLocation().length() < 1)
			throw new MissingPrimaryKeyException();
	}

}
