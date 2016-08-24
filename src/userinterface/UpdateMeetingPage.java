/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import meetingmanager.control.MeetingControl;
import meetingmanager.entity.Employee;
import meetingmanager.entity.Meeting;
import meetingmanager.exception.EntityNotFoundException;
import static meetingmanager.userinterface.UIUtils.*;

/**
 *
 * @author Admin
 */
public class UpdateMeetingPage extends AddMeetingPage {
    
    private Set<Employee> invited;
    private Meeting meeting;
    
    UpdateMeetingPage(Meeting meeting, Employee employee, List<Employee> invited) {
        super(employee);
        this.invited = new HashSet<>(invited);
        this.meeting = meeting;
    }
    
    @Override
    protected void moveToNextWindow() {                                         
        // AFTER USERS ARE SELECTED, MOVE TO NEXT WINDOW TO SELECT ROOM
        try {
            String[] employeeLogins = getSelectedEmployees();
            Set<Employee> newInviteeList = new HashSet<>(MeetingControl.getEmployees(employeeLogins));
            MeetingControl.updateInviteeList(invited, newInviteeList, meeting);
            if(!changeTimeAndLocation(newInviteeList))
                return;
            
            getMainWindow().add(new UpdateMeetingPage2(employeeLogins, invited, newInviteeList, meeting));
            this.setVisible(false);
        } catch (SQLException e) {
            showMessage("Database error while trying to get employee list.");
            e.printStackTrace();
        } catch (EntityNotFoundException e) {
            showMessage("Couldn't find employee in database.");
            e.printStackTrace();
        }
    }
    
    private boolean changeTimeAndLocation(Set<Employee> newInviteeList) {
        if(MeetingControl.conflictsExist(newInviteeList, meeting))
            return true;
        
        return warn("Would you like to change the meeting time as well?");
    }
}
