package meetingmanager.test;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import meetingmanager.entity.Employee;
import meetingmanager.entity.ScheduledEntity;
import meetingmanager.entity.TimeSlot;
import meetingmanager.exception.EntityNotFoundException;
import meetingmanager.exception.MissingPrimaryKeyException;
import meetingmanager.model.DatabaseConnection;
import meetingmanager.model.EmployeeDatabase;
import meetingmanager.model.RoomDatabase;


import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import meetingmanager.control.AdminControl;
import meetingmanager.control.EmployeeControl;
import meetingmanager.control.MeetingControl;
import meetingmanager.entity.Room;
import meetingmanager.entity.Meeting;
import meetingmanager.model.RoomScheduleDatabase;
import static meetingmanager.utils.Utils.*;
public class Tester {
	
	private static TimeSlot timeslot1;
	private static TimeSlot timeslot2;
	private static TimeSlot timeslot3;

	public static void main(String[] args) {
		createTestTimeSlots();
		runTimeSlotTest();
		runScheduleTest();
		runDBTest();
                runAdminControlTest();
                //runEmployeeControlTest();
                //runMeetingControlTest();
                
//        SwingUtilities.invokeLater(new Runnable(){
//            public void run(){
//                createAndShowGUI();
//            }
//        });
                
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
			EmployeeDatabase manager = EmployeeDatabase.getInstance();
                        RoomDatabase roomManager = RoomDatabase.getInstance();
                        
			sysout("hooray! database connection established!");
			
                        Employee emp = testEmp();
                        Employee emp2 = testEmp();
                        Room room = testRoom();
			manager.addEmployee(emp);
			
			sysout("Employee Added!");
			
			emp.setName("testOther");
			manager.updateEmployee(emp.getLoginId(), emp);
			
			sysout("Employee Updated!");
			
			manager.getEmployee(emp.getLoginId());
			
			sysout("Employee Retrieved!");
			
			manager.deleteEmployee(emp);
			
			sysout("Employee Deleted");
		} catch(ClassNotFoundException e) {
			syserr("Couldn't load driver");
		} catch(SQLException e) {
			e.printStackTrace();
		} catch(MissingPrimaryKeyException e) {
			syserr(e.getMessage());
		} catch (EntityNotFoundException e) {
			syserr(e.getMessage());
		}
		
	}
        
        private static void runAdminControlTest() {
            Employee testEmp = testEmp();
            String newPass = "123";
            Room testRoom = testRoom();
            try {
                AdminControl.addEmployee(testEmp);
                sysout("Successfully Added employee as admin!");
            } catch(SQLException e) {
                sysout("Add employee as Admin failed. Exiting.");
                e.printStackTrace();
            }
            
            try {
                AdminControl.resetEmployeePassword(testEmp, newPass);
                Employee retrieveEmp = EmployeeDatabase.getInstance().getEmployee(testEmp.getLoginId());
                if(testEmp.getPassword().equals(newPass)) {
                    sysout("Employee password reset!");
                } else {
                    syserr("Employee password was not reset in database.");
                }
            } catch(SQLException e) {
                syserr("SQLException while trying to reset password.");
                e.printStackTrace();
            } catch(EntityNotFoundException e) {
                syserr("Employee was not found in database, add must have failed without throwing.");
            }
            
            try {
                AdminControl.deleteEmployee(testEmp);
                sysout("Hooray! Employee deleted!");
            } catch(SQLException e) {
                syserr("SQLException while trying to delete employee.");
                e.printStackTrace();
            } catch(MissingPrimaryKeyException e) {
                syserr("Oh no. Employee loginId not found");
            }
            
            try {
                AdminControl.addRoom(testRoom);
                sysout("Room added as admin!");
            } catch(SQLException e) {
                syserr("SQLException while trying to add room. Exiting.");
                e.printStackTrace();
                return;
            }
            
            try {
                timeslot1 = event(HOUR_IN_MILLISECONDS);
                Date realTime = now();
                sysout("MySQL format: " + timeslot1.getSQLFormattedStartTime());
                AdminControl.addRoomEvent(testRoom, timeslot1);
                TimeSlot retrieved = RoomScheduleDatabase.getInstance().getRoomSchedule(testRoom).get(0);
                sysout("Real time: " + realTime + "; retrieved time: " + retrieved.getStartTime());
                sysout("Room event added!");
            } catch(SQLException e) {
                syserr("SQLException while trying to add room event as admin.");
                e.printStackTrace();
            }
            
//            try {
//                AdminControl.DeleteRoomEvent(testRoom, timeslot1);
//                sysout("Room event deleted!");
//            } catch(SQLException e) {
//                syserr("SQLException while trying to delete room event as admin.");
//                e.printStackTrace();
//            }
//            
//            try {
//                AdminControl.deleteRoom(testRoom);
//                sysout("Deleted Room as Admin!");
//            } catch(SQLException e) {
//                syserr("SQLException while trying to delete room as admin.");
//                e.printStackTrace();
//            } catch(MissingPrimaryKeyException e) {
//                syserr("Some how couldn't find that room we added earlier.");
//            }
        }
        
        private static void runEmployeeControlTest() {
            
            try {
                Employee testEmp = testEmp();
                EmployeeDatabase.getInstance().addEmployee(testEmp);
                String currentLogin = testEmp.getLoginId();
                String newLogin = "newLogin";
                
                EmployeeControl.addEvent(testEmp, timeslot3);
                sysout("Employee added event!");
                
                testEmp.setLoginId(newLogin);
                EmployeeControl.updateEmployee(currentLogin, testEmp);
                
                if(EmployeeDatabase.getInstance().getEmployee(newLogin) != null) {
                    sysout("Hooray! Employee login updated!");
                }
                
                TreeSet<TimeSlot> schedule = EmployeeControl.getPartialSchedule(testEmp, 5);
                
                sysout("Partial Schedule!");
                for(TimeSlot event : schedule) {
                    sysout(event.toString());
                }
                EmployeeControl.removeEvent(testEmp, timeslot3);
                sysout("Employee removed event! Event correctly updated login_id via mysql!");
                
                EmployeeDatabase.getInstance().deleteEmployee(testEmp);
            } catch (SQLException e) {
                syserr("SQLExcpetion in employee control test.");
                e.printStackTrace();
            } catch(MissingPrimaryKeyException e) {
                syserr("Somehow, added employee could not be found by primary key. Strange ...");
            } catch(EntityNotFoundException e) {
                syserr("Uh oh, new login didn't get to database.");
            }
        }
        
        private static void runMeetingControlTest() {
            try {
                Employee owner = testEmp();
                Employee invitee = testEmp2();
                Employee invitee2 = testEmp3();
                Room room = testRoom();
                
                addEmp(owner);
                addEmp(invitee);
                addEmp(invitee2);
                addRoom(room);
                
                AdminControl.addRoomEvent(room, event(HOUR_IN_MILLISECONDS));
                
                Map<Room, SortedSet<TimeSlot>> times = MeetingControl.getCoincidingTimes(1, invitee, invitee2);
                
                for(Room r : times.keySet()) {
                    sysout(r.getLocation() + " " + schedule(times.get(r)));
                }
                
                delEmp(owner);
                delEmp(invitee);
                delEmp(invitee2);
                delRoom(room);
            } catch (SQLException e) {
                syserr("SQLException during meeting control test.");
                e.printStackTrace();
            } catch (MissingPrimaryKeyException e) {
                syserr("Somehow, some way, we could not find a primary key for something we created in meeting control test.");
            }
        }
        
        private static TimeSlot event(long offsetFromCurrentTime, long duration) {
            Date start = new Date(now().getTime() + offsetFromCurrentTime);
            Date end = new Date(start.getTime() + duration);
            TimeSlot event = new TimeSlot()
                    .setStartTime(start)
                    .setEndTime(end)
                    .isVisible(true);
            return event;
        }
        
        private static TimeSlot event(long duration) {
            Date start = now();
            Date end = new Date(start.getTime() + duration);
            TimeSlot event = new TimeSlot()
                    .setStartTime(start)
                    .setEndTime(end)
                    .isVisible(true);
            return event;
            
        }
        
        private static String schedule(SortedSet<TimeSlot> schedule) {
            String scheduleString = "";
            for(TimeSlot event : schedule) {
                scheduleString += event.toString() + " ";
            }
            return scheduleString;
        }
        
        private static void addEmp(Employee emp) throws SQLException {
            EmployeeDatabase.getInstance().addEmployee(emp);
        }
        
        private static void delEmp(Employee emp) throws SQLException, MissingPrimaryKeyException {
            EmployeeDatabase.getInstance().deleteEmployee(emp);
        }
        
        private static void addRoom(Room room) throws SQLException {
            RoomDatabase.getInstance().addRoom(room);
        }
        
        private static void delRoom(Room room) throws SQLException, MissingPrimaryKeyException {
            RoomDatabase.getInstance().deleteRoom(room);
        }
        
        private static void syserr(String str) {
            System.err.println(str);
        }
        
        private static void sysout(String str) {
            System.out.println(str);
        }
        
        private static Employee testEmp() {
            return new Employee()
                        .setLoginId("testEmp")
			.isAdmin(false)
			.setName("testman")
			.setPassword("pass");
        }
        
        private static Employee testEmp2() {
            return new Employee()
                        .setLoginId("testEmp2")
                        .isAdmin(true)
                        .setName("testotherman")
                        .setPassword("pass");
        }
        
        private static Employee testEmp3() {
            return new Employee()
                    .setLoginId("testEmp3")
                    .isAdmin(false)
                    .setName("testotherotherman")
                    .setPassword("pass");
        }
        
        private static Room testRoom() {
            return new Room()
                    .setLocation("test")
                    .setCapacity(10);
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
        
        
        
        private static void createAndShowGUI() {
        System.out.println("Created GUI on EDT? "+
        SwingUtilities.isEventDispatchThread());
        JFrame f = new JFrame("Scheduler");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new Login());                        //test page
        f.pack();
        f.setVisible(true);
        f.setLocationRelativeTo(null);
    }
}
