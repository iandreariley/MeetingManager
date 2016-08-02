package meetingmanager.entity;

public class Room extends ScheduledEntity {

	private String location;
	private int capacity;
	
	public String getLocation() {
		return location;
	}
	
	public Room setLocation(String location) {
		this.location = location;
		return this;
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public Room setCapacity(int capacity) {
		this.capacity = capacity;
		return this;
	}
}
