package aubg.hack.ailyak.db.dao

import androidx.room.*
import aubg.hack.ailyak.db.model.CachedWaterEntity

@Dao
interface CachedWaterDao {
    @Query("SELECT * FROM cached_water")
    suspend fun getAll(): List<CachedWaterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CachedWaterEntity>)

    @Query("DELETE FROM cached_water")
    suspend fun clearAll()

    @Query("SELECT * FROM cached_water WHERE cachedAtMs > :since")
    suspend fun getFresh(since: Long): List<CachedWaterEntity>
}