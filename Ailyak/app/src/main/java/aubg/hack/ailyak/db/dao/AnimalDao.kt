package aubg.hack.ailyak.db.dao

import androidx.room.*
import aubg.hack.ailyak.db.model.AnimalEntity

@Dao
interface AnimalDao {
    @Query("SELECT * FROM cached_animals")
    suspend fun getAll(): List<AnimalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AnimalEntity>)

    @Query("DELETE FROM cached_animals")
    suspend fun clearAll()

    @Query("SELECT * FROM cached_animals WHERE cachedAtMs > :since")
    suspend fun getFresh(since: Long): List<AnimalEntity>
}