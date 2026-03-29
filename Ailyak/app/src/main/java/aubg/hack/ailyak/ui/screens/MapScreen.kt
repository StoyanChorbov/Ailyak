package aubg.hack.ailyak.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import aubg.hack.ailyak.ui.components.EmergencyMapOverlay
import aubg.hack.ailyak.viewmodel.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(viewModel: MapViewModel = hiltViewModel()) {
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    if (!locationPermissions.allPermissionsGranted) {
        PermissionRequestScreen(
            shouldShowRationale = locationPermissions.shouldShowRationale,
            onRequestPermission = { locationPermissions.launchMultiplePermissionRequest() }
        )
        return
    }

    MapContent(viewModel = viewModel)
}

@SuppressLint("MissingPermission")
@Composable
private fun MapContent(viewModel: MapViewModel) {
    val context = LocalContext.current
    val isConnected by viewModel.isConnected.collectAsState()
    val pathPoints by viewModel.pathPoints.collectAsState()
    val connectionLostPoint by viewModel.connectionLostPoint.collectAsState()
    val showCellularHeatmap by viewModel.showCellularHeatmap.collectAsState()
    val showWaterSources by viewModel.showWaterSources.collectAsState()
    val showShelters by viewModel.showShelters.collectAsState()
    val waterSources by viewModel.waterSources.collectAsState()
    val shelters by viewModel.shelters.collectAsState()
    val cellTowers by viewModel.cellTowers.collectAsState()   // ← must be here
    val mapStyleUrl by viewModel.mapStyleUrl.collectAsState()
    val towerRadiusOverlays = remember(cellTowers) {
        buildNonOverlappingTowerRadiusOverlays(cellTowers)
    }

    var selectedWaterId by remember { mutableStateOf<Long?>(null) }
    val selectedWater = waterSources.firstOrNull { it.id == selectedWaterId }

    var selectedShelterId by remember { mutableStateOf<Long?>(null) }
    val selectedShelter = shelters.firstOrNull { it.id == selectedShelterId }

    var selectedPoint by remember { mutableStateOf<Point?>(null) }
    val downloadRadiusMeters = 10000.0 // 10km

    val mapViewportState = rememberMapViewportState {
        setCameraOptions { zoom(2.0); pitch(0.0) }
    }

    LaunchedEffect(Unit) {
        LocationServices.getFusedLocationProviderClient(context)
            .lastLocation.addOnSuccessListener { location ->
                location?.let {
                    mapViewportState.flyTo(
                        cameraOptions {
                            center(Point.fromLngLat(it.longitude, it.latitude))
                            zoom(15.0)
                            pitch(0.0)
                        }
                    )
                    viewModel.loadWaterForMap(it.latitude, it.longitude)
                    viewModel.loadCellTowersForMap(it.latitude, it.longitude)
                    viewModel.loadSheltersForMap(it.latitude, it.longitude)
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            style = { MapStyle(style = mapStyleUrl) },
            onMapClickListener = {
                selectedPoint = it
                true
            }
        ) {
            // ── Blue location puck ─────────────────────────────────
            MapEffect(Unit) { mapView ->
                mapView.location.apply {
                    updateSettings {
                        enabled = true
                        locationPuck = createDefault2DPuck(withBearing = true)
                        puckBearingEnabled = true
                        puckBearing = PuckBearing.HEADING
                    }
                }
            }

            // ── Path polyline ──────────────────────────────────────
            if (pathPoints.size >= 2) {
                PolylineAnnotation(
                    points = pathPoints.map { Point.fromLngLat(it.lng, it.lat) }
                ) {
                    lineColor = if (isConnected) Color(0xFF2196F3) else Color(0xFFFFC107)
                    lineWidth = 4.0
                }
            }

            // ── Connection lost marker ─────────────────────────────
            connectionLostPoint?.let { lost ->
                PointAnnotation(point = Point.fromLngLat(lost.lng, lost.lat)) {
                    iconSize = 1.5
                }
            }

            // ── Water source markers ───────────────────────────────
            if (showWaterSources) {
                waterSources.forEach { source ->
                    {
                        selectedWaterId =
                            if (selectedWaterId == source.id) null else source.id
                        true
                    }
                    CircleAnnotation(point = Point.fromLngLat(source.lng, source.lat),
                        init = {
                            circleColor = Color(0xFF1565C0)
                            circleRadius = 8.0
                            circleStrokeColor = Color.White
                            circleStrokeWidth = 2.0
                        })
                }

                // Name tooltip above tapped marker
                selectedWater?.let { water ->
                    PointAnnotation(
                        point = Point.fromLngLat(water.lng, water.lat)
                    ) {
                        textField = "${water.name}\n${water.type.label()}"
                        textSize = 12.0
                        textColor = Color(0xFF1565C0)
                        textHaloColor = Color.White
                        textHaloWidth = 2.0
                        textOffset = listOf(0.0, -2.5)
                    }
                }
            }

            if (showCellularHeatmap) {
                towerRadiusOverlays.forEach { overlay ->
                    key("tower_overlay_${overlay.key}") {
                        PolygonAnnotation(
                            points = listOf(
                                createCirclePolygonPoints(
                                    centerLat = overlay.lat,
                                    centerLng = overlay.lng,
                                    radiusMeters = overlay.radiusMeters,
                                    segments = overlay.segments
                                )
                            )
                        ) {
                            fillColor = Color(overlay.color).copy(alpha = 0.1f)
                        }
                    }
                }

                cellTowers.forEach { tower ->
                    key("${tower.id}_${tower.lat}_${tower.lng}") {
                        CircleAnnotation(
                            point = Point.fromLngLat(tower.lng, tower.lat)
                        ) {
                            circleColor = Color(tower.radio.color())
                            circleRadius = 10.0
                            circleOpacity = 0.5
                            circleStrokeColor = Color.White
                            circleStrokeWidth = 1.0
                        }
                    }
                }
            }

            if (showShelters) {
                var selectedShelterId by remember { mutableStateOf<Long?>(null) }
                val selectedShelter = shelters.firstOrNull { it.id == selectedShelterId }

                shelters.forEach { shelter ->
                    key(shelter.id) {
                        {
                            selectedShelterId =
                                if (selectedShelterId == shelter.id) null else shelter.id
                            true
                        }
                        CircleAnnotation(
                            point = Point.fromLngLat(shelter.lng, shelter.lat),
                            init = {
                                circleColor = Color(0xFF2E7D32)
                                circleRadius = 9.0
                                circleStrokeColor = Color.White
                                circleStrokeWidth = 2.0
                            })
                    }
                }

                selectedShelter?.let { s ->
                    key("shelter_label_${s.id}") {
                        PointAnnotation(point = Point.fromLngLat(s.lng, s.lat)) {
                            textField = "${s.name}\n${s.type.label()}"
                            textSize = 12.0
                            textColor = Color(0xFF2E7D32)
                            textHaloColor = Color.White
                            textHaloWidth = 2.0
                            textOffset = listOf(0.0, -2.5)
                        }
                    }
                }
            }

            selectedPoint?.let { point ->

                // Center marker
                CircleAnnotation(point = point) {
                    circleColor = Color.Red
                    circleRadius = 10.0
                    circleStrokeColor = Color.White
                    circleStrokeWidth = 2.0
                }
            }
        }

        // ── Layer toggle menu ──────────────────────────────────────
        LayerToggleMenu(
            showCellularHeatmap = showCellularHeatmap,
            showWaterSources = showWaterSources,
            showShelters = showShelters,
            onToggleCellular = { viewModel.toggleCellularHeatmap() },
            onToggleWater = {
                selectedWaterId = null
                viewModel.toggleWaterSources()
            },
            onToggleShelters = {
                selectedShelterId = null
                viewModel.toggleShelters()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        )

        // ── Offline banner ─────────────────────────────────────────
        if (!isConnected) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFFFC107)
            ) {
                Text(
                    text = "⚠ No Connection — Path tracking offline",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Black
                )
            }
        }
        if (selectedPoint != null) {
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 90.dp),
                onClick = {
                    selectedPoint?.let { point ->
                        viewModel.downloadDataForArea(
                            lat = point.latitude(),
                            lon = point.longitude(),
                            radiusMeters = downloadRadiusMeters
                        )
                    }
                },
            ) {
                Text("⬇")
            }
        }

        EmergencyMapOverlay(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 72.dp)
        )
    }
}

private data class TowerRadiusOverlay(
    val key: String,
    val lat: Double,
    val lng: Double,
    val radiusMeters: Double,
    val color: Long,
    val segments: Int
)

private fun buildNonOverlappingTowerRadiusOverlays(
    towers: List<aubg.hack.ailyak.data.model.CellTowerUi>,
    maxOverlays: Int = 24
): List<TowerRadiusOverlay> {
    val candidates = towers
        .asSequence()
        .mapNotNull { tower ->
            val radius = tower.rangeMeters?.toDouble()?.takeIf { it > 0.0 } ?: return@mapNotNull null
            val clampedRadius = radius.coerceIn(120.0, 2_500.0)
            val segments = when {
                clampedRadius >= 1_800.0 -> 30
                clampedRadius >= 1_000.0 -> 24
                else -> 18
            }
            TowerRadiusOverlay(
                key = "${tower.id}_${tower.lat}_${tower.lng}",
                lat = tower.lat,
                lng = tower.lng,
                radiusMeters = clampedRadius,
                color = tower.radio.color(),
                segments = segments
            )
        }
        .sortedByDescending { it.radiusMeters }
        .toList()

    val selected = mutableListOf<TowerRadiusOverlay>()
    for (candidate in candidates) {
        val overlapsExisting = selected.any { existing ->
            val centerDistance = approximateDistanceMeters(
                candidate.lat,
                candidate.lng,
                existing.lat,
                existing.lng
            )
            centerDistance < min(candidate.radiusMeters, existing.radiusMeters) * 0.6
        }
        if (!overlapsExisting) {
            selected += candidate
            if (selected.size >= maxOverlays) break
        }
    }

    return selected
}

private fun approximateDistanceMeters(
    lat1: Double,
    lng1: Double,
    lat2: Double,
    lng2: Double
): Double {
    val metersPerDegreeLat = 111_320.0
    val avgLatRad = Math.toRadians((lat1 + lat2) / 2.0)
    val metersPerDegreeLng = metersPerDegreeLat * cos(avgLatRad)
    val dx = (lng2 - lng1) * metersPerDegreeLng
    val dy = (lat2 - lat1) * metersPerDegreeLat
    return sqrt(dx * dx + dy * dy)
}

private fun createCirclePolygonPoints(
    centerLat: Double,
    centerLng: Double,
    radiusMeters: Double,
    segments: Int = 48
): List<Point> {
    val earthRadiusMeters = 6_371_000.0
    val angularDistance = radiusMeters / earthRadiusMeters
    val latRad = Math.toRadians(centerLat)
    val lngRad = Math.toRadians(centerLng)

    val ring = (0..segments).map { step ->
        val bearing = (2.0 * PI * step) / segments
        val pointLat = asin(
            sin(latRad) * cos(angularDistance) +
                cos(latRad) * sin(angularDistance) * cos(bearing)
        )
        val pointLng = lngRad + atan2(
            sin(bearing) * sin(angularDistance) * cos(latRad),
            cos(angularDistance) - sin(latRad) * sin(pointLat)
        )
        Point.fromLngLat(Math.toDegrees(pointLng), Math.toDegrees(pointLat))
    }

    return ring
}

// ── Layer toggle menu ──────────────────────────────────────────────
@Composable
private fun LayerToggleMenu(
    showCellularHeatmap: Boolean,
    showWaterSources: Boolean,
    showShelters: Boolean,
    onToggleCellular: () -> Unit,
    onToggleWater: () -> Unit,
    onToggleShelters: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {

        // ── Floating icon button ───────────────────────────────────
        SmallFloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = if (expanded)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surface,
            contentColor = if (expanded)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurface,
            elevation = FloatingActionButtonDefaults.elevation(4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Layers,
                contentDescription = "Toggle map layers"
            )
        }

        // ── Dropdown panel ─────────────────────────────────────────
        if (expanded) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                tonalElevation = 4.dp,
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
                    LayerToggleRow(
                        emoji = "📶",
                        label = "Cell Towers",
                        checked = showCellularHeatmap,
                        onToggle = onToggleCellular
                    )
                    Spacer(Modifier.height(16.dp))
                    LayerToggleRow(
                        emoji = "💧",
                        label = "Water",
                        checked = showWaterSources,
                        onToggle = onToggleWater
                    )
                    Spacer(Modifier.height(16.dp))
                    LayerToggleRow(
                        emoji = "⛺",
                        label = "Shelters",
                        checked = showShelters,
                        onToggle = onToggleShelters
                    )
                }
            }
        }
    }
}

@Composable
private fun LayerToggleRow(
    emoji: String,
    label: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(emoji, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            modifier = Modifier.height(10.dp)
        )
    }
}

// ── Permission screen ──────────────────────────────────────────────
@Composable
private fun PermissionRequestScreen(
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📍", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Location Permission Required",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (shouldShowRationale)
                "Wildguard needs your location to track your path and show nearby plants, water sources, and shelters."
            else
                "Please grant location access so Wildguard can keep you safe in the wild.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRequestPermission, shape = RoundedCornerShape(24.dp)) {
            Text("Grant Location Access")
        }
    }
}