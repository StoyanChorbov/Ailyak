package aubg.hack.ailyak.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import aubg.hack.ailyak.data.model.CoverageCell
import aubg.hack.ailyak.data.model.CoverageCellParams
import aubg.hack.ailyak.service.CoverageCellService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CoverageCellState {
    object Idle    : CoverageCellState()
    object Loading : CoverageCellState()
    data class Success(val cells: List<CoverageCell>, val count: Int) : CoverageCellState()
    data class Error(val message: String)                             : CoverageCellState()
}

class CoverageCellViewModel(context: Context) : ViewModel() {

    private val cellService = CoverageCellService()

    private val _state = MutableStateFlow<CoverageCellState>(CoverageCellState.Idle)
    val state: StateFlow<CoverageCellState> = _state

    fun loadCellTowers(
        latMin: Double,
        lonMin: Double,
        latMax: Double,
        lonMax: Double,
        mcc: Int? = null,
        mnc: Int? = null,
        lac: Int? = null
    ) {
        val params = CoverageCellParams(
            latituteMin        = latMin,
            longitudeMin       = lonMin,
            latituteMax        = latMax,
            longitudeMax       = lonMax,
            mobileCountryCode  = mcc,
            mobileNetworkCode  = mnc,
            localAreaCode      = lac
        )

        viewModelScope.launch {
            _state.value = CoverageCellState.Loading
            _state.value = runCatching {
                val result = cellService.fetchCellTowersInArea(params).getOrThrow()
                CoverageCellState.Success(
                    cells = result.cells,
                    count = result.cellsCount
                )
            }.getOrElse {
                CoverageCellState.Error(it.message ?: "Unknown error")
            }
        }
    }

    fun reset() {
        _state.value = CoverageCellState.Idle
    }

    companion object {
        fun factory(context: Context) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CoverageCellViewModel(context) as T
            }
        }
    }
}