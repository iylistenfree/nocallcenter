package com.nocallcenter.app.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateCheckerTest {

    @Test
    fun testIsNewerVersion() {
        assertTrue(UpdateChecker.isNewerVersion("1.0.0", "1.0.1"))
        assertTrue(UpdateChecker.isNewerVersion("1.0.0", "v1.0.1"))
        assertTrue(UpdateChecker.isNewerVersion("1.0", "1.1"))
        assertTrue(UpdateChecker.isNewerVersion("1.0.1", "1.1.0"))
        assertTrue(UpdateChecker.isNewerVersion("1.0.1", "2.0.0"))

        assertFalse(UpdateChecker.isNewerVersion("1.0.1", "1.0.1"))
        assertFalse(UpdateChecker.isNewerVersion("1.0.1", "v1.0.1"))
        assertFalse(UpdateChecker.isNewerVersion("1.1.0", "1.0.1"))
        assertFalse(UpdateChecker.isNewerVersion("2.0.0", "1.9.9"))
    }
}
