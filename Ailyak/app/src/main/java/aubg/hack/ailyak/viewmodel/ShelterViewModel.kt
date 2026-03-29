package aubg.hack.ailyak.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aubg.hack.ailyak.data.model.Result
import aubg.hack.ailyak.ui.survivalguide.data.ShelterRepository
import aubg.hack.ailyak.data.model.ShelterType
import aubg.hack.ailyak.data.model.ShelterUiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShelterUiState(
    val allItems: List<ShelterUiItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filter: ShelterType? = null,
    val expandedId: Long? = null
)

@HiltViewModel
class ShelterViewModel @Inject constructor(
    private val repository: ShelterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShelterUiState())
    val uiState: StateFlow<ShelterUiState> = _uiState.asStateFlow()

    val filteredItems: StateFlow<List<ShelterUiItem>> = _uiState.map { state ->
        state.allItems.filter { item ->
            state.filter == null || item.type == state.filter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun load(location: Location) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result =
                repository.getNearbyShelters(location.latitude, location.longitude)) {
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

    fun setFilter(type: ShelterType?) = _uiState.update { it.copy(filter = type) }
    fun toggleExpanded(id: Long) = _uiState.update {
        it.copy(expandedId = if (it.expandedId == id) null else id)
    }
}