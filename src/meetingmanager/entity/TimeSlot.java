package meetingmanager.entity;

import java.util.Date;

public class TimeSlot implements Comparable<TimeSlot> {

	private String title;
	private Date startTime;
	private Date endTime;
	private boolean isVisible;

	public TimeSlot() {
		isVisible = true;
	}
        
        public TimeSlot(TimeSlot other) {
            this.startTime = other.startTime;
            this.endTime = other.endTime;
            this.isVisible = other.isVisible;
            this.title = other.title;
        }

	public boolean overlaps(TimeSlot other) {
		if (overlaps(this, other) || overlaps(other, this))
			return true;
		return false;
	}

	private static boolean overlaps(TimeSlot first, TimeSlot second) {
		return afterOrEqual(first.startTime, second.startTime)
				&& first.startTime.before(second.endTime);
	}

	private static boolean afterOrEqual(Date op1, Date op2) {
		return op1.after(op2) || op1.equals(op2);
	}

	public String getTitle() {
		return title;
	}

	public TimeSlot setTitle(String title) {
		this.title = title;
		return this;
	}

	public Date getStartTime() {
		return startTime;
	}

	public TimeSlot setStartTime(Date startTime) {
		this.startTime = startTime;
		return this;
	}

	public Date getEndTime() {
		return endTime;
	}

	public TimeSlot setEndTime(Date endTime) {
		this.endTime = endTime;
		return this;
	}

	public final boolean isVisible() {
		return isVisible;
	}

	public final TimeSlot isVisible(boolean isVisible) {
		this.isVisible = isVisible;
		return this;
	}
	
        @Override
	public int compareTo(TimeSlot other) {
		return other.startTime.compareTo(this.startTime);
	}
        
        @Override
        public boolean equals(Object other) {
            if(other == null) return false;
            if(other == this) return true;
            if(!(other instanceof TimeSlot)) return false;
            
            TimeSlot otherTimeSlot = (TimeSlot) other;
            return otherTimeSlot.endTime.equals(this.endTime) &&
                   otherTimeSlot.startTime.equals(this.startTime);
        }
}
