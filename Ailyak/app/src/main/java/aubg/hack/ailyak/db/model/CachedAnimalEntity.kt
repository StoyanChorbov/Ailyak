package aubg.hack.ailyak.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_animals")
data class CachedAnimalEntity(
    @PrimaryKey val id: Long,
    val scientificName: String,
    val commonName: String?,
    val photoUrl: String?,
    val kingdom: String?,
    val order: String?,
    val family: String?,
    val iucnCategory: String,
    val distanceMeters: Double,
    val animalGroup: String,
    val cachedLat: Double,
    val cachedLng: Double,
    val cachedAtMs: Long = System.currentTimeMillis()
)