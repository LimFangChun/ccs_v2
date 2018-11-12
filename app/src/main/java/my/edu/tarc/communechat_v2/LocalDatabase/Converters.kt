package my.edu.tarc.communechat_v2.LocalDatabase

import android.arch.persistence.room.TypeConverter
import com.google.android.gms.common.util.NumberUtils
import java.util.*

object Converters {
    @TypeConverter
    fun calendarFromTimestamp(value: String?): Calendar? {
        if (value == null) {
            return null
        }
        val cal = GregorianCalendar()
        cal.timeInMillis = NumberUtils.parseHexLong(value) * 1000
        return cal
    }

    @TypeConverter
    fun dateToTimestamp(cal: Calendar?): String? {
        return if (cal == null) {
            null
        } else "" + cal.timeInMillis / 1000
    }
}
