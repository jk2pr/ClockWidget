package com.hoppers.duoclock.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoppers.duoclock.common.component.ComposeLocalWrapper

@Composable
fun LiveUpdateBanner(
    visible: Boolean,
    onEnableClick: () -> Unit
) {
    if (!visible) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = "Live updates are off",
                style = MaterialTheme.typography.bodyMedium
            )

            TextButton(onClick = onEnableClick) {
                Text("Enable")
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ComposeLocalWrapper {
        LiveUpdateBanner(true) { }
    }
}