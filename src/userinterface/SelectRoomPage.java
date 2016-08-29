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
import meetingmanager.entity.Meeting;
import meetingmanager.entity.Room;
import meetingmanager.userinterface.UIUtils;
import static meetingmanager.userinterface.UIUtils.showMessage;

/**
 *
 * @author Admin
 */
public class SelectRoomPage extends SelectAndSubmitPage {
    
    private SelectTimePage parent;
    private Map<Integer, Room> rooms;
    
    public SelectRoomPage(SelectTimePage parent) throws SQLException {
        super();
        this.parent = parent;
        clearMainTable();
        loadAndDisplayRooms();
    }
    
    private void loadAndDisplayRooms() throws SQLException {
        Set<Room> availableRooms = MeetingControl.getAvailableRooms(parent.getSelectedTime(), parent.getHeadCount());
        rooms = new HashMap<>();
        int rowCount = 0;
        
        for(Room room : availableRooms) {
            rooms.put(rowCount, room);
            addTableRow(vectorizeRoom(room));
            rowCount++;
        }
    }
    
    private Object[] vectorizeRoom(Room room) {
        return new Object[] { room.getLocation() };
    }
    
    @Override
    protected void submitButtonAction() {
        if (nothingSelected()) {
            showMessage("Please select a room first.");
            return;
        }
        
        try {
            Meeting newMeeting = new Meeting(parent.getSelectedTime());
            newMeeting
                    .setInvited(parent.getSelectedEmployees())
                    .setOwner(parent.getOwner())
                    .setLocation(rooms.get(getSelectedRow()))
                    .setTitle(parent.getTitle());

            MeetingControl.addMeeting(newMeeting, true);
            showMessage("Meeting added!");
            parent.refreshMainPage();
        } catch (SQLException e) {
            showMessage("SQL error when trying to add meeting.");
        }
    }
    
    @Override
    protected void backButtonAction() {
        parent.returnControl(this);
    }
}
