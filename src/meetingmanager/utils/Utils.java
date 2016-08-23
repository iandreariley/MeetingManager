/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meetingmanager.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class Utils {
    
    public static final long HOUR_IN_MILLISECONDS = 60 * 60 * 1000;
    public static final long DAY_IN_MILLISECONDS = 24 * HOUR_IN_MILLISECONDS;
    public static final SimpleDateFormat LONG_DATETIME_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
    
    public static long hoursToMilliseconds(double hours) {
        return (long) (hours * 60 * 60 * 1000);
    }
    
    public static long elapsedTime(Date start, Date end) {
        return end.getTime() - start.getTime();
    }
    
    public static Date timeAfterInterval(Date startTime, long durationInMilliseconds) {
        return new Date(startTime.getTime() + durationInMilliseconds);
    }
    
    public static Date timeAfterInterval(Date startTime, double durationInHours) {
        return new Date(startTime.getTime() + hoursToMilliseconds(durationInHours));
    }
    
    public static Date now() {
        return new Date(Calendar.getInstance().getTimeInMillis());
    }
}
