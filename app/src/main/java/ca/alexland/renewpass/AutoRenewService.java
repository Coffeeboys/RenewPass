package ca.alexland.renewpass;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import ca.alexland.renewpass.model.Callback;
import ca.alexland.renewpass.model.Status;
import ca.alexland.renewpass.utils.AlarmReceiver;
import ca.alexland.renewpass.utils.AlarmUtil;
import ca.alexland.renewpass.utils.LoggerUtil;
import ca.alexland.renewpass.utils.NotifyUtil;
import ca.alexland.renewpass.utils.UPassLoader;

/**
 * Service to perform the renewal process in the background, triggered from an alarm.
 */
public class AutoRenewService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        //The UPassLoader starts a new thread to do work in,
        //so this doesn't block the app process main thread
        UPassLoader.renewUPass(this, new Callback() {
            @Override
            public void onUPassLoaded(Status result) {
                LoggerUtil.appendLog(AutoRenewService.this, "AlarmReceiver renew status: " + result.getStatusText());
                if (result.isSuccessful()) {
                    doSuccess(AutoRenewService.this);
                }
                else {
                    doFailure(AutoRenewService.this);
                }
                AlarmReceiver.completeWakefulIntent(intent);
                stopSelf();
            }
        });
        //make sure that if the android system decides to kill this service
        //that it is restarted later so that it can complete auto-renewal
        return Service.START_REDELIVER_INTENT;
    }


    private void doSuccess(Context context) {
        NotifyUtil.showSuccessNotification(context);
        AlarmUtil.setNextMonthAlarm(context);
    }

    private void doFailure(Context context) {
        NotifyUtil.showFailureNotification(context);
        AlarmUtil.setNextDayAlarm(context);
    }
}
