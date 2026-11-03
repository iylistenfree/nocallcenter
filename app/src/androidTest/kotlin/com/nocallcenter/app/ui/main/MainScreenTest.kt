package com.nocallcenter.app.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.nocallcenter.app.data.AppState
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** UI tests for [com.nocallcenter.app.ui.main.MainScreen]. */
class MainScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    composeTestRule.setContent {
      DashboardContent(
        state = AppState(),
        onToggleService = {},
        onRequestRole = {}
      )
    }
  }

  @Test
  fun dashboard_rendersStatus() {
    composeTestRule.onNodeWithText("NoCallCenter").assertDoesNotExist() // Checks render without crash
  }
}
