package ca.alexland.renewpass.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
        Toast.makeText(context, "Received!", Toast.LENGTH_LONG).show();
        final PendingResult pendingResult = goAsync();
        final boolean notificationsEnabled = intent.getBooleanExtra(PreferenceHelper.EXTRA_NOTIFICATIONS_ENABLED, false);
        UPassLoader.checkUPassAvailable(context, new Callback() {
            @Override
            public void onUPassLoaded(Status result) {
                if (result.getStatusText().equals(Status.UPASS_AVAILABLE) ||
                        result.getStatusText().equals(Status.NOTHING_TO_RENEW)) {
                    if (notificationsEnabled) {
                        showSuccessNotification(context);
                        //TODO: make this set an alarm a month from now instead of setting the same alarm
                        AlarmUtil.setAlarm(context, false);
                    }
                }
                else {
                    if (notificationsEnabled) {
                        showFailureNotification(context);
                    }
                    AlarmUtil.setAlarm(context, true);
                }
                pendingResult.finish();
            }
        });
    }

    private void showSuccessNotification(Context context) {
        showNotification(context,
                context.getString(R.string.available_notification_title),
                context.getString(R.string.available_notification_text));
    }

    private void showFailureNotification(Context context) {
        showNotification(context,
                context.getString(R.string.unavailable_notification_title),
                context.getString(R.string.unavailable_notification_text));
    }

    private void showNotification(Context context, String contentTitle, String contentText) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_autorenew)
                .setContentTitle(contentTitle)
                .setContentText(contentText);
        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
