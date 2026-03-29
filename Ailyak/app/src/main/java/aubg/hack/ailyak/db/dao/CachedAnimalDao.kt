package aubg.hack.ailyak.db.dao

import androidx.room.*
import aubg.hack.ailyak.db.model.CachedAnimalEntity

@Dao
interface CachedAnimalDao {
    @Query("SELECT * FROM cached_animals")
    suspend fun getAll(): List<CachedAnimalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CachedAnimalEntity>)

    @Query("DELETE FROM cached_animals")
    suspend fun clearAll()

    @Query("SELECT * FROM cached_animals WHERE cachedAtMs > :since")
    suspend fun getFresh(since: Long): List<CachedAnimalEntity>
}