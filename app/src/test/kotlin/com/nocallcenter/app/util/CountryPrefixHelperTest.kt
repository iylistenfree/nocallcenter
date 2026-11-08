package com.nocallcenter.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CountryPrefixHelperTest {

    @Test
    fun testEuCountriesListExcludesItaly() {
        assertEquals(26, CountryPrefixHelper.EU_COUNTRIES.size)
        assertFalse(CountryPrefixHelper.EU_COUNTRIES.any { it.dialCode == "+39" })
        assertFalse(CountryPrefixHelper.EU_COUNTRIES.any { it.isoCode == "IT" })
    }

    @Test
    fun testNormalizeNumber() {
        assertEquals("+393401234567", CountryPrefixHelper.normalizeNumber("+39 340 123 4567"))
        assertEquals("+393401234567", CountryPrefixHelper.normalizeNumber("0039 340-123-4567"))
        assertEquals("+447911123456", CountryPrefixHelper.normalizeNumber("tel:0044 7911 123456"))
        assertEquals("0612345678", CountryPrefixHelper.normalizeNumber("06 (1234) 5678"))
    }

    @Test
    fun testDomesticItalianNumbers() {
        val numbers = listOf(
            "+393401234567",
            "00393401234567",
            "3401234567",
            "0612345678",
            "0212345678",
            "800123456"
        )

        for (raw in numbers) {
            val norm = CountryPrefixHelper.normalizeNumber(raw)
            assertTrue("Expected $raw to be domestic Italian", CountryPrefixHelper.isDomesticItalian(norm))
            assertFalse("Expected $raw NOT to be foreign", CountryPrefixHelper.isForeignNumber(norm))
            assertFalse("Expected $raw NOT to be extra-EU", CountryPrefixHelper.isExtraEuNumber(norm))
            assertNull("Expected $raw to have null EU country", CountryPrefixHelper.getEuCountry(norm))
        }
    }

    @Test
    fun testEuNumbers() {
        val euCases = mapOf(
            "+33 6 12 34 56 78" to "+33",  // France
            "0049 151 1234567" to "+49",   // Germany
            "+34 612 345 678" to "+34",    // Spain
            "+31 6 12345678" to "+31",     // Netherlands
            "+48 12 345 67 89" to "+48",   // Poland
            "+359 88 123 4567" to "+359",  // Bulgaria (3 digits)
            "+420 123 456 789" to "+420",  // Czechia (3 digits)
            "+385 91 123 4567" to "+385"   // Croatia (3 digits)
        )

        for ((raw, expectedDial) in euCases) {
            val norm = CountryPrefixHelper.normalizeNumber(raw)
            assertFalse("Expected $raw NOT to be domestic", CountryPrefixHelper.isDomesticItalian(norm))
            assertTrue("Expected $raw to be foreign", CountryPrefixHelper.isForeignNumber(norm))
            assertFalse("Expected $raw NOT to be extra-EU", CountryPrefixHelper.isExtraEuNumber(norm))

            val eu = CountryPrefixHelper.getEuCountry(norm)
            assertNotNull("Expected EU country for $raw", eu)
            assertEquals(expectedDial, eu?.dialCode)
        }
    }

    @Test
    fun testExtraEuNumbers() {
        val extraEuCases = listOf(
            "+44 7911 123456" to "+44",     // UK
            "0044 7911 123456" to "+44",    // UK with 00
            "+355 69 123 4567" to "+355",   // Albania
            "+216 20 123 456" to "+216",    // Tunisia
            "+1 555 123 4567" to "+1",      // USA
            "+41 79 123 45 67" to "+41",    // Switzerland
            "+380 50 123 4567" to "+380",   // Ukraine
            "+212 6 12 34 56 78" to "+212"  // Morocco
        )

        for ((raw, expectedPrefix) in extraEuCases) {
            val norm = CountryPrefixHelper.normalizeNumber(raw)
            assertFalse("Expected $raw NOT to be domestic", CountryPrefixHelper.isDomesticItalian(norm))
            assertTrue("Expected $raw to be foreign", CountryPrefixHelper.isForeignNumber(norm))
            assertTrue("Expected $raw to be extra-EU", CountryPrefixHelper.isExtraEuNumber(norm))
            assertNull("Expected null EU country for $raw", CountryPrefixHelper.getEuCountry(norm))
            assertEquals(expectedPrefix, CountryPrefixHelper.extractDialCode(norm))
        }
    }
}
