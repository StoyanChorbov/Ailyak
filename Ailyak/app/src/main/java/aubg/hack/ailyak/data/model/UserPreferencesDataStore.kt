package aubg.hack.ailyak.data.model

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.text.get

val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val DARK_THEME              = booleanPreferencesKey("dark_theme")
        val BATTERY_SAVER           = booleanPreferencesKey("battery_saver")
        val MAP_STYLE               = stringPreferencesKey("map_style")
        val EMERGENCY_CONTACT_NAME  = stringPreferencesKey("emergency_contact_name")
        val EMERGENCY_CONTACT_PHONE = stringPreferencesKey("emergency_contact_phone")
        val TRACKING_INTERVAL_MIN   = intPreferencesKey("tracking_interval_min")
        val NOTIFY_NO_CONNECTION    = booleanPreferencesKey("notify_no_connection")
        val NOTIFY_NO_GPS           = booleanPreferencesKey("notify_no_gps")
        val SEARCH_RADIUS_KM        = intPreferencesKey("search_radius_km")
    }

    val darkTheme             = context.dataStore.data.map { it[DARK_THEME] ?: false }
    val batterySaver          = context.dataStore.data.map { it[BATTERY_SAVER] ?: false }
    val mapStyle              = context.dataStore.data.map { it[MAP_STYLE] ?: "outdoors" }
    val emergencyContactName  = context.dataStore.data.map { it[EMERGENCY_CONTACT_NAME] ?: "" }
    val emergencyContactPhone = context.dataStore.data.map { it[EMERGENCY_CONTACT_PHONE] ?: "" }
    val trackingIntervalMin   = context.dataStore.data.map { it[TRACKING_INTERVAL_MIN] ?: 3 }
    val notifyNoConnection    = context.dataStore.data.map { it[NOTIFY_NO_CONNECTION] ?: true }
    val notifyNoGps           = context.dataStore.data.map { it[NOTIFY_NO_GPS] ?: true }
    val searchRadiusKm        = context.dataStore.data.map { it[SEARCH_RADIUS_KM] ?: 10 }

    suspend fun set(key: Preferences.Key<Boolean>, value: Boolean) {
        context.dataStore.edit { it[key] = value }
    }
    suspend fun set(key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { it[key] = value }
    }
    suspend fun set(key: Preferences.Key<Int>, value: Int) {
        context.dataStore.edit { it[key] = value }
    }
}