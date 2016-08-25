/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import java.sql.SQLException;
import java.util.Set;
import meetingmanager.control.MeetingControl;
import meetingmanager.entity.Employee;
import meetingmanager.entity.Meeting;
import static meetingmanager.userinterface.UIUtils.*;

/**
 *
 * @author Admin
 */
public class UpdateMeetingPage2 extends AddMeetingPage2 {
    
    Set<Employee> original;
    Set<Employee> newList;
    Meeting meeting;
    
    public UpdateMeetingPage2(String[] originalLogins, Set<Employee> original, Set<Employee> newList, Meeting meeting) {
        super(originalLogins, meeting.getOwner());
        this.original = original;
        this.meeting = meeting;
        this.newList = newList;
    }
    
    @Override
    protected void selectMeeting() {
        if(nothingSelected())
            return;
        try{
            Meeting newMeeting = getSelectedMeeting();
            MeetingControl.updateMeeting(meeting, newMeeting);
        } catch (SQLException e) {
            showMessage("SQL error while trying to update meeting.");
            e.printStackTrace();
        }
    }
}
