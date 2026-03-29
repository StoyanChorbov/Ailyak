package aubg.hack.ailyak.db

import androidx.room.Database
import androidx.room.RoomDatabase
import aubg.hack.ailyak.db.dao.CachedAnimalDao
import aubg.hack.ailyak.db.dao.CachedPlantDao
import aubg.hack.ailyak.db.dao.CachedWaterDao
import aubg.hack.ailyak.db.dao.PathPointDao
import aubg.hack.ailyak.db.model.CachedPlantEntity
import aubg.hack.ailyak.db.model.CachedWaterEntity
import aubg.hack.ailyak.db.model.PathPointEntity
import aubg.hack.ailyak.db.model.CachedAnimalEntity

@Database(
    entities = [
        PathPointEntity::class,
        CachedPlantEntity::class,
        CachedWaterEntity::class,
        CachedAnimalEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pathPointDao(): PathPointDao
    abstract fun cachedPlantDao(): CachedPlantDao
    abstract fun cachedWaterDao(): CachedWaterDao

    abstract fun cachedAnimalDao(): CachedAnimalDao
}