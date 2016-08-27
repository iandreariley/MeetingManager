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
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null || this.getClass() != obj.getClass())
            return false;

        Notification other = (Notification) obj;

        return
            (this.message == other.message || other != null && other.message.equals(this.message)) &&
            (this.recipient == other.recipient || other != null && other.recipient.equals(this.recipient));
    }
    
    
        
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (message == null ? 0 : message.hashCode());
        hash = 17 * hash + (recipient == null ? 0 : recipient.hashCode());
        return hash;
    }
}
