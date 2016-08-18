package meetingmanager.control;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import meetingmanager.entity.Employee;
import meetingmanager.entity.Notification;
import meetingmanager.entity.TimeSlot;
import meetingmanager.entity.Meeting;
import meetingmanager.model.EmployeeDatabase;
import meetingmanager.model.EmployeeScheduleDatabase;
import meetingmanager.model.MeetingDatabase;
import meetingmanager.model.NotificationDatabase;
import meetingmanager.userinterface.EmployeePage;
import static meetingmanager.utils.Utils.*;

public class EmployeeControl {
    
    public static List<TimeSlot> getEmployeeSchedule(Employee employee) throws SQLException {
        return EmployeeScheduleDatabase.getInstance().getEmployeeSchedule(employee);
    }
    
    /**
     * Precondition: employee schedule must not be null
     * @param employee
     * @param event
     * @return boolean: true if event was added, false if there is a conflict.
     * @throws SQLException
     */
    public static boolean addEvent(Employee employee, TimeSlot event) throws SQLException {
        if(employee.isAvailable(event)) {
            EmployeeScheduleDatabase.getInstance().addEmployeeScheduleItem(employee, event);
            return true;
        }
        return false;
    }
    
    public static void removeEvent(Employee employee, TimeSlot event) throws SQLException {
        EmployeeScheduleDatabase.getInstance().deleteEmployeeScheduleItem(employee, event);
    }
    
    public static void updateEmployee(String oldLogin, Employee newValues) throws SQLException {
        EmployeeDatabase.getInstance().updateEmployee(oldLogin, newValues);
    }
    
    public static TreeSet<TimeSlot> getPartialSchedule(Employee employee, int days) throws SQLException {
        List<TimeSlot> fullSchedule = EmployeeScheduleDatabase.getInstance().getEmployeeSchedule(employee);
        TreeSet<TimeSlot> partialSchedule = new TreeSet<>();
        Date cutoff = timeAfterInterval(now(), days * DAY_IN_MILLISECONDS);
        
        for(TimeSlot scheduleItem : fullSchedule) {
            if(scheduleItem.getStartTime().before(cutoff))
                partialSchedule.add(scheduleItem);
        }
        
        return partialSchedule;
    }
    
    public static List<Meeting> getInvitedMeetings(Employee employee) throws SQLException {
        return MeetingDatabase.getInstance().getUnconfirmedMeetings(employee);
    }
    
    public static List<Notification> getNotifications(Employee employee) throws SQLException {
        return NotificationDatabase.getInstance().getNotifications(employee);
    }
    
    
    public static void deleteNotification(Notification notification) throws SQLException {
        NotificationDatabase.getInstance().deleteNotification(notification);
    }
    
    public static EmployeePage getInterface(Employee employee) {
        return new EmployeePage(employee);
    }
}
