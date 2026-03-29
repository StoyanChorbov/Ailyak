package aubg.hack.ailyak.db.dao

import androidx.room.*
import aubg.hack.ailyak.db.model.CachedPlantEntity

@Dao
interface CachedPlantDao {
    @Query("SELECT * FROM cached_plants")
    suspend fun getAll(): List<CachedPlantEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<CachedPlantEntity>)

    @Query("DELETE FROM cached_plants")
    suspend fun clearAll()

    @Query("SELECT * FROM cached_plants WHERE cachedAtMs > :since")
    suspend fun getFresh(since: Long): List<CachedPlantEntity>
}