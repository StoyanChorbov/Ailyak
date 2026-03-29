package aubg.hack.ailyak.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aubg.hack.ailyak.data.model.Result
import aubg.hack.ailyak.ui.survivalguide.data.PlantRepository
import aubg.hack.ailyak.data.model.PlantUiItem
import aubg.hack.ailyak.data.model.SafetyLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PlantFilter { ALL, PLANTS, MUSHROOMS }
enum class SafetyFilter { ALL, EDIBLE, CAUTION, TOXIC, UNKNOWN }

data class PlantIndexUiState(
    val allItems: List<PlantUiItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filter: PlantFilter = PlantFilter.ALL,
    val safetyFilter: SafetyFilter = SafetyFilter.ALL,
    val searchQuery: String = "",
    val expandedId: Long? = null,
    val loadingSummaryId: Long? = null,
    val summaryCache: Map<Long, String?> = emptyMap()
)

@HiltViewModel
class PlantViewModel @Inject constructor(
    private val repository: PlantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlantIndexUiState())
    val uiState: StateFlow<PlantIndexUiState> = _uiState.asStateFlow()

    val filteredItems: StateFlow<List<PlantUiItem>> = _uiState.map { state ->
        state.allItems
            .filter { item ->
                when (state.filter) {
                    PlantFilter.ALL -> true
                    PlantFilter.PLANTS -> !item.isFungus
                    PlantFilter.MUSHROOMS -> item.isFungus
                }
            }
            .filter { item ->
                when (state.safetyFilter) {
                    SafetyFilter.ALL     -> true
                    SafetyFilter.EDIBLE  -> item.safetyLevel == SafetyLevel.EDIBLE
                    SafetyFilter.CAUTION -> item.safetyLevel == SafetyLevel.CAUTION
                    SafetyFilter.TOXIC   -> item.safetyLevel == SafetyLevel.TOXIC
                    SafetyFilter.UNKNOWN -> item.safetyLevel == SafetyLevel.UNKNOWN
                }
            }
            .filter { item ->
                val q = state.searchQuery.trim()
                if (q.isBlank()) true
                else item.commonName?.contains(q, ignoreCase = true) == true
                        || item.scientificName.contains(q, ignoreCase = true)
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    fun loadPlants(location: Location) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getNearbyPlants(location.latitude, location.longitude)) {
                is Result.Success -> _uiState.update {
                    it.copy(allItems = result.data, isLoading = false)
                }
                is Result.Error -> _uiState.update {
                    it.copy(error = result.message, isLoading = false)
                }
                Result.Loading -> Unit
            }
        }
    }

    fun toggleExpanded(item: PlantUiItem) {
        val currentExpanded = _uiState.value.expandedId
        val newId = if (currentExpanded == item.id) null else item.id

        _uiState.update { it.copy(expandedId = newId) }

        // Fetch summary if not cached yet
        if (newId != null && !_uiState.value.summaryCache.containsKey(item.id)) {
            viewModelScope.launch {
                _uiState.update { it.copy(loadingSummaryId = item.id) }
                val summary = repository.getTaxonSummary(item.id)
                _uiState.update { state ->
                    state.copy(
                        summaryCache = state.summaryCache + (item.id to summary),
                        loadingSummaryId = null
                    )
                }
            }
        }
    }

    fun setSafetyFilter(filter: SafetyFilter) = _uiState.update { it.copy(safetyFilter = filter) }
    fun setFilter(filter: PlantFilter) = _uiState.update { it.copy(filter = filter) }
    fun setSearch(query: String) = _uiState.update { it.copy(searchQuery = query) }
}