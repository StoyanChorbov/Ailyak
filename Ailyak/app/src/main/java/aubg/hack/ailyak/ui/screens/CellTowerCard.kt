package aubg.hack.ailyak.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import aubg.hack.ailyak.data.model.CellRadioType
import aubg.hack.ailyak.data.model.CellTowerUi

@Composable
fun CellTowerCard(tower: CellTowerUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (tower.isConnected)
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else null
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Radio type badge ───────────────────────────────────
            RadioTypeBadge(radio = tower.radio)

            Spacer(Modifier.width(12.dp))

            // ── Info ───────────────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = tower.radio.label(),
                        style = MaterialTheme.typography.titleSmall
                    )
                    if (tower.isConnected) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Connected",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    tower.signalStrength?.let {
                        InfoChip(label = "Signal", value = "$it dBm")
                    }
                    tower.rangeMeters?.let {
                        InfoChip(label = "Range", value = formatDistance(it.toDouble()))
                    }
                    tower.samples?.let {
                        InfoChip(label = "Samples", value = "$it")
                    }
                }
            }

            // ── Distance ───────────────────────────────────────────
            Text(
                text = formatDistance(tower.distanceMeters),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RadioTypeBadge(radio: CellRadioType) {
    val color = Color(radio.color())
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        modifier = Modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = when (radio) {
                    CellRadioType.GSM  -> "2G"
                    CellRadioType.UMTS -> "3G"
                    CellRadioType.LTE  -> "4G"
                    CellRadioType.NR   -> "5G"
                    else               -> "?"
                },
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
        }
    }
}

@Composable
private fun InfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}