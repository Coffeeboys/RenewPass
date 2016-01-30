package ca.alexland.renewpass.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;


/**
 * Created by AlexLand on 2016-01-14.
 */
public class AlarmUtil {
    /**
     * If urgent is true, Sets the alarm for the next month as per the date from preferences, else
     * sets an alarm for each day
     */
    private static final int ALARM_REQUEST_CODE = 0;

    public static void setAlarm(Context context, boolean urgent) {
        PreferenceHelper preferenceHelper = PreferenceHelper.getInstance(context);
        PendingIntent pendingIntent = createPendingIntent(context, preferenceHelper);
        // Set an alarm to check every day if we didn't get the availability successfully
        //TODO: delete debug toast messages
        if (urgent) {
            Toast.makeText(context, "Setting alarm to be checked again in a day", Toast.LENGTH_LONG).show();
            setAlarm(context, System.currentTimeMillis() + AlarmManager.INTERVAL_DAY, pendingIntent, preferenceHelper);
        }
        else {
            Calendar cal = preferenceHelper.getNextNotificationDate();
            Toast.makeText(context, "Setting Alarm for" +
                    " Date: " + cal.get(Calendar.DATE) +
                    " Hour: " + cal.get(Calendar.HOUR_OF_DAY) +
                    " Minute: " + cal.get(Calendar.MINUTE), Toast.LENGTH_LONG).show();
            setAlarm(context, cal.getTimeInMillis(), pendingIntent, preferenceHelper);
        }
    }

    public static void setAlarmNextMonth(Context context) {
        PreferenceHelper preferenceHelper = new PreferenceHelper(context);
        PendingIntent pendingIntent = createPendingIntent(context, preferenceHelper);
        Calendar cal = preferenceHelper.getNextNotificationDate();
        setAlarm(context, CalendarUtil.getNextMonthTimeInMillis(cal.getTimeInMillis()), pendingIntent, preferenceHelper);
    }

    public static void setAlarmAtTime(Context context, long timeInMillis) {
        PreferenceHelper preferenceHelper = new PreferenceHelper(context);
        PendingIntent pendingIntent = createPendingIntent(context, preferenceHelper);
        setAlarm(context, timeInMillis, pendingIntent, preferenceHelper);
    }

    private static void setAlarm(Context context, long timeInMillis, PendingIntent pendingIntent, PreferenceHelper preferenceHelper) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent);
        preferenceHelper.setLastScheduledNotificationTime(timeInMillis);
    }

    public static void cancelAlarm(Context context) {
        PendingIntent pendingIntent = createPendingIntent(context, new PreferenceHelper(context));
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);
    }

    private static PendingIntent createPendingIntent(Context context, PreferenceHelper preferenceHelper) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(PreferenceHelper.EXTRA_NOTIFICATIONS_ENABLED, preferenceHelper.getNotificationsEnabled());
        return PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
