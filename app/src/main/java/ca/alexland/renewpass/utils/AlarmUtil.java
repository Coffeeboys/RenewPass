package ca.alexland.renewpass.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;


/**
 * Created by AlexLand on 2016-01-14.
 */
public class AlarmUtil {
    private static final int ALARM_REQUEST_CODE = 0;

    public static void setAlarm(Context context, boolean urgent) {
        PreferenceHelper preferenceHelper = new PreferenceHelper(context);
        PendingIntent pendingIntent = createPendingIntent(context, preferenceHelper);
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        // Set an alarm to check every day if we didn't get the availability successfully
        //TODO: delete debug toast messages
        if (urgent) {
            Toast.makeText(context, "Setting alarm to be checked again in a day", Toast.LENGTH_LONG).show();
            am.set(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
        else {
            Calendar cal = preferenceHelper.getDate();
            Toast.makeText(context, "Setting Alarm for" +
                    " Date: " + cal.get(Calendar.DATE) +
                    " Hour: " + cal.get(Calendar.HOUR_OF_DAY) +
                    " Minute: " + cal.get(Calendar.MINUTE), Toast.LENGTH_LONG).show();
            am.set(AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(),
                    pendingIntent);
        }
    }

    public static void cancelAlarm(Context context) {
        PendingIntent pendingIntent = createPendingIntent(context, new PreferenceHelper(context));
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);
    }

    private static PendingIntent createPendingIntent(Context context, PreferenceHelper preferenceHelper) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(PreferenceHelper.EXTRA_NOTIFICATIONS_ENABLED, preferenceHelper.getNotificationsEnabled());
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("bundle", bundle);
        return PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
