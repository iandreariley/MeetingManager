package meetingmanager.exception;

public class MissingPrimaryKeyException extends Exception {

	private static final long serialVersionUID = 9207971600266185379L;
	public static final String MESSAGE =
			"The object provided to the database class is missing a valid primary key value.";
	
	public MissingPrimaryKeyException() {
		super(MESSAGE);
	}
}
