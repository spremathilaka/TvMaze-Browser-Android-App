package au.com.tvmaze.browser.ui.model

import org.junit.Assert
import org.junit.Test

class ScheduleDescriptionFormatterTest {

    @Test
    fun `getPrefix should return Early for times between 00_00 and 05_59`() {
        val testCases = listOf(
            "00:00",
            "00:01",
            "03:30",
            "05:58",
            "05:59"
        )

        testCases.forEach { time ->
            val formatter = ScheduleDescriptionFormatter(emptyList(), time)
            Assert.assertEquals("For time $time", "Early", formatter.getPrefix())
        }
    }

    @Test
    fun `getPrefix should return empty string for times from 06_00 onwards`() {
        val testCases = listOf(
            "06:00",
            "12:00",
            "18:30",
            "23:59"
        )

        testCases.forEach { time ->
            val formatter = ScheduleDescriptionFormatter(emptyList(), time)
            Assert.assertEquals("For time $time", "", formatter.getPrefix())
        }
    }

    @Test
    fun `getPrefix should return empty string for null or empty time`() {
        val formatter = ScheduleDescriptionFormatter(emptyList(), null)
        Assert.assertEquals("For null time", "", formatter.getPrefix())
        Assert.assertEquals("For empty time", "", formatter.getPrefix())
    }

    @Test
    fun `getDayDescriptor should return Weekday when all weekdays present and no weekend days`() {
        val weekdays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
        val formatter = ScheduleDescriptionFormatter(weekdays, null)
        Assert.assertEquals("Weekday", formatter.getDayDescriptor())
    }

    @Test
    fun `getDayDescriptor should return Weekend when both weekend days present and no weekdays`() {
        val weekendDays = listOf("Saturday", "Sunday")
        val formatter = ScheduleDescriptionFormatter(weekendDays, null)
        Assert.assertEquals("Weekend", formatter.getDayDescriptor())
    }

    @Test
    fun `getDayDescriptor should return empty string when all seven days are present`() {
        val allDays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val formatter = ScheduleDescriptionFormatter(allDays, null)
        Assert.assertEquals("", formatter.getDayDescriptor())
    }

    @Test
    fun `getDayDescriptor should return comma separated list for other day combinations`() {
        val mixedDays = listOf("Thursday", "Saturday", "Sunday", "Monday")
        val formatter = ScheduleDescriptionFormatter(mixedDays, null)
        Assert.assertEquals("Thursday, Saturday, Sunday, Monday", formatter.getDayDescriptor())

        val someDays = listOf("Monday", "Wednesday", "Friday")
        val formatter2 = ScheduleDescriptionFormatter(someDays, null)
        Assert.assertEquals("Monday, Wednesday, Friday", formatter2.getDayDescriptor())
    }

    @Test
    fun `getDayDescriptor should return empty string for empty list`() {
        val formatter = ScheduleDescriptionFormatter(emptyList(), null)
        Assert.assertEquals("", formatter.getDayDescriptor())
    }

    @Test
    fun `getTimeDescriptor should return Mornings for times between 00_00 and 11_59`() {
        val morningTimes = listOf(
            "00:00",
            "00:01",
            "06:30",
            "11:58",
            "11:59"
        )

        morningTimes.forEach { time ->
            val formatter = ScheduleDescriptionFormatter(emptyList(), time)
            Assert.assertEquals("For time $time", "Mornings", formatter.getTimeDescriptor())
        }
    }

    @Test
    fun `getTimeDescriptor should return Afternoons for times between 12_00 and 17_59`() {
        val afternoonTimes = listOf(
            "12:00",
            "12:01",
            "15:30",
            "17:58",
            "17:59"
        )

        afternoonTimes.forEach { time ->
            val formatter = ScheduleDescriptionFormatter(emptyList(), time)
            Assert.assertEquals("For time $time", "Afternoons", formatter.getTimeDescriptor())
        }
    }

    @Test
    fun `getTimeDescriptor should return Nights for times between 18_00 and 23_59`() {
        val nightTimes = listOf(
            "18:00",
            "18:01",
            "20:30",
            "23:58",
            "23:59"
        )

        nightTimes.forEach { time ->
            val formatter = ScheduleDescriptionFormatter(emptyList(), time)
            Assert.assertEquals("For time $time", "Nights", formatter.getTimeDescriptor())
        }
    }

    @Test
    fun `getTimeDescriptor should return empty string for null or empty time`() {
        val formatter = ScheduleDescriptionFormatter(emptyList(), null)
        Assert.assertEquals("For null time", "", formatter.getTimeDescriptor())
        Assert.assertEquals("For empty time", "", formatter.getTimeDescriptor())
    }

    @Test
    fun `buildScheduleDescription should format schedule descriptions correctly`() {
        val testCases = listOf(
            Triple(listOf("Sunday"), "00:00", "Early Sunday Mornings"),
            Triple(listOf("Sunday"), "06:00", "Sunday Mornings"),
            Triple(listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"), "23:35", "Weekday Nights"),
            Triple(listOf("Saturday", "Sunday"), "23:35", "Weekend Nights"),
            Triple(listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"), "23:35", "Nights"),
            Triple(listOf("Monday"), "21:00", "Monday Nights"),
            Triple(listOf("Monday", "Tuesday"), "21:00", "Monday, Tuesday Nights"),
            Triple(listOf("Monday"), "07:00", "Monday Mornings"),
            Triple(listOf("Monday"), "13:00", "Monday Afternoons"),
            Triple(listOf("Monday"), "", "Monday")
        )

        testCases.forEach { (days, time, expected) ->
            val formatter = ScheduleDescriptionFormatter(days, time)
            Assert.assertEquals(
                "Schedule for days=[${days.joinToString()}], time='$time'",
                expected,
                formatter.buildScheduleDescription()
            )
        }
    }

    @Test
    fun `buildScheduleDescription should handle null values`() {
        val formatter = ScheduleDescriptionFormatter(null, null)
        Assert.assertEquals("", formatter.buildScheduleDescription())
    }
}