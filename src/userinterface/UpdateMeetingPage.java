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
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import meetingmanager.control.EmployeeControl;
import meetingmanager.control.MeetingControl;
import meetingmanager.entity.Employee;
import meetingmanager.entity.Meeting;
import meetingmanager.entity.Room;
import meetingmanager.entity.TimeSlot;
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
    private JPanel child;
    
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
    
    public void handleUpdateFailure(JPanel aChild) {
        aChild.setVisible(false);
        showMessage("You do not have any rooms that can fit that many people. Lighten your load.");
        this.setVisible(true);
    }
    
    public void handleUpdateSuccess(JPanel aChild) {
        ((EmployeePage) parent).handleUpdateSuccess(this);
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
        try {
            String[] employeeLogins = getSelectedEmployeeIds();
            Set<Employee> newInviteeList = new HashSet<>(MeetingControl.getEmployees(employeeLogins));
            Meeting newMeeting = new Meeting(meeting).setInvited(newInviteeList);
            
            if (!MeetingControl.allAvailable(newInviteeList, meeting)) {
                showMessage("Not all invitees are available at the currrent time. Please select a new time.");
                if(child == null) {
                    child = new UpdateTimePage(this, meeting, newMeeting);
                    showChild();
                }
            } else if(newMeeting.size() > meeting.getLocation().getCapacity()) {
                Set<Room> otherRooms = MeetingControl.getAvailableRooms(newMeeting, newMeeting.size());
                if(otherRooms.isEmpty()) {
                    showMessage("Too many employees for current room, and no alternates at this time.\nPlease choose a new meeting time");
                    child = new UpdateTimePage(this, meeting, newMeeting);
                    showChild();
                } else {
                    showMessage("Too many employees for current room, please update room first.");
                    child = new UpdateRoomPage(this, meeting, newMeeting);
                    showChild();
                }
            }  else {
                MeetingControl.updateInviteeList(invited, newInviteeList, meeting);
                parent.handleUpdateSuccess(this);
            }
            this.setVisible(false);
        } catch (SQLException e) {
            showMessage("Database error while trying to get employee list.");
            e.printStackTrace();
        } catch (EntityNotFoundException e) {
            showMessage("Couldn't find employee in database.");
            e.printStackTrace();
        }
    }
    
    private void showChild() {
        SwingUtilities.getWindowAncestor(this).add(child);
        child.setVisible(true);
        this.setVisible(false);
        
    }
    
    private boolean allAvailable(Set<Employee> invitees, TimeSlot time) {
        for(Employee emp : invitees) {
            if (!emp.isAvailable(time))
                return false;
        }
        return true;
    }
}
