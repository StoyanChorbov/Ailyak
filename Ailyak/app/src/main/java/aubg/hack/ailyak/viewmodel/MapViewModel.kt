package aubg.hack.ailyak.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aubg.hack.ailyak.data.model.Result
import aubg.hack.ailyak.db.model.PathPointEntity
import aubg.hack.ailyak.db.repository.PathHistoryRepository
import aubg.hack.ailyak.https.NetworkMonitor
import aubg.hack.ailyak.ui.survivalguide.data.CellTowerRepository
import aubg.hack.ailyak.data.model.CellTowerUi
import aubg.hack.ailyak.data.model.UserPreferencesDataStore
import aubg.hack.ailyak.ui.survivalguide.data.ShelterRepository
import aubg.hack.ailyak.data.model.ShelterUiItem
import aubg.hack.ailyak.ui.survivalguide.data.WaterRepository
import aubg.hack.ailyak.data.model.WaterSourceUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    networkMonitor: NetworkMonitor,
    pathHistoryRepository: PathHistoryRepository,
    private val waterRepository: WaterRepository,
    private val cellTowerRepository: CellTowerRepository,
    private val shelterRepository: ShelterRepository,
    private val prefs: UserPreferencesDataStore
) : ViewModel() {

    // ── Connectivity ───────────────────────────────────────────────
    val isConnected = networkMonitor.isConnected
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    // ── Path tracking ──────────────────────────────────────────────
    val pathPoints: StateFlow<List<PathPointEntity>> = pathHistoryRepository.observeAllPoints()
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

    fun downloadDataForArea(lat: Double, lon: Double, radiusMeters: Double) {
        viewModelScope.launch {

            // You likely already have these methods — reuse them
            loadWaterForMap(lat, lon)
            loadCellTowersForMap(lat, lon)
            loadSheltersForMap(lat, lon)

            // If your API supports radius → pass it
            // Otherwise you'll filter locally later

            // Optional: persist locally here (DB / file)
        }
    }
}