import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jk.mr.duo.clock.common.localproviders.LocalNavController
import com.jk.mr.duo.clock.navigation.AppScreens
import com.jk.mr.duo.clock.ui.AppWidgetConfigureActivity
import com.jk.mr.duo.clock.ui.screen.DashBoardScreen
import com.jk.mr.duo.clock.ui.screen.SearchLocationScreen
import com.jk.mr.duo.clock.viewmodels.CalDataViewModel

@Composable
fun Start(context: AppWidgetConfigureActivity) {
    NavHost(
        navController = LocalNavController.current,
        startDestination = AppScreens.DashBoard.route
    ) {
        composable(route = AppScreens.DashBoard.route) {
            val viewModel = hiltViewModel<CalDataViewModel>()
            DashBoardScreen(
                state = viewModel.uiState,
                onEvent = viewModel::getData,
                appWidgetId = context.mAppWidgetId,
                doOnStart = viewModel::doOnStart,
                doOnStop = viewModel::doOnStop
            )
        }
        composable(route = AppScreens.SearchLocation.route) {
            SearchLocationScreen()
        }
    }
}
