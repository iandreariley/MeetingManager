/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meetingmanager.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import meetingmanager.entity.Employee;
import meetingmanager.exception.MissingPrimaryKeyException;
import meetingmanager.entity.Notification;
import meetingmanager.exception.EntityNotFoundException;

/**
 *
 * @author Admin
 */
public class NotificationDatabase extends DatabaseConnection<Notification> {
    
    private static final String RECIPIENT = "recipient";
    private static final String MESSAGE = "message";
    private static NotificationDatabase instance;

    static {
        try {
            instance = new NotificationDatabase();
        } catch(SQLException e) {
            System.err.println("Uh Oh! Notification Database failed initialization!");
        }
    }

    public static NotificationDatabase getInstance() {
        return instance;
    }
    
    public NotificationDatabase() throws SQLException {
        super();
    }
    
    @Override
    protected void init() throws SQLException {
        updateDatabase(
            "CREATE TABLE IF NOT EXISTS notification ("
            + "recipient VARCHAR(50), "
            + "message VARCHAR(300), "
            + "FOREIGN KEY (recipient) REFERENCES employee(login_id) "
            + "ON DELETE CASCADE ON UPDATE CASCADE)"
        );
    }
    
    public void addNotification(Notification notification) throws SQLException {
        updateDatabase(
            "INSERT INTO notification VALUES ( "
            + stringify(notification.getRecipient().getLoginId()) + LINE_SEP
            + stringify(notification.getMessage()) + ")"
        );
    }

    //1472064275520 | 1472067875520
    public void deleteNotification(Notification notification) throws SQLException {
        updateDatabase(
            "DELETE FROM notification WHERE "
            + keyValue(RECIPIENT, notification.getRecipient().getLoginId()) + AND
            + keyValue(MESSAGE, notification.getMessage())
        );
    }
    
    public List<Notification> getNotifications(Employee recipient) throws SQLException {
        return queryDatabase(
            "SELECT * FROM notification WHERE "
            + keyValue(RECIPIENT, recipient.getLoginId())
        );
    }

    @Override
    protected List<Notification> toObject(ResultSet rs) throws SQLException {
        List<Notification> results = new ArrayList<>();
        try {
            while(rs.next()) {
                Notification result = new Notification(
                    rs.getString(MESSAGE),
                    EmployeeDatabase.getInstance().getEmployee(rs.getString(RECIPIENT))
                );
            }
        } catch(EntityNotFoundException e) {
            
        }
        return results;
    }

    @Override
    protected void checkPrimaryKey(Notification obj) throws MissingPrimaryKeyException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
