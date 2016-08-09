package meetingmanager.exception;

public class EntityNotFoundException extends Exception {

	private static final long serialVersionUID = 7758902680809306069L;
	private static final String MESSAGE = "Entity could not be found in database.";
	
	public EntityNotFoundException() {
		super(MESSAGE);
	}

}
