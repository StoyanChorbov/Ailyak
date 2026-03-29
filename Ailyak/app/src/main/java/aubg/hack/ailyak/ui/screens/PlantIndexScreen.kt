package aubg.hack.ailyak.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import aubg.hack.ailyak.data.model.PlantUiItem
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.location.LocationServices
import androidx.compose.material3.FilterChipDefaults
import aubg.hack.ailyak.viewmodel.PlantFilter
import aubg.hack.ailyak.viewmodel.PlantViewModel
import aubg.hack.ailyak.viewmodel.SafetyFilter

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun PlantIndexScreen(
    viewModel: PlantViewModel = hiltViewModel(),
    onItemClick: (PlantUiItem) -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val items by viewModel.filteredItems.collectAsState()

    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // Fetch location and trigger API once on first composition
    LaunchedEffect(locationPermission.status.isGranted) {
        if (locationPermission.status.isGranted) {
            LocationServices.getFusedLocationProviderClient(context)
                .lastLocation
                .addOnSuccessListener { location ->
                    location?.let { viewModel.loadPlants(it) }
                }
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Search bar ─────────────────────────────────────────────
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.setSearch(it) },
            placeholder = { Text("Search plants or mushrooms…") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true
        )

        // ── Type filter chips ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PlantFilter.entries.forEach { filter ->
                FilterChip(
                    selected = uiState.filter == filter,
                    onClick = { viewModel.setFilter(filter) },
                    label = {
                        Text(when (filter) {
                            PlantFilter.ALL -> "All"
                            PlantFilter.PLANTS -> "🌿 Plants"
                            PlantFilter.MUSHROOMS -> "🍄 Mushrooms"
                        })
                    }
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // ── Safety filter chips ────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SafetyFilter.entries.forEach { filter ->
                val (label, color) = when (filter) {
                    SafetyFilter.ALL     -> "All Safety"  to MaterialTheme.colorScheme.primary
                    SafetyFilter.EDIBLE  -> "✅ Edible"   to Color(0xFF2E7D32)
                    SafetyFilter.CAUTION -> "⚠ Caution"  to Color(0xFFF9A825)
                    SafetyFilter.TOXIC   -> "⛔ Toxic"    to Color(0xFFC62828)
                    SafetyFilter.UNKNOWN -> "❓ Unknown"  to Color(0xFF757575)
                }
                FilterChip(
                    selected = uiState.safetyFilter == filter,
                    onClick = { viewModel.setSafetyFilter(filter) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = color.copy(alpha = 0.15f),
                        selectedLabelColor = color
                    )
                )
            }
        }

        // ── Content ────────────────────────────────────────────────
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
                uiState.error != null -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("⚠ Failed to load", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(uiState.error!!, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = {
                        LocationServices.getFusedLocationProviderClient(context)
                            .lastLocation.addOnSuccessListener { loc ->
                                loc?.let { viewModel.loadPlants(it) }
                            }
                    }) { Text("Retry") }
                }
                items.isEmpty() && !uiState.isLoading -> Text(
                    "No species found nearby.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "${items.size} species found nearby",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(items, key = { it.id }) { plant ->
                        val isExpanded = uiState.expandedId == plant.id
                        val isLoadingSummary = uiState.loadingSummaryId == plant.id
                        val summary = uiState.summaryCache[plant.id]

                        PlantCard(
                            plant = plant,
                            isExpanded = isExpanded,
                            isLoadingSummary = isLoadingSummary,
                            summary = summary,
                            onToggleExpand = { viewModel.toggleExpanded(plant) }
                        )
                    }
                    item {
                        // ── Data attribution ───────────────────────
                        Text(
                            text = "Species data from iNaturalist · inaturalist.org",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        )
                    }
                }
            }
        }
    }
}