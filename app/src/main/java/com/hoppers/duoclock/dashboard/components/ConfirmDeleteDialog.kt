package com.hoppers.duoclock.dashboard.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.hoppers.duoclock.dashboard.data.DeleteDialogState

@Composable
fun ConfirmDeleteDialog(
    dialogState: DeleteDialogState,
    confirm: () -> Unit,
    cancel: () -> Unit
) {

    if (dialogState is DeleteDialogState.Confirm) {
        val item = dialogState.item

        AlertDialog(
            onDismissRequest = { cancel() },
            title = { Text("Delete clock") },
            text = {
                Text("Are you sure you want to delete ${item.name}?")
            },
            confirmButton = {
                TextButton(onClick = { confirm() }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { cancel() }) {
                    Text("Cancel")
                }
            }
        )
    }
}