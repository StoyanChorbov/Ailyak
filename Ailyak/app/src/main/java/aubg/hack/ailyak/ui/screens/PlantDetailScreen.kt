package aubg.hack.ailyak.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import aubg.hack.ailyak.data.model.PlantUiItem
import aubg.hack.ailyak.data.model.SafetyLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(plant: PlantUiItem, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(plant.commonName ?: plant.scientificName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Photo ──────────────────────────────────────────────
            AsyncImage(
                model = plant.photoUrl,
                contentDescription = plant.commonName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            // ── Name + badge ───────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = plant.commonName ?: "Unknown",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = plant.scientificName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                SafetyBadge(safetyLevel = plant.safetyLevel)
            }

            HorizontalDivider()

            // ── Type ───────────────────────────────────────────────
            Text(
                text = if (plant.isFungus) "🍄 Mushroom / Fungus" else "🌿 Plant",
                style = MaterialTheme.typography.labelLarge
            )

            // ── Summary ────────────────────────────────────────────
            if (!plant.wikipediaUrl.isNullOrBlank()) {
                val uriHandler = LocalUriHandler.current
                OutlinedButton(
                    onClick = { uriHandler.openUri(plant.wikipediaUrl) },
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📖 Read on Wikipedia")
                }
            }

            // ── Safety warning card ────────────────────────────────
            SafetyWarningCard(plant = plant)
        }
    }
}

@Composable
private fun SafetyWarningCard(plant: PlantUiItem) {
    val (title, body) = when (plant.safetyLevel) {
        SafetyLevel.TOXIC ->
            "⛔ Do Not Consume" to "This species or its genus is known to be toxic or poisonous. Do not eat under any circumstances without expert verification."
        SafetyLevel.CAUTION ->
            "⚠ Use Caution" to "Some parts of this species may be edible but others could be harmful. Verify with an expert before consuming."
        SafetyLevel.EDIBLE ->
            "✅ Generally Edible" to "This species is generally considered edible. Always ensure correct identification before consuming any wild plant."
        SafetyLevel.UNKNOWN ->
            "❓ Unknown Safety" to "Safety information is unavailable for this species. Do not consume without expert identification."
    }
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(body, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}