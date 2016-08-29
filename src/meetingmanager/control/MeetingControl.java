package meetingmanager.control;

import java.sql.SQLException;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
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
    
    public static final int MAX_TIME_PER_ROOM = 15;
    
    public static void addMeeting(Meeting meeting, boolean isUpdate) throws SQLException {
        SortedSet<Employee> invitees = meeting.getInvited();
        Room location = meeting.getLocation();
        
        MeetingDatabase.getInstance().addMeeting(meeting);
        RoomScheduleDatabase.getInstance().addRoomScheduleItem(location, meeting);
        EmployeeScheduleDatabase.getInstance().addEmployeeScheduleItem(meeting.getOwner(), meeting);
        
        invite(invitees, meeting, isUpdate);
    }
    
    public static boolean conflictsExist(Set<Employee> attendees, Meeting meeting) {
        if(attendees.size() > meeting.getLocation().getCapacity())
            return true;
        
        for(Employee attendee : attendees) {
            if(!attendee.isAvailable(meeting))
                return true;
        }
        
        return false;
    }
    
    public static SortedSet<Meeting> getOwnedMeetings(Employee owner) throws SQLException {
        TreeSet<Meeting> meetings = new TreeSet<>(MeetingDatabase.getInstance().getOwnedMeetings(owner));
        return allMeetingsAfterNow(meetings);
    }
    
    private static TreeSet<Meeting> allMeetingsAfterNow(TreeSet<Meeting> meetings) {
        Date now = now();
        TreeSet<Meeting> relevent = new TreeSet<>();
        for(Meeting meeting : meetings.descendingSet()) {
            if(meeting.getEndTime().after(now))
                relevent.add(meeting);
            else
                break;
        }
        return relevent;
    }
    
    public static List<Employee> getInvited(Meeting meeting) throws SQLException {
        return InvitationStatusDatabase.getInstance().getInvitees(meeting);
    }
    
    public static void deleteMeeting(Meeting meeting) throws SQLException {
        List<Employee> attending = InvitationStatusDatabase.getInstance().getAttendees(meeting);
        attending.add(meeting.getOwner());
        Room location = meeting.getLocation();
        
        MeetingDatabase.getInstance().deleteMeeting(meeting);
        RoomScheduleDatabase.getInstance().deleteRoomScheduleItem(location, meeting);
        InvitationStatusDatabase.getInstance().deleteMeeting(meeting);
        
        String message = meetingCancelledMessage(meeting);
        NotificationDatabase notificationDatabase = NotificationDatabase.getInstance();
        EmployeeScheduleDatabase employeeScheduleDatabase = EmployeeScheduleDatabase.getInstance();
        
        for(Employee attendee : attending) {
            employeeScheduleDatabase.deleteEmployeeScheduleItem(attendee, meeting);
            notificationDatabase.addNotification(new Notification(message, attendee));
        }
    }
    
    public static void updateMeeting(Meeting oldMeeting, Meeting newMeeting) throws SQLException {
        updateMeetingTime(oldMeeting, newMeeting);
        MeetingDatabase.getInstance().updateLocation(newMeeting);
        RoomScheduleDatabase roomScheduler = RoomScheduleDatabase.getInstance();
        roomScheduler.deleteRoomScheduleItem(oldMeeting.getLocation(), oldMeeting);
        roomScheduler.addRoomScheduleItem(newMeeting.getLocation(), newMeeting);
    }
    
    public static void updateMeetingTime(Meeting oldMeeting, Meeting newMeeting) throws SQLException {
        EmployeeScheduleDatabase employeeScheduleDatabase = EmployeeScheduleDatabase.getInstance();
        InvitationStatusDatabase invitationStatusDatabase = InvitationStatusDatabase.getInstance();
        List<Employee> attendees = invitationStatusDatabase.getAttendees(oldMeeting);
        
        employeeScheduleDatabase.updateScheduleItemTime(oldMeeting.getOwner(), oldMeeting, newMeeting);
        MeetingDatabase.getInstance().updateInvitationTime(oldMeeting, newMeeting.getStartTime(), newMeeting.getEndTime());
        invitationStatusDatabase.nullifyConfirmedAndSetUpdate(newMeeting);
        
        for(Employee invitee : attendees) {
            employeeScheduleDatabase.deleteEmployeeScheduleItem(invitee, oldMeeting);
        }
        
    }
    
    public static void updateInviteeList(Set<Employee> original, Set<Employee> newList, Meeting meeting) throws SQLException {
        Set<Employee> removed = setDifference(original, newList);
        Set<Employee> added = setDifference(newList, original);
        handleRemoval(removed, meeting);
        invite(added, meeting, false);
    }
    
    private static void handleRemoval(Set<Employee> toRemove, Meeting meeting) throws SQLException {
        for (Employee removed : toRemove) {
            NotificationDatabase.getInstance().addNotification(removalNotification(removed, meeting));
        }
    }
    
    private static Notification removalNotification(Employee employee, Meeting meeting) {
        return new Notification(newRemovalMessage(employee, meeting), employee);
    }
    
    private static String newRemovalMessage(Employee employee, Meeting meeting) {
        return "You are no longer required to attend " + meeting.getTitle() + " at " + meeting.getStartTime();
    }
    
    public static void invite(Set<Employee> invitees, Meeting meeting, boolean isUpdate) throws SQLException {
        for(Employee invitee : invitees) {
            InvitationStatusDatabase.getInstance().addInvitation(meeting, invitee, isUpdate);
        }
    }
    
    public static List<Employee> getEmployees (String... loginIds) throws SQLException, EntityNotFoundException {
        return EmployeeDatabase.getInstance().getEmployeeList(loginIds);
    }
    
    public static void acceptInvitation(Meeting meeting, Employee invitee) throws InviteeNotFoundException, SQLException {
        EmployeeScheduleDatabase.getInstance().addEmployeeScheduleItem(invitee, meeting);
        InvitationStatusDatabase.getInstance().updateInvitationStatus(invitee, meeting, true);
    }
    
    public static void declineInvitation(Meeting meeting, Employee invitee) throws SQLException {
        InvitationStatusDatabase.getInstance().updateInvitationStatus(invitee, meeting, false);
        NotificationDatabase.getInstance().addNotification(declinedInvitationNotification(meeting, invitee));
    }
    
    public static Notification declinedInvitationNotification (Meeting meeting, Employee invitee) {
        return new Notification(declinedInvitationMessage(meeting, invitee), meeting.getOwner());
    }
    
    public static String declinedInvitationMessage(Meeting meeting, Employee invitee) {
        return invitee.getLoginId() + " has declined your invitation to meet on " + meeting.getStartTime();
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
    
    public static SortedSet<TimeSlot> getCoincidingEmployeeTimes(double meetingDurationInHours, Employee... invitees) throws SQLException {
        TreeSet<TimeSlot> schedule = getCombinedSchedule(invitees);
        Set<Room> rooms = getRoomsWithCapacity(invitees.length);
        
        if(rooms.isEmpty())
            return new TreeSet<>();
        
        SortedSet<TimeSlot> times = getAvailableTimes(allTimesAfterNow(schedule), meetingDurationInHours);
        for(TimeSlot time : times) {
            if(!atLeastOneRoomAvailable(time, rooms))
                times.remove(time);
        }
        
        return times;
    }
    
    private static Set<Room> getRoomsWithCapacity(int cap) throws SQLException {
        Set<Room> rooms = new HashSet<>(RoomDatabase.getInstance().getAllRooms());
        Set<Room> edited = new HashSet<>();
        
        for(Room room : rooms) {
            if(room.getCapacity() >= cap)
                edited.add(room);
        }
        
        return rooms;
    }
    
    private static TreeSet<TimeSlot> getCombinedSchedule(Employee... invitees) throws SQLException {
        TreeSet<TimeSlot> combinedSchedule = new TreeSet<>();
        EmployeeScheduleDatabase schedules = EmployeeScheduleDatabase.getInstance();
        
        for(Employee employee : invitees) {
            combinedSchedule.addAll(schedules.getEmployeeSchedule(employee));
        }
        
        return combinedSchedule;
    }
    
    public static boolean allAvailable(Set<Employee> emps, TimeSlot time) throws SQLException {
        for(Employee emp : emps) {
            Employee scheduleCheck = new Employee();
            scheduleCheck.setSchedule(EmployeeScheduleDatabase.getInstance().getEmployeeSchedule(emp));
            if (!scheduleCheck.isAvailable(time))
                return false;
        }
        return true;
    }
    
    public static Set<Room> getAvailableRooms(TimeSlot time, int capacity) throws SQLException {
        Set<Room> rooms = new HashSet<>(RoomDatabase.getInstance().getAllRooms());
        Set<Room> edited = new HashSet<>();
        
        for(Room room : rooms) {
            Room scheduleCheck = new Room();
            scheduleCheck.setSchedule(RoomScheduleDatabase.getInstance().getRoomSchedule(room));
            if (room.getCapacity() >= capacity && scheduleCheck.isAvailable(time))
                edited.add(room);
        }
        
        return edited;
    }
    
    public static List<TimeSlot> getRoomSchedule(Room room) throws SQLException {
        return RoomScheduleDatabase.getInstance().getRoomSchedule(room);
    }
    
    private static SortedSet<TimeSlot> getAvailableRoomTimes(Room room, TreeSet<TimeSlot> inviteeSchedules, double meetingDurationInHours) throws SQLException {
        TreeSet<TimeSlot> combinedRoomAndEmployeeSchedule = new TreeSet<>();
        combinedRoomAndEmployeeSchedule.addAll(inviteeSchedules);
        combinedRoomAndEmployeeSchedule.addAll(RoomScheduleDatabase.getInstance().getRoomSchedule(room));
        
        return getAvailableTimes(allTimesAfterNow(combinedRoomAndEmployeeSchedule), meetingDurationInHours);
    }
    
    /**
     * Precondition schedule must have at least one timeslot with an endtime equivalent to now.
     * Look at "allTimesAfterNow" for reference.
     * @param schedule
     * @param meetingDurationInHours
     * @return 
     */
    private static SortedSet<TimeSlot> getAvailableTimes(NavigableSet<TimeSlot> schedule, double meetingDurationInHours) {
        TreeSet<TimeSlot> availableTimes = new TreeSet<>();
        long meetingDurationInMilliseconds = hoursToMilliseconds(meetingDurationInHours);
        TimeSlot previous = timeSlotForCurrentInstant();
        
        for(TimeSlot next : schedule) {
            long timeBetweenEvents = elapsedTime(previous.getEndTime(), next.getStartTime());
            Date startTime = previous.getEndTime();
            
            while(timeBetweenEvents >= meetingDurationInMilliseconds && availableTimes.size() < MAX_TIME_PER_ROOM) {
                TimeSlot newAvailableMeetingTime = newAvailableTime(startTime, meetingDurationInMilliseconds);
                availableTimes.add(newAvailableMeetingTime);
                
                timeBetweenEvents -= meetingDurationInMilliseconds;
                startTime = newAvailableMeetingTime.getEndTime();
            }
            
            previous = next;
            
            if(availableTimes.size() >= MAX_TIME_PER_ROOM)
                break;
        }
        
        Date nextStartTime = previous.getEndTime();
        while(availableTimes.size() < MAX_TIME_PER_ROOM) {
            availableTimes.add(newAvailableTime(nextStartTime, meetingDurationInMilliseconds));
            nextStartTime = timeAfterInterval(nextStartTime, meetingDurationInMilliseconds);
        }        
        
        return availableTimes;
    }
    
    private static boolean atLeastOneRoomAvailable(TimeSlot timeSlot, Set<Room> rooms) {
        for(Room room : rooms) {
            if(room.isAvailable(timeSlot))
                return true;
        }
        return false;
    }
    
    private static TimeSlot timeSlotForCurrentInstant() {
        return new TimeSlot().setStartTime(now()).setEndTime(now());
    }
    
    private static NavigableSet<TimeSlot> allTimesAfterNow(TreeSet<TimeSlot> schedule) {
        TimeSlot currentScheduleItem = getCurrentScheduleItem(schedule);
        TimeSlot nextScheduleItem = schedule.ceiling(timeSlotForCurrentInstant());
        TimeSlot start = currentScheduleItem == null ? nextScheduleItem : currentScheduleItem;
        if(start == null)
            return new TreeSet<>();
        else
            return schedule.subSet(start, true, schedule.last(), true);
    }
    
    private static TimeSlot getCurrentScheduleItem(TreeSet<TimeSlot> schedule) {
        TimeSlot now = timeSlotForCurrentInstant();
        TimeSlot current = schedule.floor(now);
        if (current == null || current.getEndTime().before(now.getStartTime()))
            return null;
        else
            return current;
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
    
    private static <T> Set<T> setDifference(Set<T> op1, Set<T> op2) {
        Set<T> difference = new HashSet<>(op1);
        difference.removeAll(op2);
        return difference;
    }
}
