package aubg.hack.ailyak.viewmodel

import androidx.lifecycle.ViewModel
import aubg.hack.ailyak.service.LocationService

class MapViewModel(
    private val locationService: LocationService
) : ViewModel() {
    fun updateLocation() {}
}