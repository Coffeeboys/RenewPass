package ca.alexland.renewpass.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Created by Trevor on 1/30/2016.
 */
public class CalendarUtilTest {

    @Test
    public void testGetNextMonthTimeInMillis() {
        Calendar calendar = new GregorianCalendar(2015, 11, 21); //11 is december
        calendar.setTimeInMillis(CalendarUtil.getNextMonthTimeInMillis(calendar.getTimeInMillis()));
        assertEquals(0, calendar.get(Calendar.MONTH)); //january
        assertEquals(2016, calendar.get(Calendar.YEAR));

        for (int expectedMonth = 1; expectedMonth < 12; expectedMonth++) {
            int dayBeforeChange = calendar.get(Calendar.DAY_OF_MONTH);
            int hourBeforeChange = calendar.get(Calendar.HOUR_OF_DAY);
            int minuteBeforeChange = calendar.get(Calendar.MINUTE);
            int yearBeforeChange = calendar.get(Calendar.YEAR);
            calendar.setTimeInMillis(CalendarUtil.getNextMonthTimeInMillis(calendar.getTimeInMillis()));
            assertEquals(calendar.get(Calendar.MONTH), expectedMonth);
            assertEquals(dayBeforeChange, calendar.get(Calendar.DAY_OF_MONTH));
            assertEquals(hourBeforeChange, calendar.get(Calendar.HOUR_OF_DAY));
            assertEquals(minuteBeforeChange, calendar.get(Calendar.MINUTE));
            assertEquals(yearBeforeChange, calendar.get(Calendar.YEAR));
        }
    }
}