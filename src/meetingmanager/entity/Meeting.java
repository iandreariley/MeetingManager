package meetingmanager.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import meetingmanager.exception.InviteeNotFoundException;

public class Meeting extends TimeSlot {

    private static final Comparator<Employee> ALPHA_EMPLOYEE_SORTER;    
    
	private Room location;
	private SortedSet<Employee> invited;
	private SortedSet<Employee> attending;
	private Employee owner;
        
        static {
            ALPHA_EMPLOYEE_SORTER = new Comparator<Employee>(){
                public int compare(Employee e1, Employee e2) {
                    return e1.getName().compareTo(e2.getName());
                }
            };
        }
	
        public Meeting(Meeting otherMeeting) {
            super(otherMeeting);
            this.attending = setCopy(otherMeeting.attending);
            this.invited = setCopy(otherMeeting.invited);
            this.owner = otherMeeting.owner;
            this.location = otherMeeting.location;
        }
        
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
        
        public int size() {
            if(invited == null)
                return 1;
            return invited.size() + 1;
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
        
        public void setTime(TimeSlot time) {
            setStartTime(time.getStartTime());
            setEndTime(time.getEndTime());
        }
	
	public Meeting setInvited(SortedSet<Employee> invited) {
		this.invited = invited;
		return this;
	}
        
        public Meeting setInvited(Employee... employees) {
            this.invited = new TreeSet<>(ALPHA_EMPLOYEE_SORTER);
            this.invited.addAll(Arrays.asList(employees));
            return this;
        }
        
        public Meeting setInvited(Collection<Employee> invited) {
            
            this.invited = new TreeSet<>(ALPHA_EMPLOYEE_SORTER);
            this.invited.addAll(invited);
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
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if (obj instanceof Meeting) {
            Meeting other = (Meeting) obj;
            return super.equals(obj) &&
                (this.owner == other.owner || other.owner != null  && other.owner.equals(this.owner));
        } else { // check whether it is a timeslot
            return super.equals(obj);
        }
    }
    
    private SortedSet<Employee> setCopy(SortedSet<Employee> employees) {
        if(employees == null)
            return null;
        
        TreeSet<Employee> copy = new TreeSet<>(ALPHA_EMPLOYEE_SORTER);
        copy.addAll(employees);
        
        return copy;
    }
}
