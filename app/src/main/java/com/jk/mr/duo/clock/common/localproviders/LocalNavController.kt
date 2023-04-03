package com.jk.mr.duo.clock.common.localproviders

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No LocalNavController provided")
}
val LocalScaffold = staticCompositionLocalOf<ScaffoldState> {
    error("No LocalScaffold provided")
}
