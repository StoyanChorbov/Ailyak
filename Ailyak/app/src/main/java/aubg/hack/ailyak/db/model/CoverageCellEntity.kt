package aubg.hack.ailyak.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coverage_cells")
data class CoverageCellEntity(
    @PrimaryKey val id: Int,
    val longitude: Double,
    val latitude: Double,
    val mobileCountryCode: Int,
    val mobileNetworkCode: Int,
    val localAreaCode: Int,
    val averageSignalStrength: Double,
    val range: Double,
    val isChangeable: Boolean,
    val radioType: String
)