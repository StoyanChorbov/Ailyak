package aubg.hack.ailyak.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_plants")
data class PlantEntity(
    @PrimaryKey val id: Long,
    val scientificName: String,
    val commonName: String?,
    val photoUrl: String?,
    val photoAttribution: String?,
    val wikipediaUrl: String?,
    val isFungus: Boolean,
    val safetyLevel: String,
    val observationCount: Int,
    val rank: String?,
    val cachedLat: Double,
    val cachedLng: Double,
    val cachedAtMs: Long = System.currentTimeMillis()
)