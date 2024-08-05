import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hoppers.duoclock.AppWidgetConfigureActivity
import com.hoppers.duoclock.common.localproviders.LocalNavController
import com.hoppers.duoclock.dashboard.data.DashBoardScreenArgs
import com.hoppers.duoclock.dashboard.screen.DashBoardScreen
import com.hoppers.duoclock.dashboard.viewmodel.DashboardViewModel
import com.hoppers.duoclock.navigation.AppScreens
import com.hoppers.duoclock.search.screen.SearchLocationScreen
import com.hoppers.duoclock.search.viewmodel.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun Start(context: AppWidgetConfigureActivity) {
    NavHost(
        navController = LocalNavController.current,
        startDestination = AppScreens.DashBoard.route
    ) {
        composable(route = AppScreens.DashBoard.route) {
            val viewModel = koinViewModel<DashboardViewModel>()
            val args = DashBoardScreenArgs(
                state = viewModel.uiState,
                dataList = viewModel.dataList,
                reset = viewModel::resetState,
                arrange = viewModel::arrange,
                onRemove = viewModel::removeItems,
                onDone = viewModel::onDone,
                onSelect = viewModel::onSelect,
                onEvent = viewModel::getData,
                appWidgetId = context.mAppWidgetId,
                onStart = viewModel::doOnStart,
                onStop = viewModel::doOnStop
            )
            DashBoardScreen(args)
        }
        composable(route = AppScreens.SearchLocation.route) {
            val searchViewModel = koinViewModel<SearchViewModel>()
            val result = searchViewModel.uiState.collectAsState().value
            SearchLocationScreen(result, searchViewModel::doSearch)
        }
    }
}
