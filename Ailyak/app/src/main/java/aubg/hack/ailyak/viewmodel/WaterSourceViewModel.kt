package aubg.hack.ailyak.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aubg.hack.ailyak.data.model.WaterSource
import aubg.hack.ailyak.service.WaterSourceService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import aubg.hack.ailyak.WaterSourceConstants

sealed class WaterSourceState {
    object Idle    : WaterSourceState()
    object Loading : WaterSourceState()
    data class Success(val sources: List<WaterSource>) : WaterSourceState()
    data class Error(val message: String)              : WaterSourceState()
}

class WaterSourceViewModel : ViewModel() {

    private val waterSourceService = WaterSourceService()

    private val _state = MutableStateFlow<WaterSourceState>(WaterSourceState.Idle)
    val state: StateFlow<WaterSourceState> = _state

    fun loadAllWaterSources(lat: Double, lon: Double, radiusMetres: Int = WaterSourceConstants.defaultRadius) {
        viewModelScope.launch {
            _state.value = WaterSourceState.Loading
            _state.value = runCatching {
                WaterSourceState.Success(
                    waterSourceService.getWaterSourcesNearby(lat, lon, radiusMetres).getOrThrow()
                )
            }.getOrElse {
                WaterSourceState.Error(it.message ?: "Unknown error")
            }
        }
    }

    fun loadDrinkingWater(lat: Double, lon: Double, radiusMetres: Int = WaterSourceConstants.defaultRadius) {
        viewModelScope.launch {
            _state.value = WaterSourceState.Loading
            _state.value = runCatching {
                WaterSourceState.Success(
                    waterSourceService.getDrinkingWaterNearby(lat, lon, radiusMetres).getOrThrow()
                )
            }.getOrElse {
                WaterSourceState.Error(it.message ?: "Unknown error")
            }
        }
    }

    fun loadSprings(lat: Double, lon: Double, radiusMetres: Int = WaterSourceConstants.defaultRadius) {
        viewModelScope.launch {
            _state.value = WaterSourceState.Loading
            _state.value = runCatching {
                WaterSourceState.Success(
                    waterSourceService.getSpringsNearby(lat, lon, radiusMetres).getOrThrow()
                )
            }.getOrElse {
                WaterSourceState.Error(it.message ?: "Unknown error")
            }
        }
    }
}