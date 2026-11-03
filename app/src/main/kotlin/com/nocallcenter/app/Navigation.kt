package com.nocallcenter.app

import androidx.compose.runtime.Composable
import com.nocallcenter.app.ui.main.MainScreen
import com.nocallcenter.app.ui.main.MainScreenViewModel

@Composable
fun MainNavigation(viewModel: MainScreenViewModel) {
    MainScreen(viewModel = viewModel)
}
