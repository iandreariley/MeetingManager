/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import java.sql.SQLException;
import java.util.ArrayList;
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
import static userinterface.SelectTimePage.HEADERS;

/**
 *
 * @author Admin
 */
public class UpdateTimePage extends SelectAndSubmitPage {
    
    public static final String[] HEADERS = { "Start Time", "End Time" };
    
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
        setSubmitButtonText("Choose Room ->");
        clearMainTable();
        loadAndDisplayTimes();
    }
    
    private void setHeaders() {
        setColumnHeaders(HEADERS);
    }
    
    private void loadAndDisplayTimes() throws SQLException {
        double hours = (double) oldMeeting.getDuration() / (double) 3600000;
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
            if(currentRoom.isAvailable(newTime)) {
                MeetingControl.updateMeetingTime(oldMeeting, newMeeting);
                handleSuccess(this);
            } else {
                if (child == null)
                    child = new UpdateRoomPage(this, oldMeeting, newMeeting);
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
}
