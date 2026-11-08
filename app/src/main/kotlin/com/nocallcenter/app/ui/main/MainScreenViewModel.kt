package com.nocallcenter.app.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nocallcenter.app.data.AppState
import com.nocallcenter.app.data.DataRepository
import com.nocallcenter.app.util.UpdateCheckResult
import com.nocallcenter.app.util.UpdateChecker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainScreenViewModel(private val repository: DataRepository) : ViewModel() {

    val appState: StateFlow<AppState> = repository.appState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AppState()
        )

    var updateCheckResult by mutableStateOf<UpdateCheckResult?>(null)
        private set

    var isCheckingUpdate by mutableStateOf(false)
        private set

    var showUpdateDialog by mutableStateOf(false)
        private set

    fun checkForUpdates(currentVersion: String, isManual: Boolean = false) {
        if (!isManual && !appState.value.isAutoCheckUpdatesEnabled) return

        viewModelScope.launch {
            isCheckingUpdate = true
            val result = UpdateChecker.checkForUpdate(currentVersion)
            isCheckingUpdate = false
            updateCheckResult = result

            if (result is UpdateCheckResult.Available) {
                showUpdateDialog = true
            }
        }
    }

    fun dismissUpdateDialog() {
        showUpdateDialog = false
    }

    fun clearUpdateResultFeedback() {
        updateCheckResult = null
    }

    fun reload() = repository.reloadState()

    fun completeFirstLaunch() = repository.completeFirstLaunch()

    fun setScreeningEnabled(enabled: Boolean) = repository.setScreeningEnabled(enabled)

    fun setBlockNonContacts(enabled: Boolean) = repository.setBlockNonContacts(enabled)

    fun setBlockExtraEuCalls(enabled: Boolean) = repository.setBlockExtraEuCalls(enabled)

    fun setBlockEuCalls(enabled: Boolean) = repository.setBlockEuCalls(enabled)

    fun setExcludedEuPrefixes(prefixes: Set<String>) = repository.setExcludedEuPrefixes(prefixes)

    fun toggleExcludedEuPrefix(prefix: String) = repository.toggleExcludedEuPrefix(prefix)

    fun setAutoSmsEnabled(enabled: Boolean) = repository.setAutoSmsEnabled(enabled)

    fun setBudgetingEnabled(enabled: Boolean) = repository.setBudgetingEnabled(enabled)

    fun setSmsBudgetLimit(limit: Int) = repository.setSmsBudgetLimit(limit)

    fun setBillingCycleDay(day: Int) = repository.setBillingCycleDay(day)

    fun setAntiFloodEnabled(enabled: Boolean) = repository.setAntiFloodEnabled(enabled)

    fun setAutoCheckUpdatesEnabled(enabled: Boolean) = repository.setAutoCheckUpdatesEnabled(enabled)

    fun setSmsTemplate(template: String) = repository.setSmsTemplate(template)

    fun resetSmsTemplate() = repository.resetSmsTemplate()

    fun clearCallHistory() = repository.clearCallHistory()

    fun refreshScreeningRoleStatus() = repository.refreshScreeningRoleStatus()
}
