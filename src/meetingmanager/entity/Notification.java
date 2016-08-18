/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meetingmanager.entity;

import java.sql.SQLException;
import meetingmanager.model.NotificationDatabase;
/**
 *
 * @author Admin
 */
public class Notification {
    
    private final String message;
    private final Employee recipient;
    
    public Notification(String message, Employee recipient) {
        this.message = message;
        this.recipient = recipient;
    }
    
    public Employee getRecipient() {
        return recipient;
    }
    
    public String getMessage() {
        return message;
    }
}
