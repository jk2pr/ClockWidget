package com.hoppers.duoclock.setting

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontVariation.Settings
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoppers.duoclock.component.Page

@Composable
fun AppSettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    var hasExactAlarmPermission by remember {
        mutableStateOf(
            alarmManager.canScheduleExactAlarms()
        )
    }

    // Re-check permission when coming back from Settings
    LaunchedEffect(Unit) {
        snapshotFlow { true }.collect {
            hasExactAlarmPermission =
                alarmManager.canScheduleExactAlarms()
        }
    }

    Page(title = { Text(text = "Settings") }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            LiveUpdateSettingCard(
                enabled = hasExactAlarmPermission,
                onEnableClick = {
                    openExactAlarmSystemSettings(context)
                }
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Uses a system permission to allow minute-by-minute updates.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LiveUpdateSettingCard(
    enabled: Boolean,
    onEnableClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Live clock updates",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Update widget every minute for accurate time.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (enabled) "Status: Enabled" else "Status: Off",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                if (!enabled) {
                    TextButton(onClick = onEnableClick) {
                        Text("Enable")
                    }
                }
            }
        }
    }
}

fun openExactAlarmSystemSettings(context: Context) {
    context.startActivity(
        Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
    )

}

@Preview
@Composable
private fun SettingPreview() {
    AppSettingsScreen { }
}
