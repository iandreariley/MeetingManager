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
import javax.swing.JPanel;
import meetingmanager.control.MeetingControl;
import meetingmanager.entity.Meeting;
import meetingmanager.entity.Room;
import meetingmanager.userinterface.UIUtils;
import static meetingmanager.userinterface.UIUtils.showMessage;

/**
 *
 * @author Admin
 */
public class UpdateRoomPage extends SelectAndSubmitPage {
    
    private final JPanel parent;
    private Map<Integer, Room> rooms;
    private Meeting newMeeting;
    private Meeting oldMeeting;
    
    public UpdateRoomPage(JPanel parent, Meeting oldMeeting, Meeting newMeeting) throws SQLException {
        super();
        this.newMeeting = newMeeting;
        this.oldMeeting = oldMeeting;
        this.parent = parent;
        clearMainTable();
        loadAndDisplayRooms();
    }
    
    private void loadAndDisplayRooms() throws SQLException {
        Set<Room> availableRooms = getAvailableRooms();
        if(availableRooms.isEmpty()) {
            handleFailure();
        } else {
            loadAndDisplayRooms(availableRooms);
        }
    }
    
    private void loadAndDisplayRooms(Set<Room> availableRooms) throws SQLException {        
        rooms = new HashMap<>();
        int rowCount = 0;
        
        for(Room room : availableRooms) {
            rooms.put(rowCount, room);
            addTableRow(vectorizeRoom(room));
            rowCount++;
        }
    }
    
    @Override
    protected void submitButtonAction() {
        if (nothingSelected()) {
            showMessage("Please select a room first.");
            return;
        }
        
        try {            
            newMeeting.setLocation(rooms.get(getSelectedRow()));
            MeetingControl.updateMeeting(oldMeeting, newMeeting);
            handleSuccess();
        } catch (SQLException e) {
            showMessage("SQL error while trying to update meeting.");
            e.printStackTrace();
        }
        
    }
    
    @Override
    protected void backButtonAction() {
        if (parent instanceof EmployeePage) {
            ((EmployeePage) parent).returnControl(this);
        } else if (parent instanceof UpdateTimePage) {
            ((UpdateTimePage) parent).returnControl(this);
        } else if (parent instanceof UpdateMeetingPage) {
            ((UpdateMeetingPage) parent).returnControl(this);
        }
    }
    
    private void handleSuccess() {
        if (parent instanceof EmployeePage) {
            ((EmployeePage) parent).handleUpdateSuccess(this);
        } else if (parent instanceof UpdateTimePage) {
            ((UpdateTimePage) parent).handleSuccess(this);
        } else if (parent instanceof UpdateMeetingPage) {
            ((UpdateMeetingPage) parent).handleUpdateSuccess(this);
        }
    }
    
    private void handleFailure() {
        if(parent instanceof EmployeePage)
            ((EmployeePage) parent).handleUpdateFailure(this, "No rooms available at this time. Please choose a new time");
    }
    
    private Set<Room> getAvailableRooms() throws SQLException {
        return MeetingControl.getAvailableRooms(newMeeting, newMeeting.size()); 
    }
    
    private Object[] vectorizeRoom(Room room) {
        return new Object[] { room.getLocation() };
    }
}
