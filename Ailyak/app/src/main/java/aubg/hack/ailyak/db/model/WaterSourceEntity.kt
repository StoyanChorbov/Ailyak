package aubg.hack.ailyak.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_sources")
data class WaterSourceEntity(
    @PrimaryKey val id: Long,
    val type: String,
    val name: String?,
    val lat: Double,
    val lon: Double,
    val operator: String?,
    val access: String?,
    val seasonal: String?,
    val description: String?
)