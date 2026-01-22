package com.hoppers.duoclock.dashboard.components.skelton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PinnedSkeletonRow() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {

        // "Pinned" label placeholder
        SkeletonBox(
            modifier = Modifier
                .width(80.dp)
                .height(16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(5) {
                SkeletonBox(
                    modifier = Modifier
                        .width(100.dp)
                        .height(60.dp)
                )
            }
        }
    }
}