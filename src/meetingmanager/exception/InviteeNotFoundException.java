package meetingmanager.exception;

public class InviteeNotFoundException extends Exception {
	
	public static final String MESSAGE =
			"Oh no! The person being added to the attendance list has not been invited to the meeting!";
	
	public InviteeNotFoundException() {
		super(MESSAGE);
	}
}
