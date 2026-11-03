package com.nocallcenter.app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nocallcenter.app.data.AppState
import com.nocallcenter.app.data.DataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainScreenViewModel(private val repository: DataRepository) : ViewModel() {

    val appState: StateFlow<AppState> = repository.appState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AppState()
        )

    fun reload() = repository.reloadState()

    fun completeFirstLaunch() = repository.completeFirstLaunch()

    fun setScreeningEnabled(enabled: Boolean) = repository.setScreeningEnabled(enabled)

    fun setBlockNonContacts(enabled: Boolean) = repository.setBlockNonContacts(enabled)

    fun setAutoSmsEnabled(enabled: Boolean) = repository.setAutoSmsEnabled(enabled)

    fun setBudgetingEnabled(enabled: Boolean) = repository.setBudgetingEnabled(enabled)

    fun setSmsBudgetLimit(limit: Int) = repository.setSmsBudgetLimit(limit)

    fun setBillingCycleDay(day: Int) = repository.setBillingCycleDay(day)

    fun setAntiFloodEnabled(enabled: Boolean) = repository.setAntiFloodEnabled(enabled)

    fun setSmsTemplate(template: String) = repository.setSmsTemplate(template)

    fun resetSmsTemplate() = repository.resetSmsTemplate()

    fun clearCallHistory() = repository.clearCallHistory()

    fun refreshScreeningRoleStatus() = repository.refreshScreeningRoleStatus()
}
