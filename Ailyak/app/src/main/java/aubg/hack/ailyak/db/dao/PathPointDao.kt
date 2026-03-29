package aubg.hack.ailyak.db.dao
import androidx.room.*
import aubg.hack.ailyak.db.model.EncryptedPathPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PathPointDao {
    @Query("SELECT * FROM path_points ORDER BY id ASC")
    fun getAllPoints(): Flow<List<EncryptedPathPointEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoint(point: EncryptedPathPointEntity)
    @Query("DELETE FROM path_points")
    suspend fun clearAll()
}
