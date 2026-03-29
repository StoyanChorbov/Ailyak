package aubg.hack.ailyak.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import aubg.hack.ailyak.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(4.dp))

        // ─────────────────────────────────────────────────────────
        // APPEARANCE
        // ─────────────────────────────────────────────────────────
//        SettingsSectionHeader("Appearance")

//        SettingsToggleRow(
//            title = "Dark Theme",
//            subtitle = "Switch to dark mode",
//            checked = state.darkTheme,
//            onCheckedChange = { viewModel.setDarkTheme(it) }
//        )

        SettingsSectionHeader("Map Style")
        MapStyleSelector(
            selected = state.mapStyle,
            onSelect = { viewModel.setMapStyle(it) }
        )

        // ─────────────────────────────────────────────────────────
        // TRACKING
        // ─────────────────────────────────────────────────────────
        SettingsSectionHeader("Tracking")

        SettingsToggleRow(
            title = "Battery Saver",
            subtitle = "Reduces GPS frequency to save battery",
            checked = state.batterySaver,
            onCheckedChange = { viewModel.setBatterySaver(it) }
        )

        SettingsSliderRow(
            title = "Tracking Interval",
            subtitle = "Record location every ${state.trackingIntervalMin} min",
            value = state.trackingIntervalMin.toFloat(),
            valueRange = 1f..15f,
            steps = 13,
            onValueChange = { viewModel.setTrackingInterval(it.toInt()) }
        )

        SettingsSliderRow(
            title = "Search Radius",
            subtitle = "Search for species within ${state.searchRadiusKm} km",
            value = state.searchRadiusKm.toFloat(),
            valueRange = 5f..30f,
            steps = 4,
            onValueChange = { viewModel.setSearchRadius(it.toInt()) }
        )

        // ─────────────────────────────────────────────────────────
        // NOTIFICATIONS
        // ─────────────────────────────────────────────────────────
        SettingsSectionHeader("Notifications")

        SettingsToggleRow(
            title = "Connection Lost Alert",
            subtitle = "Notify when internet connection is lost",
            checked = state.notifyNoConnection,
            onCheckedChange = { viewModel.setNotifyNoConnection(it) }
        )

        SettingsToggleRow(
            title = "GPS Signal Lost Alert",
            subtitle = "Notify when GPS signal is lost",
            checked = state.notifyNoGps,
            onCheckedChange = { viewModel.setNotifyNoGps(it) }
        )

        // ─────────────────────────────────────────────────────────
        // EMERGENCY CONTACT
        // ─────────────────────────────────────────────────────────
        SettingsSectionHeader("Emergency Contact")

        Text(
            text = "If offline for 24+ hours, an SMS will be sent to this contact.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(4.dp))

        OutlinedTextField(
            value = state.emergencyContactName,
            onValueChange = { viewModel.setEmergencyName(it) },
            label = { Text("Contact Name") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = state.emergencyContactPhone,
            onValueChange = { viewModel.setEmergencyPhone(it) },
            label = { Text("Phone Number") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(Modifier.height(16.dp))
    }
}

// ── Section header ─────────────────────────────────────────────────
@Composable
private fun SettingsSectionHeader(title: String) {
    Spacer(Modifier.height(8.dp))
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 2.dp)
    )
    HorizontalDivider()
    Spacer(Modifier.height(4.dp))
}

// ── Toggle row ─────────────────────────────────────────────────────
@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

// ── Slider row ─────────────────────────────────────────────────────
@Composable
private fun SettingsSliderRow(
    title: String,
    subtitle: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    valueRange.start.toInt().toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    valueRange.endInclusive.toInt().toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ── Map style selector ─────────────────────────────────────────────
@Composable
private fun MapStyleSelector(selected: String, onSelect: (String) -> Unit) {
    val styles = listOf(
        "outdoors"  to "🌲 Outdoors",
        "satellite" to "🛰 Satellite",
        "streets"   to "🗺 Streets",
        "light"     to "☀ Light",
        "dark"      to "🌑 Dark"
    )
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            styles.forEach { (key, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selected == key,
                        onClick = { onSelect(key) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(label, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}