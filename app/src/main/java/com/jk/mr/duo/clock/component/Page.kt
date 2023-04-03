package com.jk.mr.duo.clock.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jk.mr.duo.clock.common.localproviders.LocalNavController
import com.jk.mr.duo.clock.common.localproviders.LocalScaffold
import com.jk.mr.duo.clock.navigation.AppScreens

@Composable
fun Page(
    menuItems: List<DropdownMenuItemContent> = emptyList(),
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Surface {
        Scaffold(
            backgroundColor = Color.Transparent,
            modifier = Modifier.background(
                brush = Brush.verticalGradient(
                    0f to MaterialTheme.colors.primary,
                    500f to MaterialTheme.colors.primary.copy(alpha = 1.0f)
                )
            ),
            topBar = { AppBar(menuItems = menuItems) },
            floatingActionButton = floatingActionButton,
            content = content,
            snackbarHost = {
                SnackbarHost(
                    hostState = LocalScaffold.current.snackbarHostState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(Alignment.Bottom),
                )
            }
        )
    }
}

@Composable
private fun AppBar(menuItems: List<DropdownMenuItemContent>) {
    val navController = LocalNavController.current
    TopAppBar(
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
        title = { },
        navigationIcon = {
            if (navController.previousBackStackEntry?.destination?.route == AppScreens.DashBoard.route)
                NavigationIcon(navController = navController)
        },

        actions = {
            if (menuItems.isNotEmpty())
                menuItems.forEach { it.menu() }
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
            contentDescription = "",
        )
    }
}

data class DropdownMenuItemContent(var menu: @Composable () -> Unit)
