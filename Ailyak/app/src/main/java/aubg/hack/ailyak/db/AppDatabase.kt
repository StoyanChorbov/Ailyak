package aubg.hack.ailyak.db

import androidx.room.Database
import androidx.room.RoomDatabase
import aubg.hack.ailyak.db.dao.CachedAnimalDao
import aubg.hack.ailyak.db.dao.CachedPlantDao
import aubg.hack.ailyak.db.dao.CachedWaterDao
import aubg.hack.ailyak.db.dao.PathPointDao
import aubg.hack.ailyak.db.model.CachedPlantEntity
import aubg.hack.ailyak.db.model.CachedWaterEntity
import aubg.hack.ailyak.db.model.CachedAnimalEntity
import aubg.hack.ailyak.db.model.EncryptedPathPointEntity

@Database(
    entities = [
        EncryptedPathPointEntity::class,
        CachedPlantEntity::class,
        CachedWaterEntity::class,
        CachedAnimalEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pathPointDao(): PathPointDao
    abstract fun cachedPlantDao(): CachedPlantDao
    abstract fun cachedWaterDao(): CachedWaterDao

    abstract fun cachedAnimalDao(): CachedAnimalDao
}