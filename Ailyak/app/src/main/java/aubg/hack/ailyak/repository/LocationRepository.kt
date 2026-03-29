package aubg.hack.ailyak.repository
import android.location.Location
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor() {
    private val _locations = MutableSharedFlow<Location>(replay = 1)
    val locations: SharedFlow<Location> = _locations
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { _locations.tryEmit(it) }
        }
    }
}
