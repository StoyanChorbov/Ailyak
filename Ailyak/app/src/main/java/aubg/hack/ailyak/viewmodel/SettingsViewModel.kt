package aubg.hack.ailyak.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aubg.hack.ailyak.data.model.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val darkTheme: Boolean = false,
    val batterySaver: Boolean = false,
    val mapStyle: String = "outdoors",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val trackingIntervalMin: Int = 3,
    val notifyNoConnection: Boolean = true,
    val notifyNoGps: Boolean = true,
    val searchRadiusKm: Int = 10
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: UserPreferencesDataStore
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        prefs.darkTheme,
        prefs.batterySaver,
        prefs.mapStyle,
        prefs.emergencyContactName,
        prefs.emergencyContactPhone,
        prefs.trackingIntervalMin,
        prefs.notifyNoConnection,
        prefs.notifyNoGps,
        prefs.searchRadiusKm
    ) { values ->
        SettingsUiState(
            darkTheme             = values[0] as Boolean,
            batterySaver          = values[1] as Boolean,
            mapStyle              = values[2] as String,
            emergencyContactName  = values[3] as String,
            emergencyContactPhone = values[4] as String,
            trackingIntervalMin   = values[5] as Int,
            notifyNoConnection    = values[6] as Boolean,
            notifyNoGps           = values[7] as Boolean,
            searchRadiusKm        = values[8] as Int
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun setDarkTheme(v: Boolean)           = save { prefs.set(UserPreferencesDataStore.DARK_THEME, v) }
    fun setBatterySaver(v: Boolean)        = save { prefs.set(UserPreferencesDataStore.BATTERY_SAVER, v) }
    fun setMapStyle(v: String)             = save { prefs.set(UserPreferencesDataStore.MAP_STYLE, v) }
    fun setEmergencyName(v: String)        = save { prefs.set(UserPreferencesDataStore.EMERGENCY_CONTACT_NAME, v) }
    fun setEmergencyPhone(v: String)       = save { prefs.set(UserPreferencesDataStore.EMERGENCY_CONTACT_PHONE, v) }
    fun setTrackingInterval(v: Int)        = save { prefs.set(UserPreferencesDataStore.TRACKING_INTERVAL_MIN, v) }
    fun setNotifyNoConnection(v: Boolean)  = save { prefs.set(UserPreferencesDataStore.NOTIFY_NO_CONNECTION, v) }
    fun setNotifyNoGps(v: Boolean)         = save { prefs.set(UserPreferencesDataStore.NOTIFY_NO_GPS, v) }
    fun setSearchRadius(v: Int)            = save { prefs.set(UserPreferencesDataStore.SEARCH_RADIUS_KM, v) }

    private fun save(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}