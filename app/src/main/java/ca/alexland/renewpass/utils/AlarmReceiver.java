package ca.alexland.renewpass.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import ca.alexland.renewpass.model.Callback;
import ca.alexland.renewpass.model.Status;

/**
 * Created by AlexLand on 2016-01-14.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
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
                break;
            default:
                final PendingResult pendingResult = goAsync();
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
        AlarmUtil.setNextDayAlarm(context);
    }

    private void doSuccess(Context context, PreferenceHelper preferenceHelper) {
        NotifyUtil.showSuccessNotification(context);
        AlarmUtil.setNextMonthAlarm(context);
    }
}