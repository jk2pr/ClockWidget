package com.jk.mr.duo.clock.common.localproviders

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No LocalNavController provided")
}
val LocalSnackBarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No LocalScaffold provided")
}
