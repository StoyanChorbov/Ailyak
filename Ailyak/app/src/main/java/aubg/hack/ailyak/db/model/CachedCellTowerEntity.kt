package aubg.hack.ailyak.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import aubg.hack.ailyak.data.model.CellRadioType

@Entity(tableName = "cached_cell_towers")
data class CachedCellTowerEntity(
    @PrimaryKey val id: Long,
    val lat: Double,
    val lng: Double,
    val radio: CellRadioType,
    val signalStrength: Int?,
    val rangeMeters: Int?,
    val samples: Int?,
    val distanceMeters: Double,
    val cachedLat: Double,
    val cachedLng: Double,
    val cachedAtMs: Long = System.currentTimeMillis()
)