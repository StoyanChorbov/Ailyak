package aubg.hack.ailyak.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import aubg.hack.ailyak.data.model.DrinkableStatus
import aubg.hack.ailyak.data.model.WaterSourceUi

@Composable
fun WaterSourceCard(source: WaterSourceUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Type icon ──────────────────────────────────────────
            Text(
                text = source.type.label().split(" ").first(),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.width(48.dp)
            )

            Spacer(Modifier.width(10.dp))

            // ── Name + type ────────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = source.name,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = source.type.label().substringAfter(" "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                DrinkableBadge(status = source.drinkable)
            }

            // ── Distance ───────────────────────────────────────────
            Text(
                text = formatDistance(source.distanceMeters),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DrinkableBadge(status: DrinkableStatus) {
    val (label, color) = when (status) {
        DrinkableStatus.YES     -> status.label() to Color(0xFF2E7D32)
        DrinkableStatus.NO      -> status.label() to Color(0xFFC62828)
        DrinkableStatus.UNKNOWN -> status.label() to Color(0xFF757575)
    }
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

fun formatDistance(meters: Double): String = when {
    meters < 1000 -> "${meters.toInt()}m"
    else -> "${"%.1f".format(meters / 1000)}km"
}