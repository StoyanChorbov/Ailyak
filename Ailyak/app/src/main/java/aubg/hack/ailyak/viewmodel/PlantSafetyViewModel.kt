package aubg.hack.ailyak.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aubg.hack.ailyak.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PlantSafetyState {
    object Idle    : PlantSafetyState()
    object Loading : PlantSafetyState()
    data class Success(val plants: List<PlantSafetyInfo>) : PlantSafetyState()
    data class Error(val message: String)                 : PlantSafetyState()
}

class PlantSafetyViewModel : ViewModel() {

    private val _state = MutableStateFlow<PlantSafetyState>(PlantSafetyState.Idle)
    val state: StateFlow<PlantSafetyState> = _state

    fun loadSafePlants(latitude: Double, longitude: Double, radiusKm: Double = 50.0) {
        viewModelScope.launch {
            _state.value = PlantSafetyState.Loading
            _state.value = runCatching {
                PlantSafetyState.Success(
                    PlantSafetyService.getSafePlantsNearby(latitude, longitude, radiusKm)
                )
            }.getOrElse {
                PlantSafetyState.Error(it.message ?: "Unknown error")
            }
        }
    }

    fun loadAllPlants(latitude: Double, longitude: Double, radiusKm: Double = 50.0) {
        viewModelScope.launch {
            _state.value = PlantSafetyState.Loading
            _state.value = runCatching {
                PlantSafetyState.Success(
                    PlantSafetyService.getPlantSafetyInfoNearby(latitude, longitude, radiusKm)
                )
            }.getOrElse {
                PlantSafetyState.Error(it.message ?: "Unknown error")
            }
        }
    }}