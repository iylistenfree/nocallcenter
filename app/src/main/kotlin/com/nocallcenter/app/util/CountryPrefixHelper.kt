package com.nocallcenter.app.util

import com.nocallcenter.app.R

data class EuCountry(
    val isoCode: String,
    val dialCode: String,
    val nameResId: Int,
    val flagEmoji: String
)

object CountryPrefixHelper {

    val EU_COUNTRIES: List<EuCountry> = listOf(
        EuCountry("AT", "+43", R.string.country_austria, "🇦🇹"),
        EuCountry("BE", "+32", R.string.country_belgium, "🇧🇪"),
        EuCountry("BG", "+359", R.string.country_bulgaria, "🇧🇬"),
        EuCountry("CY", "+357", R.string.country_cyprus, "🇨🇾"),
        EuCountry("CZ", "+420", R.string.country_czechia, "🇨🇿"),
        EuCountry("DE", "+49", R.string.country_germany, "🇩🇪"),
        EuCountry("DK", "+45", R.string.country_denmark, "🇩🇰"),
        EuCountry("EE", "+372", R.string.country_estonia, "🇪🇪"),
        EuCountry("ES", "+34", R.string.country_spain, "🇪🇸"),
        EuCountry("FI", "+358", R.string.country_finland, "🇫🇮"),
        EuCountry("FR", "+33", R.string.country_france, "🇫🇷"),
        EuCountry("GR", "+30", R.string.country_greece, "🇬🇷"),
        EuCountry("HR", "+385", R.string.country_croatia, "🇭🇷"),
        EuCountry("HU", "+36", R.string.country_hungary, "🇭🇺"),
        EuCountry("IE", "+353", R.string.country_ireland, "🇮🇪"),
        EuCountry("LT", "+370", R.string.country_lithuania, "🇱🇹"),
        EuCountry("LU", "+352", R.string.country_luxembourg, "🇱🇺"),
        EuCountry("LV", "+371", R.string.country_latvia, "🇱🇻"),
        EuCountry("MT", "+356", R.string.country_malta, "🇲🇹"),
        EuCountry("NL", "+31", R.string.country_netherlands, "🇳🇱"),
        EuCountry("PL", "+48", R.string.country_poland, "🇵🇱"),
        EuCountry("PT", "+351", R.string.country_portugal, "🇵🇹"),
        EuCountry("RO", "+40", R.string.country_romania, "🇷🇴"),
        EuCountry("SE", "+46", R.string.country_sweden, "🇸🇪"),
        EuCountry("SI", "+386", R.string.country_slovenia, "🇸🇮"),
        EuCountry("SK", "+421", R.string.country_slovakia, "🇸🇰")
    ).sortedByDescending { it.dialCode.length }

    /**
     * Normalizes a phone number by stripping spaces, dashes, dots, parentheses,
     * handling 'tel:' prefixes, and converting leading '00' to '+'.
     */
    fun normalizeNumber(rawNumber: String): String {
        var cleaned = rawNumber.trim().replace(Regex("[\\s\\-\\(\\)\\.]"), "")
        if (cleaned.startsWith("tel:", ignoreCase = true)) {
            cleaned = cleaned.substring(4)
        }
        if (cleaned.startsWith("00")) {
            cleaned = "+" + cleaned.substring(2)
        }
        return cleaned
    }

    /**
     * Checks if the number is domestic (Italian).
     * In Italy, numbers without '+' are domestic numbers.
     * Numbers with '+39' (or '0039') are domestic Italian numbers.
     */
    fun isDomesticItalian(normalizedNumber: String): Boolean {
        if (!normalizedNumber.startsWith("+")) return true
        if (normalizedNumber.startsWith("+39")) return true
        return false
    }

    /**
     * Returns true if the phone number has a foreign (non-Italian) country code.
     */
    fun isForeignNumber(normalizedNumber: String): Boolean {
        return !isDomesticItalian(normalizedNumber)
    }

    /**
     * Returns the matching [EuCountry] if the normalized number begins with an EU dial code
     * (excluding Italy +39). Returns null otherwise.
     */
    fun getEuCountry(normalizedNumber: String): EuCountry? {
        if (!normalizedNumber.startsWith("+")) return null
        return EU_COUNTRIES.firstOrNull { normalizedNumber.startsWith(it.dialCode) }
    }

    /**
     * Returns true if the normalized number is foreign and does not belong to any EU country.
     */
    fun isExtraEuNumber(normalizedNumber: String): Boolean {
        if (!isForeignNumber(normalizedNumber)) return false
        return getEuCountry(normalizedNumber) == null
    }

    private val TWO_DIGIT_CODES = setOf(
        "+20", "+27",
        "+30", "+31", "+32", "+33", "+34", "+36", "+39",
        "+40", "+41", "+43", "+44", "+45", "+46", "+47", "+48", "+49",
        "+51", "+52", "+53", "+54", "+55", "+56", "+57", "+58",
        "+60", "+61", "+62", "+63", "+64", "+65", "+66",
        "+81", "+82", "+84", "+86",
        "+90", "+91", "+92", "+93", "+94", "+95", "+98"
    )

    /**
     * Extracts an approximate dial code (e.g. +44, +355, +1) from a normalized foreign number.
     */
    fun extractDialCode(normalizedNumber: String): String {
        if (!normalizedNumber.startsWith("+")) return ""
        val eu = getEuCountry(normalizedNumber)
        if (eu != null) return eu.dialCode

        if (normalizedNumber.startsWith("+1")) return "+1"
        if (normalizedNumber.startsWith("+7")) return "+7"

        val twoDigit = TWO_DIGIT_CODES.firstOrNull { normalizedNumber.startsWith(it) }
        if (twoDigit != null) return twoDigit

        return if (normalizedNumber.length >= 4) {
            normalizedNumber.substring(0, 4)
        } else {
            normalizedNumber
        }
    }
}
