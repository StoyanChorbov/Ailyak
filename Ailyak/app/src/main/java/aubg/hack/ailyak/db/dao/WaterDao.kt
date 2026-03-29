package aubg.hack.ailyak.db.dao

import androidx.room.*
import aubg.hack.ailyak.db.model.WaterEntity

@Dao
interface WaterDao {
    @Query("SELECT * FROM cached_water")
    suspend fun getAll(): List<WaterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WaterEntity>)

    @Query("DELETE FROM cached_water")
    suspend fun clearAll()

    @Query("SELECT * FROM cached_water WHERE cachedAtMs > :since")
    suspend fun getFresh(since: Long): List<WaterEntity>
}