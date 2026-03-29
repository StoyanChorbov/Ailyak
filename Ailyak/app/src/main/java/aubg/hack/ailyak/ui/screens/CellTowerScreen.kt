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
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import aubg.hack.ailyak.data.model.CellRadioType
import aubg.hack.ailyak.viewmodel.CellTowerViewModel

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun CellTowerScreen(viewModel: CellTowerViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val items by viewModel.filteredItems.collectAsState()

    val permissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        )
    )

    LaunchedEffect(permissions.allPermissionsGranted) {
        if (permissions.allPermissionsGranted) {
            LocationServices.getFusedLocationProviderClient(context)
                .lastLocation.addOnSuccessListener { loc ->
                    loc?.let { viewModel.load(it) }
                }
        } else {
            permissions.launchMultiplePermissionRequest()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "Cell Towers",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // ── Radio filter chips ─────────────────────────────────────
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
            CellRadioType.entries.forEach { radio ->
                FilterChip(
                    selected = uiState.filter == radio,
                    onClick = { viewModel.setFilter(radio) },
                    label = { Text(radio.label()) }
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
                    "No cell towers found nearby.",
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "${items.size} towers found · sorted by distance",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(items, key = { it.id }) { tower ->
                        CellTowerCard(tower = tower)
                    }
                    item {
                        Text(
                            text = "Tower data from OpenCelliD · opencellid.org",
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