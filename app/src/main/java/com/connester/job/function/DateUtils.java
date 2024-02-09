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
}
