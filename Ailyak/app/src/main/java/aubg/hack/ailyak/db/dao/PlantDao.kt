package aubg.hack.ailyak.db.dao

import androidx.room.*
import aubg.hack.ailyak.db.model.PlantEntity

@Dao
interface PlantDao {
    @Query("SELECT * FROM cached_plants")
    suspend fun getAll(): List<PlantEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<PlantEntity>)

    @Query("DELETE FROM cached_plants")
    suspend fun clearAll()

    @Query("SELECT * FROM cached_plants WHERE cachedAtMs > :since")
    suspend fun getFresh(since: Long): List<PlantEntity>
}