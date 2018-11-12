package my.edu.tarc.communechat_v2.LocalDatabase;

import android.arch.persistence.room.TypeConverter;

import com.google.android.gms.common.util.NumberUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Converters {
    @TypeConverter
    public static Calendar calendarFromTimestamp(String value) {
        if (value == null) {
            return null;
        }
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(NumberUtils.parseHexLong(value) * 1000);
        return cal;
    }

    @TypeConverter
    public static String dateToTimestamp(Calendar cal) {
        if (cal == null) {
            return null;
        }
        return "" + cal.getTimeInMillis() / 1000;
    }
}
