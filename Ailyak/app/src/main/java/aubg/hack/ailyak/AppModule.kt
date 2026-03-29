package aubg.hack.ailyak

import android.content.Context
import androidx.room.Room
import aubg.hack.ailyak.db.AppDatabase
import aubg.hack.ailyak.db.dao.CachedAnimalDao
import aubg.hack.ailyak.db.dao.CachedCellTowerDao
import aubg.hack.ailyak.db.dao.CachedPlantDao
import aubg.hack.ailyak.db.dao.CachedWaterDao
import aubg.hack.ailyak.db.dao.PathPointDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ailyak"
        )
            .fallbackToDestructiveMigration()   // handles version bumps during development
            .build()

    @Provides
    @Singleton
    fun providePathPointDao(db: AppDatabase): PathPointDao = db.pathPointDao()

    @Provides
    @Singleton
    fun provideCachedPlantDao(db: AppDatabase): CachedPlantDao = db.cachedPlantDao()  // ← add

    @Provides
    @Singleton
    fun provideCachedWaterDao(db: AppDatabase): CachedWaterDao = db.cachedWaterDao()

    @Provides
    @Singleton
    fun provideCachedAnimalDao(db: AppDatabase): CachedAnimalDao = db.cachedAnimalDao()

    @Provides
    @Singleton
    fun provideCachedCellTowers(db: AppDatabase): CachedCellTowerDao = db.cachedCellTowerDao()
}