package aubg.hack.ailyak.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aubg.hack.ailyak.data.model.PlantSafetyInfo
import aubg.hack.ailyak.service.GbifService
import aubg.hack.ailyak.service.PerenualService
import aubg.hack.ailyak.service.PlantSafetyService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PlantSafetyState {
    object Idle : PlantSafetyState()
    object Loading : PlantSafetyState()
    data class Success(val plants: List<PlantSafetyInfo>) : PlantSafetyState()
    data class Error(val message: String) : PlantSafetyState()
}

class PlantSafetyViewModel : ViewModel() {

    private val gbifService        = GbifService()
    private val perenualService    = PerenualService(apiKey = "YOUR_PERENUAL_API_KEY")
    private val plantSafetyService = PlantSafetyService(gbifService, perenualService)

    private val _state = MutableStateFlow<PlantSafetyState>(PlantSafetyState.Idle)
    val state: StateFlow<PlantSafetyState> = _state

    fun loadSafePlants(countryCode: String) {
        viewModelScope.launch {
            _state.value = PlantSafetyState.Loading
            _state.value = runCatching {
                PlantSafetyState.Success(plantSafetyService.getSafePlantsInCountry(countryCode))
            }.getOrElse {
                PlantSafetyState.Error(it.message ?: "Unknown error")
            }
        }
    }

    fun loadAllPlants(countryCode: String) {
        viewModelScope.launch {
            _state.value = PlantSafetyState.Loading
            _state.value = runCatching {
                PlantSafetyState.Success(plantSafetyService.getPlantSafetyInfoForCountry(countryCode))
            }.getOrElse {
                PlantSafetyState.Error(it.message ?: "Unknown error")
            }
        }
    }
}
