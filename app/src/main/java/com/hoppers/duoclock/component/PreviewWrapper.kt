package com.hoppers.duoclock.component

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.hoppers.duoclock.common.localproviders.LocalNavController
import com.hoppers.duoclock.common.localproviders.LocalSnackBarHostState

@Composable
fun ComposeLocalWrapper(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalNavController provides rememberNavController(),
        LocalSnackBarHostState provides remember { SnackbarHostState() },
        content = content
    )
}
