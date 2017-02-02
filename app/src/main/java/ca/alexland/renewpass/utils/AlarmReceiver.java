package ca.alexland.renewpass.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import ca.alexland.renewpass.AutoRenewService;
import ca.alexland.renewpass.model.Callback;
import ca.alexland.renewpass.model.Status;

/**
 * Receives alarms from the OS for automatic renewal and package re-installation.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        LoggerUtil.appendLog(context.getApplicationContext(), "Alarm received");
        final PreferenceHelper preferenceHelper = PreferenceHelper.getInstance(context);
        String intentAction = intent.getAction();
        if (intentAction == null) {
            intentAction = "Default";
        }
        LoggerUtil.appendLog(context.getApplicationContext(), "Alarm intent action: " + intentAction);
        switch(intentAction) {
            case Intent.ACTION_BOOT_COMPLETED :
                if (preferenceHelper.getNotificationsEnabled()) {
                    AlarmUtil.setAlarmAtTime(context, preferenceHelper.getLastScheduledNotificationTime());
                }
                break;
            case Intent.ACTION_MY_PACKAGE_REPLACED:
                if (preferenceHelper.getNotificationsEnabled()) {
                    AlarmUtil.setAlarmAtTime(context, preferenceHelper.getLastScheduledNotificationTime());
                }
                break;
            default:
                //the AutoRenewService will stop itself when it has finished renewing
                startWakefulService(context, new Intent(context, AutoRenewService.class));
//                context.startService();
        }
    }
}