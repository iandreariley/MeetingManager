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
import meetingmanager.control.MeetingControl;
import meetingmanager.entity.Employee;
import meetingmanager.entity.TimeSlot;
import static meetingmanager.userinterface.UIUtils.showMessage;

/**
 *
 * @author Admin
 */
public class SelectTimePage extends SelectAndSubmitPage {
    
    
    public static final String[] HEADERS = { "Start Time", "End Time" };
    
    private AddMeetingPage parent;
    private SelectRoomPage child;
    private Map<Integer, TimeSlot> times;
    
    public SelectTimePage(AddMeetingPage parent) throws SQLException {
        super();
        this.parent = parent;
        setHeaders();
        setSubmitButtonText("Choose Room ->");
        loadAndDisplayTimes();
    }
    
    private void setHeaders() {
        setColumnHeaders(HEADERS);
    }
    
    public Employee getOwner() {
        return parent.getOwner();
    }
    
    private void loadAndDisplayTimes() throws SQLException {
        Set<TimeSlot> possibletimes = MeetingControl.getCoincidingEmployeeTimes(parent.getDuration(), parent.getAllEmployees());
        times = new HashMap<>();
        int rowCount = 0;
        
        for(TimeSlot time : possibletimes) {
            times.put(rowCount, time);
            addTableRow(vectorizeTimeSlot(time));
            rowCount++;
        }
    }
    
    private Object[] vectorizeTimeSlot(TimeSlot timeSlot) {
        return new Object[] { timeSlot.getStartTime().toString(), timeSlot.getEndTime().toString() };
    }
    
    public TimeSlot getSelectedTime() {
        return times.get(getSelectedRow());
    }
    
    public Employee[] getSelectedEmployees() {
        return parent.getSelectedEmployees();
    }
    
    public String getTitle() {
        return parent.getTitle();
    }
    
    public double getDuration() {
        return parent.getDuration();
    }
    
    public int getHeadCount() {
        return parent.getHeadCount();
    }
    
    public void refreshMainPage() {
        parent.refreshMainPage();
    }
    
    @Override
    public void submitButtonAction() {
        if(nothingSelected()) {
            showMessage("Please select a time first.");
            return;
        }
        try {
            if(child == null)
                child = new SelectRoomPage(this);

            getWindowAncestor().add(child);
            this.setVisible(false);
            
        } catch (SQLException e) {
            showMessage("SQL error while loading available rooms.");
        }
    }
    
    @Override
    public void backButtonAction() {
        this.setVisible(false);
        parent.setVisible(true);
    }
}
