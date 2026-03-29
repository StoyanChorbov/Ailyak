package aubg.hack.ailyak.db

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var db: AppDatabase? = null

    fun get(context: Context): AppDatabase {
        return db ?: Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ailyak_db"
        ).build().also { db = it }
    }
}