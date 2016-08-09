package meetingmanager.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import meetingmanager.entity.Meeting;
import meetingmanager.exception.MissingPrimaryKeyException;

public class MeetingDatabase extends DatabaseConnection<Meeting> {

	public MeetingDatabase() throws SQLException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void init() throws SQLException {
		// TODO Auto-generated method stub
		
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
