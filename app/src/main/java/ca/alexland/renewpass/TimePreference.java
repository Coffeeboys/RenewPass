package ca.alexland.renewpass;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ca.alexland.renewpass.utils.SimpleTimeFormat;

/**
 * Created by Trevor on 1/21/2016.
 * A Time Preference for use when setting the default time for
 */
public class TimePreference extends DialogPreference {

    private Calendar calendar;
    private TimePickerCompat timePicker;
    private String defaultValue;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        defaultValue = context.getString(R.string.preference_value_notification_default_time);
        setPositiveButtonText(getContext().getResources().getString(R.string.preference_dialog_button_positive));
        setNegativeButtonText(getContext().getResources().getString(R.string.preference_dialog_button_negative));
        calendar = new GregorianCalendar();
    }

    @Override
    protected View onCreateDialogView() {
        timePicker = new TimePickerCompat(new TimePicker(getContext()));
        return timePicker.getTimePicker();
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        timePicker.setHourCompat(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinuteCompat(calendar.get(Calendar.MINUTE));
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object xmlDefaultValue) {
        String timeVal;
        if (restorePersistedValue) {
            //this is kind of confusing... what it's doing is:
            //try to set the value to the value stored in userprefs
            //if somehow, this fails, set it to the default (specified in xml).
            //if the default isn't specified, set it to the hardcoded default value
            if (xmlDefaultValue == null) {
                timeVal = getPersistedString(defaultValue);
            } else {
                timeVal = getPersistedString((String) xmlDefaultValue);
            }
        } else {
            if (xmlDefaultValue == null) {
                timeVal = defaultValue;
            } else {
                timeVal = (String) xmlDefaultValue;
            }
            //set the time so that there will be a value to restore next time this is called
            //setTime();
        }
        SimpleTimeFormat simpleTimeFormat = new SimpleTimeFormat(timeVal);
        calendar.set(Calendar.HOUR_OF_DAY, simpleTimeFormat.getHour());
        calendar.set(Calendar.MINUTE, simpleTimeFormat.getMinute());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHourCompat());
            calendar.set(Calendar.MINUTE, timePicker.getMinuteCompat());

            if (callChangeListener(calendar.getTimeInMillis())) {
                setTime();
            }
        }
    }

    @Override
    public CharSequence getSummary() {
        if (calendar == null) {
            return null;
        }
        return DateFormat.getTimeFormat(getContext()).format(new Date(calendar.getTimeInMillis()));
    }

    private void setTime() {
        persistString(new SimpleTimeFormat(
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE))
                .toString());
        notifyChanged();
    }

    //-----------------------Gross sub-class--------------------------------------------------------
    private class TimePickerCompat {
        private TimePicker timePicker;

        public TimePickerCompat(TimePicker timePicker) {
            this.timePicker = timePicker;
        }

        public TimePicker getTimePicker() {
            return this.timePicker;
        }

        public int getHourCompat() {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                return timePicker.getHour();
            } //else
            return timePicker.getCurrentHour();
        }

        public int getMinuteCompat() {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                return timePicker.getMinute();
            } //else
            return timePicker.getCurrentMinute();
        }

        public void setMinuteCompat(int minute) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                timePicker.setMinute(minute);
            } //else
            timePicker.setCurrentMinute(minute);
        }

        public void setHourCompat(int hour) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                timePicker.setHour(hour);
            } //else
            timePicker.setCurrentHour(hour);
        }
    }
}
