package ca.alexland.renewpass.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ca.alexland.renewpass.AutoRenewService;
import ca.alexland.renewpass.model.Callback;
import ca.alexland.renewpass.model.Status;

/**
 * Created by AlexLand on 2016-01-14.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        LoggerUtil.appendLog(context, "Alarm received");
        final PreferenceHelper preferenceHelper = PreferenceHelper.getInstance(context);
        String intentAction = intent.getAction();
        if (intentAction == null) {
            intentAction = "Default";
        }
        LoggerUtil.appendLog(context, "Alarm intent action: " + intentAction);
        switch(intentAction) {
            case Intent.ACTION_BOOT_COMPLETED :
                if (preferenceHelper.getNotificationsEnabled()) {
                    AlarmUtil.setAlarmAtTime(context, preferenceHelper.getLastScheduledNotificationTime());
                }
                break;
            default:
                //the AutoRenewService will stop itself when it has finished renewing
                context.startService(new Intent(context, AutoRenewService.class));
        }
    }
}