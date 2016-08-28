package meetingmanager.entity;

import java.util.Collection;
import java.util.TreeSet;

public abstract class ScheduledEntity {

	private TreeSet<TimeSlot> schedule;
	
	protected ScheduledEntity() {
		schedule = new TreeSet<TimeSlot>();
	}
	
	public void setSchedule(TreeSet<TimeSlot> schedule) {
		this.schedule = schedule;
	}
        
        public void setSchedule(Collection<TimeSlot> schedule) {
            this.schedule = new TreeSet<>();
            this.schedule.addAll(schedule);
        }

	public TreeSet<TimeSlot> getSchedule() {
		return schedule;
	}

	public boolean isAvailable(TimeSlot slot) {
		TimeSlot after = schedule.ceiling(slot);
		TimeSlot before = schedule.floor(slot);
		
		return
			(after == null || !slot.overlaps(after)) &&
			(before == null || !slot.overlaps(before));
	}

	public boolean addEvent(TimeSlot slot) {
		return schedule.add(slot);
	}

	public boolean removeEvent(TimeSlot slot) {
		return schedule.remove(slot);
	}
}
