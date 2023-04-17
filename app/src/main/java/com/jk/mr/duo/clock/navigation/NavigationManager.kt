import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jk.mr.duo.clock.common.localproviders.LocalNavController
import com.jk.mr.duo.clock.navigation.AppScreens
import com.jk.mr.duo.clock.ui.screen.DashBoardScreen
import com.jk.mr.duo.clock.ui.screen.SearchLocationScreen

@Composable
fun Start() {
    NavHost(navController = LocalNavController.current, startDestination = AppScreens.DashBoard.route) {
        composable(route = AppScreens.DashBoard.route) {
            DashBoardScreen()
        }
        composable(route = AppScreens.SearchLocation.route) {
            SearchLocationScreen()
        }
    }
}
