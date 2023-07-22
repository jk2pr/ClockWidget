package com.jk.mr.duo.clock.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.jk.mr.duo.clock.data.ColorScheme

@Composable
fun ColorDialog(
    colorList: List<ColorScheme>,
    onDismiss: (() -> Unit),
    currentlySelected: ColorScheme,
    onColorSelected: ((ColorScheme) -> Unit) // when a colour is picked
) {
    val gridState = rememberLazyGridState()
    AlertDialog(
        shape = RoundedCornerShape(20.dp),
        onDismissRequest = onDismiss,
        title = { Text(text = "Pick a color") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                state = gridState
            ) {
                items(colorList) { color ->
                    // Add a border around the selected colour only
                    var borderWidth = 0.dp
                    if (currentlySelected == color) {
                        borderWidth = 2.dp
                    }

                    Canvas(
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(
                                borderWidth,
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                                RoundedCornerShape(20.dp)
                            )
                            .requiredSize(70.dp)
                            .clickable {
                                onColorSelected(color)
                                onDismiss()
                            }
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        drawPath(
                            Path().apply {
                                moveTo(0f, 0f)
                                lineTo(canvasWidth, 0f)
                                lineTo(0f, canvasHeight)
                                close()
                            },
                            color = color.primaryColor
                        )

                        drawPath(
                            Path().apply {
                                moveTo(canvasWidth, 0f)
                                lineTo(0f, canvasHeight)
                                lineTo(canvasWidth, canvasHeight)
                                close()
                            },
                            color = color.secondaryColor
                        ) // (alpha = 0.6f))
                    }
                }
            }
        },
        confirmButton = {}
    )
}
