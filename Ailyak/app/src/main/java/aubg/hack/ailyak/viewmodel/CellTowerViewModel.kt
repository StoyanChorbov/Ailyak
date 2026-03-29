package aubg.hack.ailyak.viewmodel

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aubg.hack.ailyak.data.model.Result
import aubg.hack.ailyak.data.model.CellRadioType
import aubg.hack.ailyak.ui.survivalguide.data.CellTowerRepository
import aubg.hack.ailyak.data.model.CellTowerUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CellTowerUiState(
    val allItems: List<CellTowerUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filter: CellRadioType? = null   // null = ALL
)

@HiltViewModel
class CellTowerViewModel @Inject constructor(
    private val repository: CellTowerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CellTowerUiState())
    val uiState: StateFlow<CellTowerUiState> = _uiState.asStateFlow()

    val filteredItems: StateFlow<List<CellTowerUi>> = _uiState.map { state ->
        state.allItems.filter { tower ->
            state.filter == null || tower.radio == state.filter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Also expose for MapViewModel
    val towers: StateFlow<List<CellTowerUi>> = _uiState
        .map { it.allItems }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @SuppressLint("MissingPermission")
    fun load(location: Location) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result =
                repository.getNearbyCellTowers(location.latitude, location.longitude)) {
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

    fun setFilter(filter: CellRadioType?) =
        _uiState.update { it.copy(filter = filter) }
}