package com.hoppers.duoclock.navigation

sealed class AppScreens(val route: String) {
    object DashBoard : AppScreens(ScreenName.DASHBOARD_SCREEN)
    object SearchLocation : AppScreens(ScreenName.SEARCH_SCREEN)
}
