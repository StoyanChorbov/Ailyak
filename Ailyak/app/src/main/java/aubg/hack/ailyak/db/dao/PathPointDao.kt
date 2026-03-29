package aubg.hack.ailyak.db.dao
import androidx.room.*
import aubg.hack.ailyak.db.model.PathPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PathPointDao {
    @Query("SELECT * FROM path_points ORDER BY timestamp ASC")
    fun getAllPoints(): Flow<List<PathPointEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoint(point: PathPointEntity)
    @Query("DELETE FROM path_points")
    suspend fun clearAll()
}
