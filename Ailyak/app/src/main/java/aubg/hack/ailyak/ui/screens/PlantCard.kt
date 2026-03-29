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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import aubg.hack.ailyak.data.model.PlantUiItem
import aubg.hack.ailyak.data.model.SafetyLevel

@Composable
fun PlantCard(
    plant: PlantUiItem,
    isExpanded: Boolean,
    isLoadingSummary: Boolean,
    summary: String?,
    onToggleExpand: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

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
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = plant.photoUrl,
                    contentDescription = plant.commonName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plant.commonName ?: plant.scientificName,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = plant.scientificName,
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    SafetyBadge(safetyLevel = plant.safetyLevel)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (plant.isFungus) "🍄" else "🌿",
                        style = MaterialTheme.typography.titleLarge
                    )
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
                enter = expandVertically(animationSpec = tween(300)) + fadeIn(tween(300)),
                exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(tween(300))
            ) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))

                    // Full-width photo
                    AsyncImage(
                        model = plant.photoUrl,
                        contentDescription = plant.commonName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    Column(modifier = Modifier.padding(12.dp)) {

                        // Photo attribution
                        plant.photoAttribution?.let { attr ->
                            Text(
                                text = attr,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        // Observation count
                        Text(
                            text = "📍 ${plant.observationCount} observations in area",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(8.dp))

                        // Safety warning
                        SafetyWarningCard(plant = plant)

                        Spacer(Modifier.height(10.dp))

                        // Summary / description
                        when {
                            isLoadingSummary -> {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.CenterHorizontally),
                                    strokeWidth = 2.dp
                                )
                            }
                            !summary.isNullOrBlank() -> {
                                Text(
                                    text = "About",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = summary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                            else -> {
                                Text(
                                    text = "No description available.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }

                        // Wikipedia button
                        if (!plant.wikipediaUrl.isNullOrBlank()) {
                            OutlinedButton(
                                onClick = { uriHandler.openUri(plant.wikipediaUrl) },
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("📖 Read on Wikipedia")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SafetyBadge(safetyLevel: SafetyLevel) {
    val (label, color) = when (safetyLevel) {
        SafetyLevel.EDIBLE  -> "EDIBLE"  to Color(0xFF2E7D32)
        SafetyLevel.CAUTION -> "CAUTION" to Color(0xFFF9A825)
        SafetyLevel.TOXIC   -> "TOXIC"   to Color(0xFFC62828)
        SafetyLevel.UNKNOWN -> "UNKNOWN" to Color(0xFF757575)
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

@Composable
private fun SafetyWarningCard(plant: PlantUiItem) {
    val (title, body) = when (plant.safetyLevel) {
        SafetyLevel.TOXIC ->
            "⛔ Do Not Consume" to "This genus is known to be toxic. Do not eat under any circumstances without expert verification."
        SafetyLevel.CAUTION ->
            "⚠ Use Caution" to "Some parts may be edible but others harmful. Verify with an expert before consuming."
        SafetyLevel.EDIBLE ->
            "✅ Generally Edible" to "Generally considered edible. Always confirm correct identification before consuming any wild species."
        SafetyLevel.UNKNOWN ->
            "❓ Unknown Safety" to "Safety is unverified. Do not consume without expert identification."
    }
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(
                body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}