package com.nocallcenter.app.data

import android.app.role.RoleManager
import android.content.Context
import android.os.Build
import com.nocallcenter.app.data.db.CallLogDatabaseHelper
import com.nocallcenter.app.data.model.ScreenedCall
import com.nocallcenter.app.data.preferences.AppPreferences
import com.nocallcenter.app.util.BillingCycleHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppState(
    val isFirstLaunch: Boolean = false,
    val isScreeningEnabled: Boolean = true,
    val blockNonContacts: Boolean = true,
    val isAutoSmsEnabled: Boolean = true,
    val isBudgetingEnabled: Boolean = true,
    val smsBudgetLimit: Int = 40,
    val billingCycleDay: Int = 1,
    val isAntiFloodEnabled: Boolean = true,
    val isAutoCheckUpdatesEnabled: Boolean = true,
    val smsTemplate: String = "",
    val paypalUrl: String = AppPreferences.PAYPAL_URL,
    val screenedCalls: List<ScreenedCall> = emptyList(),
    val totalBlockedCalls: Int = 0,
    val smsSentThisCycle: Int = 0,
    val isScreeningRoleHeld: Boolean = false
)

interface DataRepository {
    val appState: StateFlow<AppState>
    fun reloadState()
    fun completeFirstLaunch()
    fun setScreeningEnabled(enabled: Boolean)
    fun setBlockNonContacts(enabled: Boolean)
    fun setAutoSmsEnabled(enabled: Boolean)
    fun setBudgetingEnabled(enabled: Boolean)
    fun setSmsBudgetLimit(limit: Int)
    fun setBillingCycleDay(day: Int)
    fun setAntiFloodEnabled(enabled: Boolean)
    fun setAutoCheckUpdatesEnabled(enabled: Boolean)
    fun setSmsTemplate(template: String)
    fun resetSmsTemplate()
    fun clearCallHistory()
    fun refreshScreeningRoleStatus()
}

class DefaultDataRepository(private val context: Context) : DataRepository {

    private val prefs = AppPreferences(context)
    private val db = CallLogDatabaseHelper(context)

    private val _appState = MutableStateFlow(computeCurrentState())
    override val appState: StateFlow<AppState> = _appState.asStateFlow()

    private fun checkScreeningRole(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(RoleManager::class.java)
            roleManager?.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) ?: false
        } else {
            true
        }
    }

    private fun computeCurrentState(): AppState {
        val cycleStart = BillingCycleHelper.getCycleStartTimestamp(prefs.billingCycleDay)
        val cycleEnd = BillingCycleHelper.getCycleEndTimestamp(prefs.billingCycleDay)

        return AppState(
            isFirstLaunch = prefs.isFirstLaunch,
            isScreeningEnabled = prefs.isScreeningEnabled,
            blockNonContacts = prefs.blockNonContacts,
            isAutoSmsEnabled = prefs.isAutoSmsEnabled,
            isBudgetingEnabled = prefs.isBudgetingEnabled,
            smsBudgetLimit = prefs.smsBudgetLimit,
            billingCycleDay = prefs.billingCycleDay,
            isAntiFloodEnabled = prefs.isAntiFloodEnabled,
            isAutoCheckUpdatesEnabled = prefs.isAutoCheckUpdatesEnabled,
            smsTemplate = prefs.customSmsTemplate,
            paypalUrl = AppPreferences.PAYPAL_URL,
            screenedCalls = db.getAllCalls(limit = 100),
            totalBlockedCalls = db.getBlockedCallsCount(),
            smsSentThisCycle = db.getSmsSentCountInCycle(cycleStart, cycleEnd),
            isScreeningRoleHeld = checkScreeningRole()
        )
    }

    override fun reloadState() {
        _appState.value = computeCurrentState()
    }

    override fun completeFirstLaunch() {
        prefs.isFirstLaunch = false
        reloadState()
    }

    override fun setScreeningEnabled(enabled: Boolean) {
        prefs.isScreeningEnabled = enabled
        reloadState()
    }

    override fun setBlockNonContacts(enabled: Boolean) {
        prefs.blockNonContacts = enabled
        reloadState()
    }

    override fun setAutoSmsEnabled(enabled: Boolean) {
        prefs.isAutoSmsEnabled = enabled
        reloadState()
    }

    override fun setBudgetingEnabled(enabled: Boolean) {
        prefs.isBudgetingEnabled = enabled
        reloadState()
    }

    override fun setSmsBudgetLimit(limit: Int) {
        prefs.smsBudgetLimit = limit
        reloadState()
    }

    override fun setBillingCycleDay(day: Int) {
        prefs.billingCycleDay = day
        reloadState()
    }

    override fun setAntiFloodEnabled(enabled: Boolean) {
        prefs.isAntiFloodEnabled = enabled
        reloadState()
    }

    override fun setAutoCheckUpdatesEnabled(enabled: Boolean) {
        prefs.isAutoCheckUpdatesEnabled = enabled
        reloadState()
    }

    override fun setSmsTemplate(template: String) {
        prefs.customSmsTemplate = template
        reloadState()
    }

    override fun resetSmsTemplate() {
        prefs.resetSmsTemplateToDefault()
        reloadState()
    }

    override fun clearCallHistory() {
        db.clearHistory()
        reloadState()
    }

    override fun refreshScreeningRoleStatus() {
        _appState.value = _appState.value.copy(
            isScreeningRoleHeld = checkScreeningRole()
        )
    }
}
