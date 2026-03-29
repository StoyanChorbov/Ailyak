package aubg.hack.ailyak.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_water")
data class CachedWaterEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val type: String,
    val lat: Double,
    val lng: Double,
    val drinkable: String,
    val distanceMeters: Double,
    val cachedLat: Double,
    val cachedLng: Double,
    val cachedAtMs: Long = System.currentTimeMillis()
)