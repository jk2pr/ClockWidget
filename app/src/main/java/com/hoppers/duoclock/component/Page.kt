package com.hoppers.duoclock.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.hoppers.duoclock.common.localproviders.LocalNavController
import com.hoppers.duoclock.common.localproviders.LocalSnackBarHostState
import com.hoppers.duoclock.navigation.AppScreens

@Composable
fun Page(
    menuItems: List<DropdownMenuItemContent> = emptyList(),
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = { AppBar(menuItems = menuItems) },
        floatingActionButton = floatingActionButton,
        content = { paddingValues ->
            Box(
                modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = LocalSnackBarHostState.current,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Bottom)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(menuItems: List<DropdownMenuItemContent>) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(),
        title = {},
        navigationIcon = {
            val navController = LocalNavController.current
            if (navController.previousBackStackEntry?.destination?.route == AppScreens.DashBoard.route) {
                NavigationIcon(navController = navController)
            }
        },

        actions = {
            if (menuItems.isNotEmpty()) {
                menuItems.forEach { it.menu() }
            }
        }
    )
}

@Composable
private fun NavigationIcon(navController: NavController) {
    IconButton(
        onClick = { navController.popBackStack() }
    ) {
        Icon(
            Icons.Default.ArrowBack,
            contentDescription = ""
        )
    }
}

data class DropdownMenuItemContent(var menu: @Composable () -> Unit)
