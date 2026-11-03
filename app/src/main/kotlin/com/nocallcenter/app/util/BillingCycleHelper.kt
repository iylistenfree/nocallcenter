package com.nocallcenter.app.util

import java.time.LocalDate
import java.time.ZoneId

object BillingCycleHelper {

    /**
     * Calculates the start timestamp (in millis) of the current billing cycle
     * based on the user's monthly renewal day (1..31).
     */
    fun getCycleStartTimestamp(renewalDay: Int, zoneId: ZoneId = ZoneId.systemDefault()): Long {
        val today = LocalDate.now(zoneId)
        val validDay = renewalDay.coerceIn(1, 31)

        val cycleStartDate = if (today.dayOfMonth >= validDay) {
            // Started this month
            val maxDaysInThisMonth = today.lengthOfMonth()
            val day = validDay.coerceAtMost(maxDaysInThisMonth)
            today.withDayOfMonth(day)
        } else {
            // Started last month
            val prevMonth = today.minusMonths(1)
            val maxDaysInPrevMonth = prevMonth.lengthOfMonth()
            val day = validDay.coerceAtMost(maxDaysInPrevMonth)
            prevMonth.withDayOfMonth(day)
        }

        return cycleStartDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    /**
     * Calculates the end timestamp (in millis) of the current billing cycle.
     */
    fun getCycleEndTimestamp(renewalDay: Int, zoneId: ZoneId = ZoneId.systemDefault()): Long {
        val today = LocalDate.now(zoneId)
        val validDay = renewalDay.coerceIn(1, 31)

        val nextCycleStartDate = if (today.dayOfMonth >= validDay) {
            // Next renewal is next month
            val nextMonth = today.plusMonths(1)
            val maxDaysInNextMonth = nextMonth.lengthOfMonth()
            val day = validDay.coerceAtMost(maxDaysInNextMonth)
            nextMonth.withDayOfMonth(day)
        } else {
            // Next renewal is this month
            val maxDaysInThisMonth = today.lengthOfMonth()
            val day = validDay.coerceAtMost(maxDaysInThisMonth)
            today.withDayOfMonth(day)
        }

        return nextCycleStartDate.atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
    }
}
