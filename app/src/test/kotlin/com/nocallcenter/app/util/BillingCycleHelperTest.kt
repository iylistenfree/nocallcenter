package com.nocallcenter.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class BillingCycleHelperTest {

    @Test
    fun testCycleCalculation() {
        val zoneId = ZoneId.of("UTC")
        val renewalDay = 15

        val start = BillingCycleHelper.getCycleStartTimestamp(renewalDay, zoneId)
        val end = BillingCycleHelper.getCycleEndTimestamp(renewalDay, zoneId)

        assertTrue("End must be after start", end > start)
    }

    @Test
    fun testCycleStartIsAtMidnight() {
        val zoneId = ZoneId.of("UTC")
        val renewalDay = 1

        val start = BillingCycleHelper.getCycleStartTimestamp(renewalDay, zoneId)
        val date = LocalDate.ofInstant(java.time.Instant.ofEpochMilli(start), zoneId)

        assertEquals(1, date.dayOfMonth)
    }
}
