package com.jk.mr.duo.clock.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Page(
    floatingActionButton: @Composable () -> Unit ={},
    content: @Composable (PaddingValues) -> Unit ,
) {
    Scaffold(
        topBar = { AppBar() },
        floatingActionButton = floatingActionButton,
        content = content
    )

}

@Composable
private fun AppBar() {
    TopAppBar(title = { },
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
    )
}