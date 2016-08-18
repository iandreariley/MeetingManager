package meetingmanager.control;

import java.sql.SQLException;
import java.util.List;
import meetingmanager.entity.Employee;
import meetingmanager.entity.Room;
import meetingmanager.entity.TimeSlot;
import meetingmanager.exception.MissingPrimaryKeyException;
import meetingmanager.model.EmployeeDatabase;
import meetingmanager.model.RoomDatabase;
import meetingmanager.model.RoomScheduleDatabase;

public class AdminControl {
    
    public static void addEmployee(Employee employee) throws SQLException {
        EmployeeDatabase.getInstance().addEmployee(employee);
    }
    
    public static void deleteEmployee(Employee employee) throws SQLException, MissingPrimaryKeyException {
        EmployeeDatabase.getInstance().deleteEmployee(employee);
    }
    
    /**
     * Precondition: Employee object has new password set.
     * @param employee 
     */
    public static void resetEmployeePassword(Employee employee, String pass) throws SQLException {
        employee.setPassword(pass);
        EmployeeDatabase.getInstance().updateEmployee(employee.getLoginId(), employee);
    }
    
    public static void addRoom(Room room) throws SQLException {
        RoomDatabase.getInstance().addRoom(room);
    }
    
    public static void deleteRoom(Room room) throws SQLException, MissingPrimaryKeyException {
        RoomDatabase.getInstance().deleteRoom(room);
    }
    
    public static void addRoomEvent(Room room, TimeSlot event) throws SQLException {
        RoomScheduleDatabase.getInstance().addRoomScheduleItem(room, event);
    }
    
    public static void DeleteRoomEvent(Room room, TimeSlot event) throws SQLException {
        RoomScheduleDatabase.getInstance().deleteRoomScheduleItem(room, event);
    }
    
    public static List<Employee> getAllEmployees() throws SQLException {
        return EmployeeDatabase.getInstance().getAllEmployees();
    }
}
