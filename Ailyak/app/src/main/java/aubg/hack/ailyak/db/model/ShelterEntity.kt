package aubg.hack.ailyak.db.model
import androidx.room.*

@Entity(tableName = "shelters")
data class ShelterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val lat: Double,
    val lng: Double,
    val isManual: Boolean = false
)
