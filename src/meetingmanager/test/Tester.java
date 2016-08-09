package meetingmanager.test;

import java.sql.SQLException;
import java.util.Calendar;

import meetingmanager.entity.Employee;
import meetingmanager.entity.ScheduledEntity;
import meetingmanager.entity.TimeSlot;
import meetingmanager.exception.EntityNotFoundException;
import meetingmanager.exception.MissingPrimaryKeyException;
import meetingmanager.model.DatabaseConnection;
import meetingmanager.model.EmployeeDatabase;

public class Tester {
	
	private static TimeSlot timeslot1;
	private static TimeSlot timeslot2;
	private static TimeSlot timeslot3;

	public static void main(String[] args) {
		createTestTimeSlots();
		runTimeSlotTest();
		runScheduleTest();
		runDBTest();
	}
	
	private static void runScheduleTest() {
		ScheduledEntity entity = new Employee();
		
		testisAvailable(entity, timeslot1, true);
		entity.addEvent(timeslot1);
		testisAvailable(entity, timeslot3, true);
		entity.addEvent(timeslot3);
		testisAvailable(entity, timeslot2, false);
	}
	
	private static void runDBTest() {
		try {
			DatabaseConnection.registerSQLDriver();
			EmployeeDatabase manager = new EmployeeDatabase();
			System.out.println("hooray! database connection established!");
			
			Employee emp = new Employee()
				.setLoginId("testEmp")
				.isAdmin(false)
				.setName("testman")
				.setPassword("pass");
			
			manager.addEmployee(emp);
			
			System.out.println("Employee Added!");
			
			emp.setName("testOther");
			manager.updateEmployee(emp);
			
			System.out.println("Employee Updated!");
			
			manager.getEmployee(emp.getLoginId());
			
			System.out.println("Employee Retrieved!");
			
			manager.deleteEmployee(emp);
			
			System.out.println("Employee Deleted");
		} catch(ClassNotFoundException e) {
			System.err.println("Couldn't load driver");
		} catch(SQLException e) {
			e.printStackTrace();
		} catch(MissingPrimaryKeyException e) {
			System.err.println(e.getMessage());
		} catch (EntityNotFoundException e) {
			System.err.println(e.getMessage());
		}
		
	}

	private static void runTimeSlotTest() {
		testOverlap(timeslot1, timeslot2, true);
		testOverlap(timeslot2, timeslot1, true);
		testOverlap(timeslot2, timeslot3, false);
		testOverlap(timeslot3, timeslot2, false);
	}
	
	private static void createTestTimeSlots() {
		Calendar st1 = newDate(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		Calendar et1 = newDate(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		Calendar st2 = newDate(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		Calendar et2 = newDate(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		Calendar st3 = newDate(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		Calendar et3 = newDate(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		
		timeslot1 = new TimeSlot()
			.setStartTime(st1.getTime())
			.setEndTime(et1.getTime());
		timeslot2 = new TimeSlot()
			.setStartTime(st2.getTime())
			.setEndTime(et2.getTime());
		timeslot3 = new TimeSlot()
			.setStartTime(st3.getTime())
			.setEndTime(et3.getTime());
	}
	
	private static void testisAvailable(ScheduledEntity schedule, TimeSlot block, boolean expected) {
		System.out.println(schedule.isAvailable(block) == expected);
	}
	
	private static void testOverlap(TimeSlot ts1, TimeSlot ts2, boolean expected) {
		System.out.println(ts1.overlaps(ts2) == expected);
	}
	
	private static Calendar newDate(int calendarField, int value) {
		Calendar c = Calendar.getInstance();
		c.set(calendarField, value);
		return c;
	}
}
