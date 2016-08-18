package meetingmanager.control;

import java.sql.SQLException;
import meetingmanager.entity.Employee;
import meetingmanager.exception.EntityNotFoundException;
import meetingmanager.model.EmployeeDatabase;
import userinterface.EmployeePage;
import userinterface.AdminPage;
import javax.swing.JPanel;
public class LoginControl {
    
    public static JPanel validate(String loginId, String password) throws SQLException, EntityNotFoundException {
        Employee emp = EmployeeDatabase.getInstance().getEmployee(loginId);
        
        if(emp.getPassword().equals(password) && emp.isAdmin() == false)
            return new EmployeePage(emp);
        else if(emp.getPassword().equals(password) && emp.isAdmin() == true)
            return new AdminPage(emp);
        else
            return null;
    }
}
