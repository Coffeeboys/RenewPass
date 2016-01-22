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

/**
 * Created by Trevor on 1/21/2016.
 * A Time Preference for use when setting the default time for
 */
public class TimePreference extends DialogPreference {
    private Calendar calendar;
    private TimePickerCompat timePicker;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
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
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            //this is kind of confusing... what it's doing is:
            //try to set the value to the value stored in userprefs
            //if somehow, this fails, set it to the default (specified in xml).
            //if the default isn't specified, set it to 0
            if (defaultValue == null) {
                calendar.setTimeInMillis(getPersistedLong(0));
            } else {
                calendar.setTimeInMillis(getPersistedLong(getCalendarMillisFromString((String)defaultValue)));
            }
        } else {
            if (defaultValue == null) {
                calendar.setTimeInMillis(0);
            } else {
                calendar.setTimeInMillis(getCalendarMillisFromString((String) defaultValue));
            }
            //set the time so that there will be a value to restore next time this is called
            setTime(calendar.getTimeInMillis());
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHourCompat());
            calendar.set(Calendar.MINUTE, timePicker.getMinuteCompat());

            if (callChangeListener(calendar.getTimeInMillis())) {
                setTime(calendar.getTimeInMillis());
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

    private void setTime(long time) {
        persistLong(time);
        notifyChanged();
    }

    private Calendar helperCalendar;

    private long getCalendarMillisFromString(String defaultValue) {
        if (helperCalendar == null) {
            helperCalendar = new GregorianCalendar();
        }
        helperCalendar.set(Calendar.HOUR_OF_DAY, getHour(defaultValue));
        helperCalendar.set(Calendar.MINUTE, getMinute(defaultValue));
        return helperCalendar.getTimeInMillis();
    }

    public static int getHour(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
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
