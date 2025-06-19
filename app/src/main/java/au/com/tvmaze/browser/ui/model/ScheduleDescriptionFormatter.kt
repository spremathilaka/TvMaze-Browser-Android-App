package au.com.tvmaze.browser.ui.model

import android.util.Log
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeParseException

class ScheduleDescriptionFormatter(val days: List<String>?,
                                   val time: String?) {

    private val TAG = "ScheduleDescriptionFormatter"

    fun buildScheduleDescription(

    ): String {
        val prefix = getPrefix()
        val dayDescriptor = getDayDescriptor()
        val timeDescriptor = getTimeDescriptor()

        return listOf(prefix, dayDescriptor, timeDescriptor)
            .filter { it.isNotEmpty() }
            .joinToString(" ")
    }


    fun getPrefix(): String {
        if (time.isNullOrEmpty()) return ""

        return if (isTimeBetween(time, "00:00", "05:59")) {
            "Early"
        } else {
            ""
        }
    }

    fun getDayDescriptor(): String {
        if (days.isNullOrEmpty()) return ""

        val weekdays = setOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
        val weekendDays = setOf("Saturday", "Sunday")
        val allDays = weekdays + weekendDays

        val daysSet = days.toSet()

        return when {
            // All seven days case
            daysSet.containsAll(allDays) -> ""

            // Weekday case: contains all weekdays and no weekend days
            daysSet.containsAll(weekdays) && daysSet.intersect(weekendDays).isEmpty() -> "Weekday"

            // Weekend case: contains both weekend days and no weekdays
            daysSet.containsAll(weekendDays) && daysSet.intersect(weekdays).isEmpty() -> "Weekend"

            // Default case: just list the days
            else -> days.joinToString(", ")
        }
    }

    fun getTimeDescriptor(): String {
        if (time.isNullOrEmpty()) return ""

        return when {
            isTimeBetween(time, "00:00", "11:59") -> "Mornings"
            isTimeBetween(time, "12:00", "17:59") -> "Afternoons"
            isTimeBetween(time, "18:00", "23:59") -> "Nights"
            else -> ""
        }
    }

    private  fun isTimeBetween(checkTimeStr: String, startTimeStr: String, endTimeStr: String): Boolean {
        return try {
            val checkTime = LocalTime.parse(checkTimeStr)
            val startTime = LocalTime.parse(startTimeStr)
            val endTime = LocalTime.parse(endTimeStr)

            if (startTime <= endTime) {
                checkTime >= startTime && checkTime <= endTime
            } else {
                checkTime >= startTime || checkTime <= endTime
            }
        } catch (e: DateTimeParseException) {
            Log.e(TAG,"Error: Invalid time format for one or more inputs. Please use HH:MM. Error: ${e.message}")
            false
        }
    }
}