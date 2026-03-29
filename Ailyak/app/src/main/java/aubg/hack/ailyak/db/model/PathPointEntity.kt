package aubg.hack.ailyak.db.model
import androidx.room.*

@Entity(tableName = "path_points")
data class PathPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lat: Double,
    val lng: Double,
    val timestamp: Long,
    val hadConnection: Boolean = true
)
