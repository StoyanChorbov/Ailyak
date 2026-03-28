package aubg.hack.ailyak.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import aubg.hack.ailyak.db.model.CoverageCellEntity

@Dao
interface CoverageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cells: List<CoverageCellEntity>)

    @Query("SELECT * FROM coverage_cells")
    suspend fun getAll(): List<CoverageCellEntity>

    @Query("SELECT * FROM coverage_cells WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLon AND :maxLon")
    suspend fun getInArea(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ): List<CoverageCellEntity>
}