package meetingmanager.entity;

import java.util.SortedSet;

import meetingmanager.exception.InviteeNotFoundException;

public class Meeting extends TimeSlot {

	private Room location;
	private SortedSet<Employee> invited;
	private SortedSet<Employee> attending;
	private Employee owner;
	
	public Meeting() {
		super();
	}
	
        public Meeting(TimeSlot timeSlot) {
            super(timeSlot);
            this.isVisible(true);
        }
        
	public void invite(Employee invitee) {
		if(!invitee.equals(owner))
			invited.add(invitee);
	}
	
	public boolean uninvite(Employee invitee) {
		return invited.remove(invitee);	
	}
	
	public void isAttending(Employee invitee) throws InviteeNotFoundException {
		if(invited.remove(invitee)) {
			attending.add(invitee);
		} else {
			throw new InviteeNotFoundException();
		}
	}
	
	public Room getLocation() {
		return location;
	}
	
	public Meeting setLocation(Room location) {
		this.location = location;
		return this;
	}
	
	public SortedSet<Employee> getInvited() {
		return invited;
	}
	
	public Meeting setInvited(SortedSet<Employee> invited) {
		this.invited = invited;
		return this;
	}
	
	public SortedSet<Employee> getAttending() {
		return attending;
	}
	
	public Meeting setAttending(SortedSet<Employee> attending) {
		this.attending = attending;
		return this;
	}
	
	public Employee getOwner() {
		return owner;
	}
	
	public Meeting setOwner(Employee owner) {
		this.owner = owner;
		return this;
	}
}
