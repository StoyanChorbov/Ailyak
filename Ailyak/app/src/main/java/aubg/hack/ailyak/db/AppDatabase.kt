package aubg.hack.ailyak.db

import androidx.room.Database
import androidx.room.RoomDatabase
import aubg.hack.ailyak.db.dao.AnimalDao
import aubg.hack.ailyak.db.dao.CoverageDao
import aubg.hack.ailyak.db.dao.PathPointDao
import aubg.hack.ailyak.db.dao.PlantDao
import aubg.hack.ailyak.db.dao.WaterDao
import aubg.hack.ailyak.db.model.CoverageCellEntity
import aubg.hack.ailyak.db.model.AnimalEntity
import aubg.hack.ailyak.db.model.PathPointEntity
import aubg.hack.ailyak.db.model.PlantEntity
import aubg.hack.ailyak.db.model.WaterEntity

@Database(
    entities = [
        CoverageCellEntity::class,
        PathPointEntity::class,
        PlantEntity::class,
        WaterEntity::class,
        AnimalEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coverageDao(): CoverageDao
    abstract fun pathPointDao(): PathPointDao
    abstract fun plantDao(): PlantDao
    abstract fun waterDao(): WaterDao
    abstract fun animalDao(): AnimalDao
}