package com.nocallcenter.app.ui.main

import com.nocallcenter.app.data.AppState
import com.nocallcenter.app.data.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MainScreenViewModelTest {

    @Test
    fun testInitialState() = runTest {
        val repo = FakeDataRepository()
        val viewModel = MainScreenViewModel(repo)
        val state = viewModel.appState.first()

        assertTrue(state.isScreeningEnabled)
        assertEquals(40, state.smsBudgetLimit)
    }

    @Test
    fun testUpdateBudget() = runTest {
        val repo = FakeDataRepository()
        val viewModel = MainScreenViewModel(repo)

        viewModel.setSmsBudgetLimit(50)
        assertEquals(50, repo.appState.value.smsBudgetLimit)
    }

    @Test
    fun testCompleteFirstLaunch() = runTest {
        val repo = FakeDataRepository()
        val viewModel = MainScreenViewModel(repo)

        viewModel.completeFirstLaunch()
        assertEquals(false, repo.appState.value.isFirstLaunch)
    }
}

private class FakeDataRepository : DataRepository {
    private val _state = MutableStateFlow(AppState(isFirstLaunch = true, smsBudgetLimit = 40))
    override val appState: StateFlow<AppState> = _state.asStateFlow()

    override fun reloadState() {}
    override fun completeFirstLaunch() { _state.value = _state.value.copy(isFirstLaunch = false) }
    override fun setScreeningEnabled(enabled: Boolean) { _state.value = _state.value.copy(isScreeningEnabled = enabled) }
    override fun setBlockNonContacts(enabled: Boolean) { _state.value = _state.value.copy(blockNonContacts = enabled) }
    override fun setAutoSmsEnabled(enabled: Boolean) { _state.value = _state.value.copy(isAutoSmsEnabled = enabled) }
    override fun setBudgetingEnabled(enabled: Boolean) { _state.value = _state.value.copy(isBudgetingEnabled = enabled) }
    override fun setSmsBudgetLimit(limit: Int) { _state.value = _state.value.copy(smsBudgetLimit = limit) }
    override fun setBillingCycleDay(day: Int) { _state.value = _state.value.copy(billingCycleDay = day) }
    override fun setAntiFloodEnabled(enabled: Boolean) { _state.value = _state.value.copy(isAntiFloodEnabled = enabled) }
    override fun setAutoCheckUpdatesEnabled(enabled: Boolean) { _state.value = _state.value.copy(isAutoCheckUpdatesEnabled = enabled) }
    override fun setSmsTemplate(template: String) { _state.value = _state.value.copy(smsTemplate = template) }
    override fun resetSmsTemplate() { _state.value = _state.value.copy(smsTemplate = "") }
    override fun clearCallHistory() { _state.value = _state.value.copy(screenedCalls = emptyList()) }
    override fun refreshScreeningRoleStatus() {}
}
