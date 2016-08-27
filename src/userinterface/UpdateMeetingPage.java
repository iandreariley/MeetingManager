/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userinterface;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import meetingmanager.control.EmployeeControl;
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
    
    private static final Comparator<Employee> EMPLOYEE_COMPARATOR;
    private Set<Employee> invited;
    private Meeting meeting;
    
    static {
        EMPLOYEE_COMPARATOR = new Comparator<Employee>() {
            public int compare(Employee op1, Employee op2) {
                return op1.getLoginId().compareTo(op2.getLoginId());
            }
        };
    }
    
    UpdateMeetingPage(Meeting meeting, EmployeePage parent) {
        super(meeting.getOwner(), parent);
        this.meeting = meeting;
        setDuration(meeting.getDuration());
        setTitle(meeting.getTitle());
        loadInvited();
        clearSelected();
        clearBench();
        loadEmployees();
    }
    
    private void loadInvited() {
        try {
            invited = new HashSet<>(MeetingControl.getInvited(meeting));
        } catch (SQLException e) {
            showMessage("SQL error while loading invited employees for meeting update.");
            e.printStackTrace();
        }
    }
   
    private void loadEmployees() {
        try {
            List<Employee> employees = EmployeeControl.getAllEmployees();
            
            for(Employee employee : employees) {
                if(invited.contains(employee))
                    addToSelected(employee);
                else
                    addToBench(employee);
            }
        } catch (SQLException e) {
            showMessage(DATABASE_ERROR_MESSAGE);
        }
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
            
            UpdateMeetingPage2 nextPage = new UpdateMeetingPage2(employeeLogins, this, invited, newInviteeList, meeting);
            nextPage.setGrandParent(parent);
            
            getMainWindow().add(nextPage);
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
        if(MeetingControl.conflictsExist(newInviteeList, meeting)){
            showMessage("One or more employees can't make the original meeting time. Please choose a new time");
            return true;
        }
        
        return warn("Would you like to change the meeting time as well?");
    }
}
