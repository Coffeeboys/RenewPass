package ca.alexland.renewpass.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import ca.alexland.renewpass.MainActivity;
import ca.alexland.renewpass.R;
import ca.alexland.renewpass.model.Callback;
import ca.alexland.renewpass.model.Status;

/**
 * Created by AlexLand on 2016-01-14.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        //TODO: REMOVE ALL TOAST DEBUG MESSAGES
        final PreferenceHelper preferenceHelper = PreferenceHelper.getInstance(context);
        String intentAction = intent.getAction();
        if (intentAction == null) {
            intentAction = "Default";
        }
        switch(intentAction) {
            case Intent.ACTION_BOOT_COMPLETED :
                if (preferenceHelper.getNotificationsEnabled()) {
                    AlarmUtil.setAlarmAtTime(context, preferenceHelper.getLastScheduledNotificationTime());
                }
                Toast.makeText(context, "Restoring alarms. Notifications: " + preferenceHelper.getNotificationsEnabled()
                        + "Last Alarm date set for: " + CalendarUtil.convertDateToString(context, preferenceHelper.getLastScheduledNotificationTime())
                        + "At time: " + CalendarUtil.convertTimeToString(context, preferenceHelper.getLastScheduledNotificationTime()), Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, "Received!", Toast.LENGTH_LONG).show();
                final PendingResult pendingResult = goAsync();
//                final boolean notificationsEnabled = intent.getBooleanExtra(PreferenceHelper.EXTRA_NOTIFICATIONS_ENABLED, false);
                UPassLoader.renewUPass(context, new Callback() {
                    @Override
                    public void onUPassLoaded(Status result) {
                        if (result.isSuccessful()) {
                            doSuccess(context, preferenceHelper);
                        }
                        else {
                            doFailure(context);
                        }
                        pendingResult.finish();
                    }
                });
        }
    }

    private void doFailure(Context context) {
        NotifyUtil.showFailureNotification(context);
        AlarmUtil.setNextHourAlarm(context);
        Toast.makeText(context,
                "Failure! retry alarm set for: " +
                        CalendarUtil.convertDateToString(context, System.currentTimeMillis() + AlarmManager.INTERVAL_DAY), Toast.LENGTH_LONG)
                .show();
    }

    private void doSuccess(Context context, PreferenceHelper preferenceHelper) {
        NotifyUtil.showSuccessNotification(context);
        AlarmUtil.setNextMonthAlarm(context);
        Toast.makeText(context,
                "Renewed! next alarm set for: " +
                        CalendarUtil.convertDateToString(context, preferenceHelper.getNextMonthNotificationDate()), Toast.LENGTH_LONG)
                .show();
    }
}