package com.hoppers.duoclock.dashboard.components.skelton

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoppers.duoclock.common.component.ComposeLocalWrapper
import com.hoppers.duoclock.common.component.Page

@Composable
fun DashBoardSkeleton() {
    Page {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .shimmer(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🕒 Top clock placeholder
            SkeletonBox(
                modifier = Modifier
                    .size(120.dp)
            )

            HorizontalDivider()

            // 📌 Pinned section
            PinnedSkeletonRow()

            // 📋 Main list
            repeat(5) {
                ClockRowSkeleton()
            }
        }
    }
}
fun Modifier.shimmer(): Modifier = composed {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        )
    )
    this.alpha(alpha)
}
@Preview
@Composable
private fun DashboarSkeltonPreview() {
    ComposeLocalWrapper {
        DashBoardSkeleton()
    }
}