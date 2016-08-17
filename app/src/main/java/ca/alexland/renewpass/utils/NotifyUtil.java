package ca.alexland.renewpass.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import ca.alexland.renewpass.MainActivity;
import ca.alexland.renewpass.R;

/**
 * Handles the creation of auto-renew notifications.
 */
public class NotifyUtil {
    //https://gist.github.com/BrandonSmith/6679223
    //http://nnish.com/2014/12/16/scheduled-notifications-in-android-using-alarm-manager/

    public static void showSuccessNotification(Context context) {
        showNotification(context,
                context.getString(R.string.available_notification_title),
                context.getString(R.string.available_notification_expanded_text),
                context.getString(R.string.available_notification_short_text));
    }

    public static void showFailureNotification(Context context) {
        // TODO: Add retry button on failure notification
        showNotification(context,
                context.getString(R.string.unavailable_notification_title),
                context.getString(R.string.unavailable_notification_expanded_text),
                context.getString(R.string.unavailable_notification_short_text));
    }

    private static void showNotification(Context context, String title, String expandedText, String shortText) {
        NotificationCompat.BigTextStyle notificationStyle = new NotificationCompat.BigTextStyle()
                .setBigContentTitle(title)
                .bigText(expandedText);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_autorenew)
                .setContentTitle(title)
                .setContentText(shortText)
                .setContentIntent(pi)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setStyle(notificationStyle);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

}
