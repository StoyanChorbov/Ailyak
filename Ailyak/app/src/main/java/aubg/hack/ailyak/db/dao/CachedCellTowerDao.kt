package aubg.hack.ailyak.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import aubg.hack.ailyak.db.model.CachedCellTowerEntity

@Dao
interface CachedCellTowerDao {

    @Query("SELECT * FROM cached_cell_towers")
    suspend fun getAll(): List<CachedCellTowerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CachedCellTowerEntity>)

    @Query("DELETE FROM cached_cell_towers")
    suspend fun clearAll()

    @Query("SELECT * FROM cached_cell_towers WHERE cachedAtMs > :since")
    suspend fun getCached(since: Long): List<CachedCellTowerEntity>
}