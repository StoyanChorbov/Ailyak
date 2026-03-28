package aubg.hack.ailyak.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class PlantSafetyEntity(
    @PrimaryKey val species: String,
    val commonName: String?,
    val lat: Double?,
    val lon: Double?,
    val isEdible: Boolean?,
    val isPoisonous: Boolean?,
    val region: String?
)