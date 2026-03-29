package aubg.hack.ailyak.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import aubg.hack.ailyak.data.model.ShelterType
import aubg.hack.ailyak.data.model.ShelterUiItem

@Composable
fun ShelterCard(
    shelter: ShelterUiItem,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // ── Collapsed row ──────────────────────────────────────
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Type icon
                Text(
                    text = shelter.type.label().split(" ").first(),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.width(48.dp)
                )

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = shelter.name,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = shelter.type.label().substringAfter(" "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        shelter.hasWater?.let { hasWater ->
                            StatusChip(
                                label = if (hasWater) "💧 Water" else "No Water",
                                color = if (hasWater) Color(0xFF1565C0) else Color(0xFF757575)
                            )
                        }
                        shelter.isLockable?.let { lockable ->
                            StatusChip(
                                label = if (lockable) "🔒 Lockable" else "🔓 Open",
                                color = if (lockable) Color(0xFF2E7D32) else Color(0xFF757575)
                            )
                        }
                        shelter.accessType?.let { access ->
                            if (access != "yes" && access.isNotBlank()) {
                                StatusChip(
                                    label = "Access: $access",
                                    color = Color(0xFFF9A825)
                                )
                            }
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatDistance(shelter.distanceMeters),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (isExpanded) "▲" else "▼",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── Expanded section ───────────────────────────────────
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                exit = shrinkVertically(tween(300)) + fadeOut(tween(300))
            ) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Description
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    .copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = shelter.type.label(),
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = shelter.type.description(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Survival tip
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2E7D32).copy(alpha = 0.08f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "🧭 Survival Tip",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color(0xFF2E7D32)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = survivalTip(shelter.type),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Coordinates
                        Text(
                            text = "📍 ${"%.5f".format(shelter.lat)}, ${"%.5f".format(shelter.lng)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

private fun survivalTip(type: ShelterType): String = when (type) {
    ShelterType.WILDERNESS_HUT  ->
        "Check for firewood, matches and emergency supplies inside. Many wilderness huts have a logbook — sign in with your route and expected exit date."
    ShelterType.ALPINE_HUT      ->
        "Alpine huts are staffed seasonally. Outside season they may have an emergency room (Notfall-Raum) left unlocked with basic supplies."
    ShelterType.BASIC_HUT       ->
        "Basic huts offer full wind and rain protection. Prioritise insulation — use any available materials to insulate from the floor."
    ShelterType.LEAN_TO         ->
        "Lean-tos block wind from one direction. Position your fire at the open side to reflect heat inward. Keep fire small and controlled."
    ShelterType.WEATHER_SHELTER ->
        "Good for short-term rest and weather waiting. Not suitable for overnight stays without additional insulation."
    ShelterType.PICNIC_SHELTER  ->
        "Provides rain cover but minimal thermal protection. Use as a base to build additional shelter with natural materials."
    ShelterType.CAVE            ->
        "Check for animals before entering. Avoid deep caves — stay near the entrance. Cold air sinks so sleep elevated off the cave floor."
    ShelterType.OTHER           ->
        "Assess structural integrity before sheltering. Clear any debris and insulate yourself from the ground to prevent heat loss."
}