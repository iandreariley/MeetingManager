/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meetingmanager.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Date;
import meetingmanager.entity.Employee;
import meetingmanager.entity.Meeting;
import meetingmanager.exception.MissingPrimaryKeyException;

public class InvitationStatusDatabase extends DatabaseConnection<Employee> {
    
    public static final String INVITEE = "invitee";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String OWNER = "owner";
    public static final String CONFIRMED = "confirmed";
    public static final String IS_UPDATE = "is_update";
    private static InvitationStatusDatabase instance;
        
    static {
        try {
            instance = new InvitationStatusDatabase();
        } catch(SQLException e) {
            System.err.println("Uh Oh! InvitationStatusDatabase failed initialization!");
        }
    }

    public static InvitationStatusDatabase getInstance() {
        return instance;
    }

    private InvitationStatusDatabase() throws SQLException {
        super();
    }
    
    @Override
    protected void init() throws SQLException {
        updateDatabase (
            "CREATE TABLE IF NOT EXISTS invitation_status ("
            + " owner VARCHAR(50),"
            + " startTime BIGINT,"
            + " endTime BIGINT,"
            + " invitee VARCHAR(50),"
            + " confirmed BOOL,"
            + " is_update BOOL,"
            + " FOREIGN KEY (owner, startTime, endTime)"
            + " REFERENCES meeting(owner, startTime, endTime)"
            + " ON DELETE CASCADE ON UPDATE CASCADE,"
            + " FOREIGN KEY (invitee) REFERENCES employee(login_id)"
            + " ON DELETE CASCADE ON UPDATE CASCADE,"
            + " PRIMARY KEY (owner, startTime, endTime, invitee))"
        );
    }
    
    public void addInvitation(Meeting meeting, Employee invitee, boolean isUpdate) throws SQLException {
        updateDatabase(
            "INSERT INTO invitation_status VALUES( "
            + stringify(meeting.getOwner().getLoginId()) + LINE_SEP
            + meeting.getStartTimeStamp() + LINE_SEP
            + meeting.getEndTimeStamp() + LINE_SEP
            + stringify(invitee.getLoginId()) + LINE_SEP
            + "NULL" + LINE_SEP
            + isUpdate + ")"
        );
    }
    
    public void updateInvitationTime(Meeting meeting, Date newStartTime, Date newEndTime) throws SQLException {
        updateDatabase(
            "UPDATE invitation_status SET "
            + keyValue(START_TIME, newStartTime) + LINE_SEP
            + keyValue(END_TIME, newEndTime) + " "
            + "WHERE "
            + keyValue(OWNER, meeting.getOwner().getLoginId()) + AND
            + keyValue(START_TIME, meeting.getStartTime()) + AND
            + keyValue(END_TIME, meeting.getEndTime())
        );
    }
    
    public void updateInvitationStatus(Employee employee, Meeting meeting, boolean isAttending) throws SQLException {
        updateDatabase(
            "UPDATE invitation_status SET "
            + keyValue(CONFIRMED, isAttending) +
            " WHERE "
            + keyValue(OWNER, meeting.getOwner().getLoginId()) + AND
            + keyValue(START_TIME, meeting.getStartTime()) + AND
            + keyValue(END_TIME, meeting.getEndTime()) + AND
            + keyValue(INVITEE, employee.getLoginId())
        );
    }
    
    public void deleteInvitationTime(Meeting meeting, Employee invitee) throws SQLException {
        updateDatabase(
            "DELETE FROM invitation_status WHERE "
            + keyValue(OWNER, meeting.getOwner().getLoginId()) + AND
            + keyValue(START_TIME, meeting.getStartTime()) + AND
            + keyValue(END_TIME, meeting.getEndTime()) + AND
            + keyValue(INVITEE, invitee.getLoginId())
        );
    }
    
    public void deleteMeeting(Meeting meeting) throws SQLException {
        updateDatabase(
            "DELETE FROM invitation_status WHERE "
            + keyValue(OWNER, meeting.getOwner().getLoginId()) + AND
            + keyValue(START_TIME, meeting.getStartTime()) + AND
            + keyValue(END_TIME, meeting.getEndTime())
        );
    }
    
    public List<Employee> getAttendees(Meeting meeting) throws SQLException {
        return queryDatabase(
            "SELECT * FROM employee WHERE login_id IN (" +
            "SELECT " + INVITEE + " FROM invitation_status WHERE "
            + keyValue(OWNER, meeting.getOwner().getLoginId()) + AND
            + keyValue(START_TIME, meeting.getStartTime()) + AND
            + keyValue(END_TIME, meeting.getEndTime()) + AND
            + keyValue(CONFIRMED, true) + ")"
        ); 
    }
    
    public List<Employee> getDeclinedInvitees(Employee owner) throws SQLException {
        return queryDatabase(
            "SELECT * FROM employee WHERE login_id IN (" +
            "SELECT " + INVITEE + " FROM invitation_status WHERE "
            + keyValue(OWNER, owner.getLoginId()) + AND
            + keyValue(CONFIRMED, false) + ")"
        );
    }
    
    public List<Employee> getUndecidedInvitees(Employee owner) throws SQLException {
        return queryDatabase(
            "SELECT * FROM employee WHERE login_id IN (" +
            "SELECT " + INVITEE + " FROM invitation_status WHERE "
            + keyValue(OWNER, owner.getLoginId()) + AND
            + CONFIRMED + " IS NULL"
        );
    }

    @Override
    protected List<Employee> toObject(ResultSet rs) throws SQLException {
        return EmployeeDatabase.getInstance().toObject(rs);
    }

    @Override
    protected void checkPrimaryKey(Employee obj) throws MissingPrimaryKeyException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
