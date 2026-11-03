package com.nocallcenter.app.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.nocallcenter.app.R

class AppPreferences(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_IS_FIRST_LAUNCH, true)
        set(value) = prefs.edit().putBoolean(KEY_IS_FIRST_LAUNCH, value).apply()

    var isScreeningEnabled: Boolean
        get() = prefs.getBoolean(KEY_SCREENING_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SCREENING_ENABLED, value).apply()

    var blockNonContacts: Boolean
        get() = prefs.getBoolean(KEY_BLOCK_NON_CONTACTS, true)
        set(value) = prefs.edit().putBoolean(KEY_BLOCK_NON_CONTACTS, value).apply()

    var isAutoSmsEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_SMS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_SMS_ENABLED, value).apply()

    // Configurable SMS Budgeting: can be turned ON/OFF (for limited vs unlimited SMS plans)
    var isBudgetingEnabled: Boolean
        get() = prefs.getBoolean(KEY_BUDGETING_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_BUDGETING_ENABLED, value).apply()

    // Configurable number of SMS per month (default 40)
    var smsBudgetLimit: Int
        get() = prefs.getInt(KEY_SMS_BUDGET_LIMIT, 40)
        set(value) = prefs.edit().putInt(KEY_SMS_BUDGET_LIMIT, value.coerceAtLeast(1)).apply()

    // Renewal day of the monthly plan (1..31)
    var billingCycleDay: Int
        get() = prefs.getInt(KEY_BILLING_CYCLE_DAY, 1)
        set(value) = prefs.edit().putInt(KEY_BILLING_CYCLE_DAY, value.coerceIn(1, 31)).apply()

    var isAntiFloodEnabled: Boolean
        get() = prefs.getBoolean(KEY_ANTI_FLOOD_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ANTI_FLOOD_ENABLED, value).apply()

    var antiFloodHours: Int
        get() = prefs.getInt(KEY_ANTI_FLOOD_HOURS, 48)
        set(value) = prefs.edit().putInt(KEY_ANTI_FLOOD_HOURS, value.coerceAtLeast(1)).apply()

    var customSmsTemplate: String
        get() {
            val saved = prefs.getString(KEY_SMS_TEMPLATE, null)
            return if (!saved.isNullOrBlank()) saved else context.getString(R.string.default_sms_template)
        }
        set(value) = prefs.edit().putString(KEY_SMS_TEMPLATE, value.trim()).apply()

    fun resetSmsTemplateToDefault() {
        prefs.edit().remove(KEY_SMS_TEMPLATE).apply()
    }

    val paypalUrl: String = PAYPAL_URL

    companion object {
        const val PAYPAL_USERNAME = "gabrilc"
        const val PAYPAL_URL = "https://paypal.me/$PAYPAL_USERNAME"
        private const val PREFS_NAME = "nocallcenter_prefs"
        private const val KEY_IS_FIRST_LAUNCH = "pref_is_first_launch"
        private const val KEY_SCREENING_ENABLED = "pref_screening_enabled"
        private const val KEY_BLOCK_NON_CONTACTS = "pref_block_non_contacts"
        private const val KEY_AUTO_SMS_ENABLED = "pref_auto_sms_enabled"
        private const val KEY_BUDGETING_ENABLED = "pref_budgeting_enabled"
        private const val KEY_SMS_BUDGET_LIMIT = "pref_sms_budget_limit"
        private const val KEY_BILLING_CYCLE_DAY = "pref_billing_cycle_day"
        private const val KEY_ANTI_FLOOD_ENABLED = "pref_anti_flood_enabled"
        private const val KEY_ANTI_FLOOD_HOURS = "pref_anti_flood_hours"
        private const val KEY_SMS_TEMPLATE = "pref_sms_template"
        private const val KEY_PAYPAL_USERNAME = "pref_paypal_username"
    }
}
