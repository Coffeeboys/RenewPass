package ca.alexland.renewpass.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;


/**
 * Created by AlexLand on 2016-01-14.
 */
public class AlarmUtil {
    public static void setAlarm(Context context) {
        Intent intent = new Intent(context, Receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                System.currentTimeMillis(), AlarmManager.INTERVAL_DAY * 5, pendingIntent);
    }
}
