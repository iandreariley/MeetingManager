/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JPanel;
import meetingmanager.control.MeetingControl;
import meetingmanager.entity.Employee;
import meetingmanager.entity.Meeting;
import meetingmanager.entity.Room;
import meetingmanager.entity.TimeSlot;
import static meetingmanager.userinterface.UIUtils.showMessage;

/**
 *
 * @author Admin
 */
public class UpdateTimePage extends SelectAndSubmitPage {
    
    public static final String[] HEADERS = { "Start Time", "End Time" };
    private static final double HOURS_IN_MILLISECONDS = 3600000;
    
    private JPanel parent;
    private UpdateRoomPage child;
    private Meeting oldMeeting;
    private Meeting newMeeting;
    private Map<Integer, TimeSlot> times;
    
    public UpdateTimePage(JPanel parent, Meeting oldMeeting, Meeting newMeeting) throws SQLException {
        super();
        this.parent = parent;
        this.oldMeeting = oldMeeting;
        this.newMeeting = newMeeting;
        setHeaders();
        setSubmitButtonText("Submit");
        clearMainTable();
        loadAndDisplayTimes();
    }
    
    private void setHeaders() {
        setColumnHeaders(HEADERS);
    }
    
    private void loadAndDisplayTimes() throws SQLException {
        double hours = (double) oldMeeting.getDuration() / (double) HOURS_IN_MILLISECONDS;
        Set<TimeSlot> possibleTimes = MeetingControl.getCoincidingEmployeeTimes(hours, toArray(newMeeting.getInvited()));
        if (possibleTimes.isEmpty())
            handleFailure();
        else
            loadAndDisplayTimes(possibleTimes);
    }
    
    private void loadAndDisplayTimes(Set<TimeSlot> possibleTimes) throws SQLException {
        times = new HashMap<>();
        int rowCount = 0;
        
        for(TimeSlot time : possibleTimes) {
            times.put(rowCount, time);
            addTableRow(vectorizeTimeSlot(time));
            rowCount++;
        }
    }
    
    private void handleFailure() {
        if(parent instanceof UpdateMeetingPage) {
            ((UpdateMeetingPage) parent).handleUpdateFailure(this);
        }
    }
    
    private Object[] vectorizeTimeSlot(TimeSlot timeSlot) {
        return new Object[] { timeSlot.getStartTime().toString(), timeSlot.getEndTime().toString() };
    }
    
    private Employee[] toArray(Set<Employee> set) {
        Employee[] arr = new Employee[set.size()];
        int i = 0;
        for(Employee obj : set) {
            arr[i] = obj;
            i++;
        }
        return arr;
    }
    
    @Override
    public void submitButtonAction() {
        if(nothingSelected()) {
            showMessage("Please select a time first.");
            return;
        }
        try {
            TimeSlot newTime = times.get(getSelectedRow());
            Room currentRoom = oldMeeting.getLocation();
            currentRoom.setSchedule(new TreeSet<>(MeetingControl.getRoomSchedule(currentRoom)));
            newMeeting.setTime(newTime);
            if(currentRoom.isAvailable(newTime) && currentRoom.getCapacity() >= newMeeting.size()) {                
                MeetingControl.updateMeetingTime(oldMeeting, newMeeting);
                handleSuccess(this);
            } else {
                if (child == null)
                    child = new UpdateRoomPage(this, oldMeeting, newMeeting);
                showMessage("The room you're meeting in isn't available at that time, or doesn't have the capacity for all your invitees. Please choose a new time");
                getWindowAncestor().add(child);
                child.setVisible(true);
                this.setVisible(false);
            }
        } catch (SQLException e) {
            showMessage("SQL error while loading available rooms.");
        }
    }
    
    public void handleSuccess(JPanel panel) {
        if (parent instanceof EmployeePage) {
            ((EmployeePage) parent).handleUpdateSuccess(this);
        } else if (parent instanceof UpdateMeetingPage) {
            ((UpdateMeetingPage) parent).handleUpdateSuccess(this);
        }
    }
    
    @Override
    protected void backButtonAction() {
        if (parent instanceof EmployeePage) {
            ((EmployeePage) parent).returnControl(this);
        } else if (parent instanceof UpdateMeetingPage) {
            ((UpdateMeetingPage) parent).returnControl(this);
        }
    }
    
    public void returnControl() {
        getWindowAncestor().remove(child);
        this.setVisible(true);
    }
}
