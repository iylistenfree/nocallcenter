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

    @Test
    fun testForeignPrefixSettings() = runTest {
        val repo = FakeDataRepository()
        val viewModel = MainScreenViewModel(repo)

        assertEquals(false, repo.appState.value.blockExtraEuCalls)
        assertEquals(false, repo.appState.value.blockEuCalls)
        assertTrue(repo.appState.value.excludedEuPrefixes.isEmpty())

        viewModel.setBlockExtraEuCalls(true)
        assertEquals(true, repo.appState.value.blockExtraEuCalls)

        viewModel.setBlockEuCalls(true)
        assertEquals(true, repo.appState.value.blockEuCalls)

        viewModel.toggleExcludedEuPrefix("+33")
        assertTrue(repo.appState.value.excludedEuPrefixes.contains("+33"))

        viewModel.toggleExcludedEuPrefix("+33")
        assertTrue(!repo.appState.value.excludedEuPrefixes.contains("+33"))

        viewModel.setExcludedEuPrefixes(setOf("+49", "+34"))
        assertEquals(setOf("+49", "+34"), repo.appState.value.excludedEuPrefixes)
    }

    @Test
    fun testClearCallHistory() = runTest {
        val repo = FakeDataRepository()
        val viewModel = MainScreenViewModel(repo)

        viewModel.clearCallHistory()
        assertEquals(emptyList<com.nocallcenter.app.data.model.ScreenedCall>(), repo.appState.value.screenedCalls)
    }

    @Test
    fun testDialableNumberRecognition() {
        val dialableCall = com.nocallcenter.app.data.model.ScreenedCall(
            phoneNumber = "+39 340 1234567",
            outcome = com.nocallcenter.app.data.model.CallScreeningOutcome.BLOCKED_SMS_SENT
        )
        val anonymousCall = com.nocallcenter.app.data.model.ScreenedCall(
            phoneNumber = "Sconosciuto",
            outcome = com.nocallcenter.app.data.model.CallScreeningOutcome.BLOCKED_SMS_DISABLED
        )

        assertTrue(dialableCall.phoneNumber.any { it.isDigit() })
        assertTrue(!anonymousCall.phoneNumber.any { it.isDigit() })
    }
}

private class FakeDataRepository : DataRepository {
    private val _state = MutableStateFlow(AppState(isFirstLaunch = true, smsBudgetLimit = 40))
    override val appState: StateFlow<AppState> = _state.asStateFlow()

    override fun reloadState() {}
    override fun completeFirstLaunch() { _state.value = _state.value.copy(isFirstLaunch = false) }
    override fun setScreeningEnabled(enabled: Boolean) { _state.value = _state.value.copy(isScreeningEnabled = enabled) }
    override fun setBlockNonContacts(enabled: Boolean) { _state.value = _state.value.copy(blockNonContacts = enabled) }
    override fun setBlockExtraEuCalls(enabled: Boolean) { _state.value = _state.value.copy(blockExtraEuCalls = enabled) }
    override fun setBlockEuCalls(enabled: Boolean) { _state.value = _state.value.copy(blockEuCalls = enabled) }
    override fun setExcludedEuPrefixes(prefixes: Set<String>) { _state.value = _state.value.copy(excludedEuPrefixes = prefixes) }
    override fun toggleExcludedEuPrefix(prefix: String) {
        val current = _state.value.excludedEuPrefixes.toMutableSet()
        if (current.contains(prefix)) current.remove(prefix) else current.add(prefix)
        _state.value = _state.value.copy(excludedEuPrefixes = current)
    }
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
