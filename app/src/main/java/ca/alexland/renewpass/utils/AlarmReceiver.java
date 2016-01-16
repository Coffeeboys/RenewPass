package ca.alexland.renewpass.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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
        Bundle bundle = intent.getBundleExtra("bundle");
        final boolean notificationsEnabled = bundle.getBoolean(PreferenceHelper.NOTIFICATIONS_ENABLED_PREFERENCE);
        UPassLoader.checkUPassAvailable(context, new Callback() {
            @Override
            public void onUPassLoaded(Status result) {
                if (result.getStatusText().equals(Status.UPASS_AVAILABLE)) {
                    if (notificationsEnabled) {
                        showNotification(context);
                        AlarmUtil.setAlarm(context, false);
                    }
                }
                else {
                    AlarmUtil.setAlarm(context, true);
                }
            }
        });
    }

    public void showNotification(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_autorenew)
                .setContentTitle(context.getString(R.string.available_notification_title))
                .setContentText(context.getString(R.string.available_notification_text));
        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
