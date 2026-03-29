package aubg.hack.ailyak.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import aubg.hack.ailyak.data.model.ShelterType
import aubg.hack.ailyak.viewmodel.ShelterViewModel

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun ShelterIndexScreen(viewModel: ShelterViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val items by viewModel.filteredItems.collectAsState()
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(locationPermission.status.isGranted) {
        if (locationPermission.status.isGranted) {
            LocationServices.getFusedLocationProviderClient(context)
                .lastLocation.addOnSuccessListener { loc ->
                    loc?.let { viewModel.load(it) }
                }
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "Shelters",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // ── Type filter chips ──────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = uiState.filter == null,
                onClick = { viewModel.setFilter(null) },
                label = { Text("All") }
            )
            ShelterType.entries.forEach { type ->
                FilterChip(
                    selected = uiState.filter == type,
                    onClick = { viewModel.setFilter(type) },
                    label = { Text(type.label()) }
                )
            }
        }

        Spacer(Modifier.height(4.dp))

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
                                loc?.let { viewModel.load(it) }
                            }
                    }) { Text("Retry") }
                }
                items.isEmpty() && !uiState.isLoading -> Text(
                    "No shelters found nearby.",
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "${items.size} shelters found · sorted by distance",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(items, key = { it.id }) { shelter ->
                        ShelterCard(
                            shelter = shelter,
                            isExpanded = uiState.expandedId == shelter.id,
                            onToggleExpand = { viewModel.toggleExpanded(shelter.id) }
                        )
                    }
                    item {
                        Text(
                            text = "Shelter data from OpenStreetMap contributors · openstreetmap.org",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}