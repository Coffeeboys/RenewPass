package ca.alexland.renewpass.utils;

import java.text.SimpleDateFormat;

/**
 * Created by Trevor on 1/21/2016.
 * Class that can return the hours and minutes from a string of the form HH:MM
 */
public class SimpleTimeFormat {
    //TODO: Change this to wrap/use a SimpleDateFormat, then just offer a nice interface for retrieving values from it
    String time;

    public SimpleTimeFormat(String time) {
        this.time = time;
    }
    public SimpleTimeFormat(int hour, int minute) {
        time = hour + ":" + minute;
    }

    public int getHour() {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    public int getMinute() {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }

    @Override
    public String toString() {
        return time;
    }
}
