package meetingmanager.control;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import meetingmanager.entity.Employee;
import meetingmanager.entity.Room;
import meetingmanager.entity.TimeSlot;
import meetingmanager.entity.Meeting;
import meetingmanager.entity.Notification;
import meetingmanager.exception.EntityNotFoundException;
import meetingmanager.exception.InviteeNotFoundException;
import meetingmanager.model.EmployeeDatabase;
import meetingmanager.model.EmployeeScheduleDatabase;
import meetingmanager.model.RoomScheduleDatabase;
import meetingmanager.model.InvitationStatusDatabase;
import meetingmanager.model.MeetingDatabase;
import meetingmanager.model.NotificationDatabase;
import meetingmanager.model.RoomDatabase;
import static meetingmanager.utils.Utils.*;

public class MeetingControl {
    
    public static void addMeeting(Meeting meeting, boolean isUpdate) throws SQLException {
        SortedSet<Employee> invitees = meeting.getInvited();
        Room location = meeting.getLocation();
        
        MeetingDatabase.getInstance().addMeeting(meeting);
        RoomScheduleDatabase.getInstance().addRoomScheduleItem(location, meeting);
        EmployeeScheduleDatabase.getInstance().addEmployeeScheduleItem(meeting.getOwner(), meeting);
        
        for(Employee invitee : invitees) {
            InvitationStatusDatabase.getInstance().addInvitation(meeting, invitee, isUpdate);
        }
    }
    
    public static SortedSet<Meeting> getOwnedMeetings(Employee owner) throws SQLException {
        return new TreeSet<>(MeetingDatabase.getInstance().getOwnedMeetings(owner));
    } 
    
    public static void deleteMeeting(Meeting meeting) throws SQLException {
        List<Employee> attending = InvitationStatusDatabase.getInstance().getAttendees(meeting);
        Room location = meeting.getLocation();
        String message = meetingCancelledMessage(meeting);
        
        MeetingDatabase.getInstance().deleteMeeting(meeting);
        RoomScheduleDatabase.getInstance().deleteRoomScheduleItem(location, meeting);
        InvitationStatusDatabase.getInstance().deleteMeeting(meeting);
        
        for(Employee attendee : attending) {
            EmployeeScheduleDatabase.getInstance().deleteEmployeeScheduleItem(attendee, meeting);
            NotificationDatabase.getInstance().addNotification(new Notification(message, attendee));
        }
    }
    
    public static List<Employee> getEmployees (String... loginIds) throws SQLException, EntityNotFoundException {
        return EmployeeDatabase.getInstance().getEmployeeList(loginIds);
    }
    
    public static void acceptInvitation(Meeting meeting, Employee invitee) throws InviteeNotFoundException, SQLException {
        meeting.isAttending(invitee);
        EmployeeScheduleDatabase.getInstance().addEmployeeScheduleItem(invitee, meeting);
        InvitationStatusDatabase.getInstance().updateInvitationStatus(invitee, meeting, true);
    }
    
    public static void declineInvitation(Meeting meeting, Employee invitee) throws SQLException {
        meeting.getInvited().remove(invitee);
        InvitationStatusDatabase.getInstance().updateInvitationStatus(invitee, meeting, false);
    }
    
    public static Map<Room, SortedSet<TimeSlot>> getCoincidingTimes(double meetingDurationInHours, Employee... invitees) throws SQLException {
        Map<Room, SortedSet<TimeSlot>> timesByRoom = new HashMap<>();
        TreeSet<TimeSlot> combinedEmployeeSchedule = getCombinedSchedule(invitees);
        List<Room> allRooms = RoomDatabase.getInstance().getAllRooms();
        
        for(Room room : allRooms) {
            SortedSet<TimeSlot> availableTimes = getAvailableRoomTimes(room, combinedEmployeeSchedule, meetingDurationInHours);
            timesByRoom.put(room, availableTimes);
        }
        
        return timesByRoom;
    }
    
    private static TreeSet<TimeSlot> getCombinedSchedule(Employee... invitees) throws SQLException {
        TreeSet<TimeSlot> combinedSchedule = new TreeSet<>();
        EmployeeScheduleDatabase schedules = EmployeeScheduleDatabase.getInstance();
        
        for(Employee employee : invitees) {
            combinedSchedule.addAll(schedules.getEmployeeSchedule(employee));
        }
        
        return combinedSchedule;
    }
    
    private static SortedSet<TimeSlot> getAvailableRoomTimes(Room room, TreeSet<TimeSlot> inviteeSchedules, double meetingDurationInHours) throws SQLException {
        TreeSet<TimeSlot> combinedRoomAndEmployeeSchedule = new TreeSet<>();
        combinedRoomAndEmployeeSchedule.addAll(inviteeSchedules);
        combinedRoomAndEmployeeSchedule.addAll(RoomScheduleDatabase.getInstance().getRoomSchedule(room));
        
        return getAvailableRoomtimes(combinedRoomAndEmployeeSchedule, meetingDurationInHours);
    }
    
    private static SortedSet<TimeSlot> getAvailableRoomtimes(TreeSet<TimeSlot> combinedRoomAndEmployeeSchedule, double meetingDurationInHours) {
        SortedSet<TimeSlot> availableTimes = new TreeSet<>();
        long meetingDurationInMilliseconds = hoursToMilliseconds(meetingDurationInHours);
        
        // If the schedule is empty, then make the room available now.
        if(combinedRoomAndEmployeeSchedule.isEmpty()) {
            availableTimes.add(newAvailableTime(now(), meetingDurationInMilliseconds));
            return availableTimes;
        }
        
        TimeSlot previous = combinedRoomAndEmployeeSchedule.first();
        combinedRoomAndEmployeeSchedule.remove(previous);
        
        for(TimeSlot next : combinedRoomAndEmployeeSchedule) {
            long timeBetweenEvents = elapsedTime(previous.getEndTime(), next.getStartTime());
            if(timeBetweenEvents >= meetingDurationInMilliseconds)
                availableTimes.add(newAvailableTime(previous.getEndTime(), meetingDurationInMilliseconds));
            previous = next;
        }
        
        // Default: Add an available time after all scheduled events.
        availableTimes.add(newAvailableTime(previous.getEndTime(), meetingDurationInMilliseconds));
        
        return availableTimes;
    }
    
    private static TimeSlot newAvailableTime(Date startTime, long durationInMilliseconds) {
        Date endTime = timeAfterInterval(startTime, durationInMilliseconds);
        return new TimeSlot()
                .setStartTime(startTime)
                .setEndTime(endTime);
    }
    
    private static String meetingCancelledMessage(Meeting meeting) {
        return String.format(
            "The meeting you were attending from %s to %s called by %s has been cancelled.",
            meeting.getStartTime().toString(),
            meeting.getEndTime().toString(),
            meeting.getOwner().getName()
        );
    }
}
