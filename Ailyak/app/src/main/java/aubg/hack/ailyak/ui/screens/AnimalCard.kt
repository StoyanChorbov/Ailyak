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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import aubg.hack.ailyak.data.model.AnimalUiItem
import aubg.hack.ailyak.data.model.IucnCategory

@Composable
fun AnimalCard(
    animal: AnimalUiItem,
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
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = animal.photoUrl,
                    contentDescription = animal.commonName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = animal.commonName ?: animal.scientificName,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = animal.scientificName,
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IucnBadge(category = animal.iucnCategory)
                        Text(
                            text = animal.animalGroup.label(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatDistance(animal.distanceMeters),
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

                    // Full photo
                    AsyncImage(
                        model = animal.photoUrl,
                        contentDescription = animal.commonName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Taxonomy
                        animal.order?.let {
                            DetailRow(label = "Order", value = it)
                        }
                        animal.family?.let {
                            DetailRow(label = "Family", value = it)
                        }
                        DetailRow(
                            label = "Distance",
                            value = formatDistance(animal.distanceMeters)
                        )

                        Spacer(Modifier.height(4.dp))

                        // IUCN status card
                        IucnStatusCard(category = animal.iucnCategory)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun IucnBadge(category: IucnCategory) {
    val (label, color) = when (category) {
        IucnCategory.LC      -> category.shortLabel() to Color(0xFF2E7D32)
        IucnCategory.NT      -> category.shortLabel() to Color(0xFF558B2F)
        IucnCategory.VU      -> category.shortLabel() to Color(0xFFF9A825)
        IucnCategory.EN      -> category.shortLabel() to Color(0xFFE65100)
        IucnCategory.CR      -> category.shortLabel() to Color(0xFFC62828)
        IucnCategory.EW,
        IucnCategory.EX      -> category.shortLabel() to Color(0xFF4A148C)
        IucnCategory.UNKNOWN -> category.shortLabel() to Color(0xFF757575)
    }
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun IucnStatusCard(category: IucnCategory) {
    val (title, body, color) = when (category) {
        IucnCategory.LC ->
            Triple("✅ Least Concern", "Population is stable. Common in its habitat.", Color(0xFF2E7D32))
        IucnCategory.NT ->
            Triple("🔵 Near Threatened", "Close to qualifying as threatened in the near future.", Color(0xFF558B2F))
        IucnCategory.VU ->
            Triple("🟡 Vulnerable", "Faces a high risk of extinction in the wild.", Color(0xFFF9A825))
        IucnCategory.EN ->
            Triple("🟠 Endangered", "Faces a very high risk of extinction in the wild.", Color(0xFFE65100))
        IucnCategory.CR ->
            Triple("🔴 Critically Endangered", "Faces an extremely high risk of extinction.", Color(0xFFC62828))
        IucnCategory.EW ->
            Triple("⚫ Extinct in Wild", "Survives only in captivity or cultivation.", Color(0xFF4A148C))
        IucnCategory.EX ->
            Triple("⚫ Extinct", "No living individuals remain.", Color(0xFF4A148C))
        IucnCategory.UNKNOWN ->
            Triple("❓ Not Assessed", "Conservation status has not been evaluated.", Color(0xFF757575))
    }
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = color)
            Spacer(Modifier.height(2.dp))
            Text(
                body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}