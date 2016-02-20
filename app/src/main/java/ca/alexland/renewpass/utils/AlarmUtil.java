package ca.alexland.renewpass.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;


/**
 * Created by AlexLand on 2016-01-14.
 */
public class AlarmUtil {
    private static final int ALARM_REQUEST_CODE = 0;

    /**
     * Sets the next alarm according to the current notification values saved in preference helper.
     * If the currently saved day and time has already passed for the current month,
     * this will automatically schedule an alarm for the next month
     */
    public static void setNextAlarm(Context context) {
        PreferenceHelper preferenceHelper = PreferenceHelper.getInstance(context);
        Calendar cal = preferenceHelper.getNextNotificationDate();
        setAlarm(context, cal.getTimeInMillis(), preferenceHelper);
    }

    /**
     * Sets the alarm for the month after the month saved in preference helper.
     * This is useful if you want to force the alarm to be scheduled for the next month
     * ex: when renewing the user's Upass, if the alarm is not forced to the next month
     * and the renew process finishes before the minute that the alarm is scheduled for,
     * then the alarm will be rescheduled for the same month instead of the next month.
     * Using this method will force it to be scheduled for the next month
     */
    public static void setNextMonthAlarm(Context context) {
        PreferenceHelper preferenceHelper = PreferenceHelper.getInstance(context);
        Calendar cal = preferenceHelper.getNextMonthNotificationDate();
        setAlarm(context, cal.getTimeInMillis(), preferenceHelper);
    }

    public static void setNextDayAlarm(Context context) {
        PreferenceHelper preferenceHelper = PreferenceHelper.getInstance(context);
        setAlarm(context, System.currentTimeMillis() + AlarmManager.INTERVAL_DAY, preferenceHelper);
    }

    public static void setAlarmAtTime(Context context, long timeInMillis) {
        PreferenceHelper preferenceHelper = PreferenceHelper.getInstance(context);
        setAlarm(context, timeInMillis, preferenceHelper);
    }

    private static void setAlarm(Context context, long timeInMillis, PreferenceHelper preferenceHelper) {
        PendingIntent pendingIntent = createPendingIntent(context, preferenceHelper);
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent);
        preferenceHelper.setLastScheduledNotificationTime(timeInMillis);
        LoggerUtil.appendLog(context, "Alarm set for " + CalendarUtil.convertDateToString(context, timeInMillis)
                + " at " + CalendarUtil.convertTimeToString(context, timeInMillis));
    }

    public static void cancelAlarm(Context context) {
        PendingIntent pendingIntent = createPendingIntent(context, PreferenceHelper.getInstance(context));
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);
        LoggerUtil.appendLog(context, "Alarm cancelled");
    }

    private static PendingIntent createPendingIntent(Context context, PreferenceHelper preferenceHelper) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(PreferenceHelper.EXTRA_NOTIFICATIONS_ENABLED, preferenceHelper.getNotificationsEnabled());
        return PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
