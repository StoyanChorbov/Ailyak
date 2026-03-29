package aubg.hack.ailyak.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aubg.hack.ailyak.location.LocationTracker
import com.example.reginola.core.common.Result
import com.example.reginola.core.database.PathPointDao
import com.example.reginola.core.database.entities.PathPointEntity
import com.example.reginola.core.network.NetworkMonitor
import com.example.reginola.features.cellular.data.CellTowerRepository
import com.example.reginola.features.cellular.data.CellTowerUi
import com.example.reginola.features.settings.data.UserPreferencesDataStore
import com.example.reginola.features.shelters.data.ShelterRepository
import com.example.reginola.features.shelters.data.ShelterUiItem
import com.example.reginola.features.water.data.WaterRepository
import com.example.reginola.features.water.data.WaterSourceUi
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    networkMonitor: NetworkMonitor,
    pathPointDao: PathPointDao,
    private val waterRepository: WaterRepository,
    private val cellTowerRepository: CellTowerRepository,
    private val shelterRepository: ShelterRepository,
    private val prefs: UserPreferencesDataStore
) : ViewModel() {

    // ── Connectivity ───────────────────────────────────────────────
    val isConnected = networkMonitor.isConnected
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    // ── Path tracking ──────────────────────────────────────────────
    val pathPoints: StateFlow<List<PathPointEntity>> = pathPointDao.getAllPoints()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val connectionLostPoint: StateFlow<PathPointEntity?> = pathPoints.map { points ->
        points.lastOrNull { !it.hadConnection }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // ── Map style ──────────────────────────────────────────────────
    val mapStyleUrl: StateFlow<String> = prefs.mapStyle.map { key ->
        when (key) {
            "satellite" -> "mapbox://styles/mapbox/satellite-streets-v12"
            "streets"   -> "mapbox://styles/mapbox/streets-v12"
            "light"     -> "mapbox://styles/mapbox/light-v11"
            "dark"      -> "mapbox://styles/mapbox/dark-v11"
            else        -> "mapbox://styles/mapbox/outdoors-v12"
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "mapbox://styles/mapbox/outdoors-v12")

    // ── Layer toggles ──────────────────────────────────────────────
    private val _showCellularHeatmap = MutableStateFlow(false)
    val showCellularHeatmap: StateFlow<Boolean> = _showCellularHeatmap

    private val _showWaterSources = MutableStateFlow(false)
    val showWaterSources: StateFlow<Boolean> = _showWaterSources

    private val _showShelters = MutableStateFlow(false)
    val showShelters: StateFlow<Boolean> = _showShelters

    fun toggleCellularHeatmap() { _showCellularHeatmap.value = !_showCellularHeatmap.value }
    fun toggleWaterSources() { _showWaterSources.value = !_showWaterSources.value }
    fun toggleShelters() { _showShelters.value = !_showShelters.value }

    // ── Water sources ──────────────────────────────────────────────
    private val _waterSources = MutableStateFlow<List<WaterSourceUi>>(emptyList())
    val waterSources: StateFlow<List<WaterSourceUi>> = _waterSources

    private var waterLoadJob: Job? = null
    fun loadWaterForMap(lat: Double, lng: Double) {
        waterLoadJob?.cancel()
        waterLoadJob = viewModelScope.launch {
            delay(500L)
            when (val result = waterRepository.getNearbyWater(lat, lng)) {
                is Result.Success -> _waterSources.value = result.data
                is Result.Error   -> { }
                Result.Loading    -> Unit
            }
        }
    }

    // ── Cell towers ────────────────────────────────────────────────
    private val _cellTowers = MutableStateFlow<List<CellTowerUi>>(emptyList())
    val cellTowers: StateFlow<List<CellTowerUi>> = _cellTowers

    private var cellLoadJob: Job? = null
    @SuppressLint("MissingPermission")
    fun loadCellTowersForMap(lat: Double, lng: Double) {
        cellLoadJob?.cancel()
        cellLoadJob = viewModelScope.launch {
            delay(500L)
            when (val result = cellTowerRepository.getNearbyCellTowers(lat, lng)) {
                is Result.Success -> _cellTowers.value = result.data
                is Result.Error   -> { }
                Result.Loading    -> Unit
            }
        }
    }

    // ── Shelters ───────────────────────────────────────────────────
    private val _shelters = MutableStateFlow<List<ShelterUiItem>>(emptyList())
    val shelters: StateFlow<List<ShelterUiItem>> = _shelters

    private var shelterLoadJob: Job? = null
    fun loadSheltersForMap(lat: Double, lng: Double) {
        shelterLoadJob?.cancel()
        shelterLoadJob = viewModelScope.launch {
            delay(500L)
            when (val result = shelterRepository.getNearbyShelters(lat, lng)) {
                is Result.Success -> _shelters.value = result.data
                else              -> { }
            }
        }
    }

//    private var mapboxMap: MapboxMap? = null
//    private var locationJob: Job? = null
//
//    // Keeps the full location history
//    private val locationHistory = mutableListOf<Point>()
//
//    private val _currentLocation = MutableStateFlow<Location?>(null)
//    val currentLocation: StateFlow<Location?> = _currentLocation
//
//    fun onMapReady(map: MapboxMap) {
//        mapboxMap = map
//    }
//
//    // ─────────────────────────────────────────
//    // LOCATION TRACKING
//    // ─────────────────────────────────────────
//
//    fun startLocationTracking(context: Context, intervalMs: Long = 3000L) {
//        locationJob?.cancel()
//        val tracker = LocationTracker(context)
//
//        locationJob = viewModelScope.launch {
//            tracker.locationUpdates(intervalMs).collect { location ->
//                _currentLocation.value = location
//
//                val newPoint = Point.fromLngLat(location.longitude, location.latitude)
//                locationHistory.add(newPoint)
//
//                // Draw line between last two positions
//                if (locationHistory.size >= 2) {
//                    val last = locationHistory.takeLast(2)
//                    drawLocationLine(last)
//                }
//            }
//        }
//    }
//
//    fun stopLocationTracking() {
//        locationJob?.cancel()
//        locationJob = null
//    }
//
//    // ─────────────────────────────────────────
//    // LINE BETWEEN LAST TWO LOCATIONS
//    // ─────────────────────────────────────────
//    private fun drawLocationLine(points: List<Point>) {
//        viewModelScope.launch(Dispatchers.Main) {
//            mapboxMap?.getStyle { style ->
//                val sourceId = "location-line-source"
//                val layerId = "location-line-layer"
//
//                val existingSource = style.getSource(sourceId)
//
//                if (existingSource == null) {
//                    style.addSource(geoJsonSource(sourceId) {
//                        geometry(LineString.fromLngLats(points))
//                    })
//
//                    style.addLayer(lineLayer(layerId, sourceId) {
//                        lineColor("#2196F3")
//                        lineWidth(4.0)
//                        lineCap(LineCap.ROUND)
//                        lineJoin(LineJoin.ROUND)
//                    })
//                } else {
//                    val geoJsonSource = style.getSourceAs<GeoJsonSource>(sourceId)
//                    geoJsonSource?.geometry(LineString.fromLngLats(points))
//                }
//            }
//        }
//    }
////    private fun drawLocationLine(points: List<Point>) {
////        viewModelScope.launch(Dispatchers.Main) {
////            mapboxMap?.loadStyle(Style.MAPBOX_STREETS) { style ->
//////                clearLayer("location-line-layer", "location-line-source", style)
//////
//////                style.addSource(geoJsonSource("location-line-source") {
//////                    geometry(LineString.fromLngLats(points))
//////                })
//////                style.addLayer(lineLayer("location-line-layer", "location-line-source") {
//////                    lineColor("#2196F3")     // blue
//////                    lineWidth(4.0)
//////                    lineCap(LineCap.ROUND)
//////                    lineJoin(LineJoin.ROUND)
//////                })
////                val sourceId = "location-line-source"
////                val layerId = "location-line-layer"
////
////                val existingSource = style.getSource(sourceId)
////
////                if (existingSource == null) {
////                    style.addSource(geoJsonSource(sourceId) {
////                        geometry(LineString.fromLngLats(points))
////                    })
////
////                    style.addLayer(lineLayer(layerId, sourceId) {
////                        lineColor("#2196F3")
////                        lineWidth(4.0)
////                        lineCap(LineCap.ROUND)
////                        lineJoin(LineJoin.ROUND)
////                    })
////                } else {
////                    val geoJsonSource = style.getSourceAs<GeoJsonSource>(sourceId)
////                    geoJsonSource?.geometry(LineString.fromLngLats(points))
////                }
////            }
////        }
////    }
//
//    // Draw the FULL traveled path instead of just last two points
//    fun drawFullPath() {
//        if (locationHistory.size < 2) return
//        viewModelScope.launch(Dispatchers.Main) {
//            mapboxMap?.getStyle { style ->
//                clearLayer("location-line-layer", "location-line-source", style)
//                style.addSource(geoJsonSource("location-line-source") {
//                    geometry(LineString.fromLngLats(locationHistory))
//                })
//                style.addLayer(lineLayer("location-line-layer", "location-line-source") {
//                    lineColor("#2196F3")
//                    lineWidth(4.0)
//                    lineCap(LineCap.ROUND)
//                    lineJoin(LineJoin.ROUND)
//                })
//            }
//        }
//    }
//
//    fun clearLocationPath() = removeOverlay("location-line-layer", "location-line-source")
//    fun clearLocationHistory() { locationHistory.clear() }
//
//    // ─────────────────────────────────────────
//    // ... rest of your existing drawLine,
//    //     drawHeatmap, clearAll etc.
//    // ─────────────────────────────────────────
//
//    private fun removeOverlay(layerId: String, sourceId: String?) {
//        viewModelScope.launch(Dispatchers.Main) {
//            mapboxMap?.getStyle { style -> clearLayer(layerId, sourceId, style) }
//        }
//    }
//
//    private fun clearLayer(layerId: String, sourceId: String?, style: com.mapbox.maps.Style) {
//        if (style.styleLayerExists(layerId)) style.removeStyleLayer(layerId)
//        if (sourceId != null && style.styleSourceExists(sourceId)) style.removeStyleSource(sourceId)
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        stopLocationTracking()
//        mapboxMap = null
//    }
}