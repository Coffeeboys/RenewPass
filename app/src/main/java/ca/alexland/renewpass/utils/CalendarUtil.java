package ca.alexland.renewpass.utils;

import android.content.Context;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Trevor on 1/26/2016.
 */
public class CalendarUtil {
    private static Calendar calendar = new GregorianCalendar();

    public static long getNextMonthTimeInMillis(long currTimeInMillis) {
        calendar.setTimeInMillis(currTimeInMillis);
        int nextMonth = (calendar.get(Calendar.MONTH) + 1) % 12;
        calendar.set(Calendar.MONTH, nextMonth);
        if (nextMonth == 0) {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
        }
        return calendar.getTimeInMillis();
    }

    public static String convertDateToString(Context context, Calendar calendar) {
        return convertDateToString(context, calendar.getTimeInMillis());
    }

    public static String convertDateToString(Context context, long timeInMillis) {
        return DateFormat.getDateFormat(context).format(new Date(timeInMillis));
    }

    public static String convertTimeToString(Context context, Calendar calendar) {
        return convertTimeToString(context, calendar.getTimeInMillis());
    }

    public static String convertTimeToString(Context context, long timeInMillis) {
        return DateFormat.getTimeFormat(context).format(new Date(timeInMillis));
    }
}
