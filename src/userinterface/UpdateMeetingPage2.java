/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import java.sql.SQLException;
import java.util.Date;
import java.util.Set;
import meetingmanager.control.MeetingControl;
import meetingmanager.entity.Employee;
import meetingmanager.entity.Meeting;
import meetingmanager.entity.Room;
import static meetingmanager.userinterface.UIUtils.*;
import meetingmanager.utils.Utils;

/**
 *
 * @author Admin
 */
public class UpdateMeetingPage2 extends AddMeetingPage2 {
    
    Set<Employee> original;
    Set<Employee> newList;
    Meeting meeting;
    
    public UpdateMeetingPage2(String[] originalLogins, UpdateMeetingPage previous, Set<Employee> original, Set<Employee> newList, Meeting meeting) {
        super(originalLogins, previous);
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
            grandParent.refreshSchedule();
            grandParent.refreshMeetings();
            showMessage("Meeting updated!");
        } catch (SQLException e) {
            showMessage("SQL error while trying to update meeting.");
            e.printStackTrace();
        }
    }
    
//    @Override
//    protected Meeting getSelectedMeeting() {
//        Date startTime = getSelectedTime();
//        Date endTime = Utils.timeAfterInterval(startTime, getDuration());
//        Room location = getSelectedLocation();
//        Meeting newMeeting = new Meeting();
//        
//        newMeeting
//                .setOwner(meeting.getOwner())
//                .setLocation(getSelectedLocation())
//                .setTitle(getTitle())
//                .setStartTime(startTime)
//                .setEndTime(Utils.timeAfterInterval(startTime, getDuration()));
//        
//        return newMeeting;
//    }
    
//    private double getDuration() {
//        return parent.getDuration() < 0 ? meeting.getDuration() : parent.getDuration();
//    }
//    
//    private String getTitle() {
//        return parent.getTitle().isEmpty() ? meeting.getTitle() : parent.getTitle();
//    }
}
