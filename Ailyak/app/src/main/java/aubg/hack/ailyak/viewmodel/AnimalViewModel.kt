package aubg.hack.ailyak.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aubg.hack.ailyak.data.model.Result
import aubg.hack.ailyak.data.model.AnimalGroup
import aubg.hack.ailyak.ui.survivalguide.data.AnimalRepository
import aubg.hack.ailyak.data.model.AnimalUiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AnimalFilter { ALL, MAMMAL, BIRD, REPTILE, AMPHIBIAN, INSECT, FISH }

data class AnimalUiState(
    val allItems: List<AnimalUiItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filter: AnimalFilter = AnimalFilter.ALL,
    val searchQuery: String = "",
    val expandedId: Long? = null
)

@HiltViewModel
class AnimalViewModel @Inject constructor(
    private val repository: AnimalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnimalUiState())
    val uiState: StateFlow<AnimalUiState> = _uiState.asStateFlow()

    val filteredItems: StateFlow<List<AnimalUiItem>> = _uiState.map { state ->
        state.allItems
            .filter { item ->
                when (state.filter) {
                    AnimalFilter.ALL       -> true
                    AnimalFilter.MAMMAL    -> item.animalGroup == AnimalGroup.MAMMAL
                    AnimalFilter.BIRD      -> item.animalGroup == AnimalGroup.BIRD
                    AnimalFilter.REPTILE   -> item.animalGroup == AnimalGroup.REPTILE
                    AnimalFilter.AMPHIBIAN -> item.animalGroup == AnimalGroup.AMPHIBIAN
                    AnimalFilter.INSECT    -> item.animalGroup == AnimalGroup.INSECT
                    AnimalFilter.FISH      -> item.animalGroup == AnimalGroup.FISH
                }
            }
            .filter { item ->
                val q = state.searchQuery.trim()
                if (q.isBlank()) true
                else item.commonName?.contains(q, ignoreCase = true) == true
                        || item.scientificName.contains(q, ignoreCase = true)
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun loadAnimals(location: Location) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getNearbyAnimals(location.latitude, location.longitude)) {
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

    fun toggleExpanded(id: Long) = _uiState.update {
        it.copy(expandedId = if (it.expandedId == id) null else id)
    }

    fun setFilter(filter: AnimalFilter) = _uiState.update { it.copy(filter = filter) }
    fun setSearch(query: String) = _uiState.update { it.copy(searchQuery = query) }
}