package meetingmanager.control;

import java.sql.SQLException;
import meetingmanager.entity.Employee;
import meetingmanager.exception.EntityNotFoundException;
import meetingmanager.model.EmployeeDatabase;
import userinterface.EmployeePage;

public class LoginControl {
    
    public static EmployeePage validate(String loginId, String password) throws SQLException, EntityNotFoundException {
        Employee emp = EmployeeDatabase.getInstance().getEmployee(loginId);
        if(emp.getPassword().equals(password))
            return new EmployeePage(emp);
        else
            return null;
    }
}
