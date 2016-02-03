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
                final boolean notificationsEnabled = intent.getBooleanExtra(PreferenceHelper.EXTRA_NOTIFICATIONS_ENABLED, false);
                UPassLoader.checkUPassAvailable(context, new Callback() {
                    @Override
                    public void onUPassLoaded(Status result) {
                        if (result.getStatusText().equals(Status.UPASS_AVAILABLE) ||
                                result.getStatusText().equals(Status.NOTHING_TO_RENEW)) {
                            //is this overkill to check if notifications are enabled here
                            //since they are also being cancelled when notifications are turned off?
                            if (notificationsEnabled) {
                                showSuccessNotification(context);
                                AlarmUtil.setNextAlarm(context);
                                Toast.makeText(context,
                                        "Renewed! next alarm set for: " +
                                                CalendarUtil.convertDateToString(context, preferenceHelper.getNextNotificationDate()), Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                        else {
                            if (notificationsEnabled) {
                                showFailureNotification(context);
                            }
                            AlarmUtil.setAlarmNextHour(context);
                            Toast.makeText(context,
                                    "Failure! retry alarm set for: " +
                                            CalendarUtil.convertDateToString(context, System.currentTimeMillis() + AlarmManager.INTERVAL_DAY), Toast.LENGTH_LONG)
                                    .show();
                        }
                        pendingResult.finish();
                    }
                });
        }
    }

    private void showSuccessNotification(Context context) {
        showNotification(context,
                context.getString(R.string.available_notification_short_title),
                context.getString(R.string.available_notification_title),
                context.getString(R.string.available_notification_text));
    }

    private void showFailureNotification(Context context) {
        showNotification(context,
                context.getString(R.string.unavailable_notification_short_title),
                context.getString(R.string.unavailable_notification_title),
                context.getString(R.string.unavailable_notification_text));
    }

    private void showNotification(Context context, String contentShortTitle, String contentTitle, String contentText) {
        NotificationCompat.BigTextStyle notificationStyle = new NotificationCompat.BigTextStyle()
                .setBigContentTitle(contentShortTitle)
                .bigText(contentTitle)
                .setSummaryText(contentText);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_autorenew)
                .setContentTitle(contentShortTitle)
                .setContentText(contentTitle);
        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        mBuilder.setStyle(notificationStyle);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}