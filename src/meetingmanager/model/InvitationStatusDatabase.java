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
    
    EmployeeDatabase employeeDatabase;

    public InvitationStatusDatabase() throws SQLException {
        super();
        employeeDatabase = new EmployeeDatabase();
    }
    
    @Override
    protected void init() throws SQLException {
        updateDatabase (
            "CREATE TABLE IF NOT EXISTS invitation_status VALUES ( "
            + "owner VARCHAR(50), "
            + "startTime DATE, "
            + "endTime DATE, "
            + "invitee VARCHAR(50), "
            + "confirmed BOOL, "
            + "is_update BOOL NOT NULL, "
            + "PRIMARY KEY (owner, startTime, endTime, invitee))"
        );
    }
    
    public void addInvitation(Meeting meeting, Employee invitee) throws SQLException {
        updateDatabase(
            "INSERT INTO TABLE invitation_status ( "
            + stringify(meeting.getOwner().getLoginId()) + LINE_SEP
            + stringify(meeting.getStartTime().toString()) + LINE_SEP
            + stringify(meeting.getEndTime().toString()) + LINE_SEP
            + stringify(invitee.getLoginId()) + LINE_SEP
            + "NULL)"
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
    
    public void deleteInvitationTime(Meeting meeting, Employee invitee) throws SQLException {
        updateDatabase(
            "DELETE FROM invitation_status WHERE "
            + keyValue(OWNER, meeting.getOwner().getLoginId()) + AND
            + keyValue(START_TIME, meeting.getStartTime()) + AND
            + keyValue(END_TIME, meeting.getEndTime()) + AND
            + keyValue(INVITEE, invitee.getLoginId())
        );
    }
    
    public List<Employee> getDeclinedInvitees(Employee owner) throws SQLException {
        return queryDatabase(
            "SELECT * FROM employee WHERE login_id IN (" +
            "SELECT " + INVITEE + " FROM invitation_status WHERE "
            + keyValue(OWNER, owner.getLoginId()) + AND
            + keyValue(CONFIRMED, false)
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
        return employeeDatabase.toObject(rs);
    }

    @Override
    protected void checkPrimaryKey(Employee obj) throws MissingPrimaryKeyException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
