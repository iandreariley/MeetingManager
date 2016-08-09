package meetingmanager.exception;

public class InviteeNotFoundException extends Exception {

	private static final long serialVersionUID = 2984415438082394681L;
	public static final String MESSAGE =
			"Oh no! The person being added to the attendance list has not been invited to the meeting!";
	
	public InviteeNotFoundException() {
		super(MESSAGE);
	}
}
