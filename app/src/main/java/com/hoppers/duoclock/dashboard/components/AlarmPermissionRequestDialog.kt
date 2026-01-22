package com.hoppers.duoclock.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hoppers.duoclock.common.component.ComposeLocalWrapper
import com.jk.mr.duo.clock.R

@Composable
fun AlarmPermissionRequestDialog(
    onContinue: () -> Unit,
    onDismiss: () -> Unit
) {
    val appName = stringResource(R.string.app_name)
    Dialog(onDismiss) {


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Allow live updates",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "On the next screen:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Optional: replace with your app icon
                    Icon(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                    )
                    Spacer(
                        Modifier.width(8.dp)
                    )
                    Text("Scroll down and find ${stringResource(R.string.app_name)}")
                }

                Text(
                    text = "Then enable:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text("• Turn ON the switch next to $appName")
                Text(
                    text = "The system may close $appName during this step. Just reopen the app if that happens.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = onContinue) {
                        Text("Open settings")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ComposeLocalWrapper {
        AlarmPermissionRequestDialog(onContinue = {}) { }
    }
}