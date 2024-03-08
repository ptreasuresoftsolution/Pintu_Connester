package com.connester.job.function;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    //ALL DATE Function

    public static String getStringDate(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date);
    }
    public static String getStringDate(String format, Date date) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String getStringDate(String argDateFormat, String returnDateFormate, String date) {

        SimpleDateFormat spf = new SimpleDateFormat(argDateFormat);
        Date newDate = null;
        try {
            newDate = spf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat(returnDateFormate);
        date = spf.format(newDate);

        return date;
    }
    public static Date getObjectDate(String argDateFormat, String date) {

        SimpleDateFormat spf = new SimpleDateFormat(argDateFormat);
        Date newDate = null;
        try {
            newDate = spf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String convertSecondsToDate(Long seconds) {
        try {

            Date date = new Date(seconds);
            // formattter
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            // Pass date object
            String formatted = formatter.format(date);
            return formatted;
        } catch (Exception e) {
            e.printStackTrace();
            return String.valueOf(seconds);
        }
    }


    public static String DateToMonth(String dateYMD) {

        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = spf.parse(dateYMD);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("MMM");
        dateYMD = spf.format(newDate);
        return dateYMD;
    }

    public static String DateToDayInWeek(String dateYMD) {

        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = spf.parse(dateYMD);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("EEEE");
        dateYMD = spf.format(newDate);
        return dateYMD;
    }

    public static String DateToMonthDate(String dateYMD) {

        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = spf.parse(dateYMD);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("dd");
        dateYMD = spf.format(newDate);
        return dateYMD;
    }
    //external use

    public static String TODAYDATEforDB() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static String TODAYDATETIMEforDB() {
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static Date minusDays(Date date, int days) {
        long oneDay = 24 * 3600 * 1000;
        return new Date(date.getTime() - days * oneDay);
    }

    public static Date plusDays(Date date, int days) {
        long oneDay =  24 * 3600 * 1000;
        return new Date(date.getTime() + days * oneDay);
    }
    public static Date minusMinute(Date date, int minute) {
        long oneMinute = 60 * 1000;
        return new Date(date.getTime() - minute * oneMinute);
    }

    public static Date plusMinute(Date date, int minute) {
        long oneMinute =  60 * 1000;
        return new Date(date.getTime() + minute * oneMinute);
    }
    // date diff
    public static long dateDiff(String interval, String join_date_YMDHMS, String leave_date_YMDHMS) {
        /*
         * $interval can be:
         * Y - Number of full years
         * q - Number of full quarters
         * m - Number of full months
         * d - Number of full days
         * w - Number of full weeks
         * h - Number of full hours
         * n - Number of full minutes
         * s - Number of full seconds
         */

        // Create an instance of the SimpleDateFormat class
        SimpleDateFormat obj = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // In the try block, we will try to find the difference
        try {
            // Use parse method to get date object of both dates
            Date date1 = obj.parse(join_date_YMDHMS);
            Date date2 = obj.parse(leave_date_YMDHMS);
            // Calucalte time difference in milliseconds
            long time_difference = date2.getTime() - date1.getTime();

            if (interval.equals("Y")) {
                // Calucalte time difference in years
                return (time_difference / (1000l * 60 * 60 * 24 * 365));
            }
            if (interval.equals("q")) {
                // Calucalte time difference in quarters
                return (time_difference / (1000l * 60 * 60 * 24 * 93));
            }
            if (interval.equals("m")) {
                // Calucalte time difference in months
                return (time_difference / (1000l * 60 * 60 * 24 * 31));
            }
            if (interval.equals("d")) {
                // Calucalte time difference in days
                return (time_difference / (1000 * 60 * 60 * 24));
            }
            if (interval.equals("w")) {
                // Calucalte time difference in weeks
                return (time_difference / (1000 * 60 * 60 * 24 * 7));
            }
            if (interval.equals("h")) {
                // Calucalte time difference in hours
                return (time_difference / (1000 * 60 * 60));
            }
            if (interval.equals("n")) {
                // Calucalte time difference in minutes
                return (time_difference / (1000 * 60));
            }
            if (interval.equals("s")) {
                // Calucalte time difference in seconds
                return (time_difference / (1000));
            }
        }
        // Catch parse exception
        catch (ParseException excep) {
            excep.printStackTrace();
        }
        return 0;
    }
    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
    public static Date toDate(Calendar calendar){
        return calendar.getTime();
    }
}
