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
    private static final int ALARM_REQUEST_CODE = 0;

    /**
     * Sets the next alarm according to the current notification values saved in preference helper.
     * If the currently saved day and time has already passed for the current month,
     * this will automatically schedule an alarm for the next month
     */
    public static void setNextAlarm(Context context) {
        PreferenceHelper preferenceHelper = PreferenceHelper.getInstance(context);
        //TODO: delete debug toast messages
        Calendar cal = preferenceHelper.getNextNotificationDate();
        Toast.makeText(context, "Setting Alarm for" +
                " Date: " + cal.get(Calendar.DATE) +
                " Hour: " + cal.get(Calendar.HOUR_OF_DAY) +
                " Minute: " + cal.get(Calendar.MINUTE), Toast.LENGTH_LONG).show();
        setAlarm(context, cal.getTimeInMillis(), preferenceHelper);
    }

    public static void setAlarmNextDay(Context context) {
        PreferenceHelper preferenceHelper = PreferenceHelper.getInstance(context);
        //TODO: remove toast messages
        Toast.makeText(context, "Setting alarm to be checked again in a day", Toast.LENGTH_LONG).show();
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
    }

    public static void cancelAlarm(Context context) {
        PendingIntent pendingIntent = createPendingIntent(context, PreferenceHelper.getInstance(context));
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);
    }

    private static PendingIntent createPendingIntent(Context context, PreferenceHelper preferenceHelper) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(PreferenceHelper.EXTRA_NOTIFICATIONS_ENABLED, preferenceHelper.getNotificationsEnabled());
        return PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
