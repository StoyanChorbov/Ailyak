package aubg.hack.ailyak.db

import androidx.room.Database
import androidx.room.RoomDatabase
import aubg.hack.ailyak.db.dao.CoverageDao
import aubg.hack.ailyak.db.model.CoverageCellEntity
import aubg.hack.ailyak.db.model.PlantSafetyEntity
import aubg.hack.ailyak.db.model.WaterSourceEntity

@Database(
    entities = [
        CoverageCellEntity::class,
        PlantSafetyEntity::class,
        WaterSourceEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coverageDao(): CoverageDao
}