package com.hoppers.duoclock.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hoppers.duoclock.common.localproviders.LocalNavController
import com.hoppers.duoclock.dashboard.data.DashBoardScreenArgs
import com.hoppers.duoclock.dashboard.screen.DashBoardScreen
import com.hoppers.duoclock.dashboard.viewmodel.DashboardViewModel
import com.hoppers.duoclock.search.screen.SearchLocationScreen
import com.hoppers.duoclock.search.viewmodel.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun Start() {
    NavHost(
        navController = LocalNavController.current,
        startDestination = AppScreens.DashBoard.route
    ) {
        composable(route = AppScreens.DashBoard.route) {
            val viewModel = koinViewModel<DashboardViewModel>()
            val args = DashBoardScreenArgs(
                state = viewModel.uiState,
                cityUiState = viewModel.citiesUiState,
                dialogState = viewModel.dialogState,
                requestDelete = viewModel::requestRemove,
                confirmDelete = viewModel::confirmRemove,
                cancelRemove = viewModel::cancelRemove,
                onEvent = viewModel::addLocationFromPlace,
                onToggle = viewModel::onTogglePinned,
            )
            DashBoardScreen(args)
        }
        composable(route = AppScreens.SearchLocation.route) {
            val searchViewModel = koinViewModel<SearchViewModel>()
            val result = searchViewModel.uiState.collectAsState().value
            SearchLocationScreen(result =
                result, onSearch = searchViewModel::doSearch,
            )
        }
    }
}
