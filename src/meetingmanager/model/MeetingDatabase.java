package meetingmanager.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import meetingmanager.entity.Meeting;
import meetingmanager.exception.MissingPrimaryKeyException;

public class MeetingDatabase extends DatabaseConnection<Meeting> {

	public MeetingDatabase() throws SQLException {
		super();
	}

	@Override
	protected void init() throws SQLException {
		updateDatabase(
			"CREATE TABLE IF NOT EXISTS meeting ("
			+ "id INT PRIMARY KEY AUTO_INCREMENT,"
			+ "owner VARCHAR(50) NOT NULL, startTime DATETIME,"
			+ "endTime DATETIME, location VARCHAR(100),"
			+ "FOREIGN KEY (owner)  REFERENCES employee(login_id) ON DELETE CASCADE ON UPDATE CASCADE,"
			+ "FOREIGN KEY (location) REFERENCES room(location) ON DELETE SET NULL ON UPDATE CASCADE)"
		);
	}

	@Override
	protected List<Meeting> toObject(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void checkPrimaryKey(Meeting obj)
			throws MissingPrimaryKeyException {
		// TODO Auto-generated method stub
		
	}

}
