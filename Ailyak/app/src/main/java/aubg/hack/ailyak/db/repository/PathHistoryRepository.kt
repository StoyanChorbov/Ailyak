package aubg.hack.ailyak.db.repository

import aubg.hack.ailyak.db.dao.PathPointDao
import aubg.hack.ailyak.db.model.EncryptedPathPointEntity
import aubg.hack.ailyak.db.model.PathPointEntity
import aubg.hack.ailyak.security.KeystoreCrypto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PathHistoryRepository @Inject constructor(
    private val pathPointDao: PathPointDao,
    private val crypto: KeystoreCrypto
) {
    fun observeAllPoints(): Flow<List<PathPointEntity>> =
        pathPointDao.getAllPoints().map { encryptedPoints ->
            encryptedPoints.mapNotNull { it.toPlain(crypto) }
        }

    suspend fun insertPoint(point: PathPointEntity) {
        pathPointDao.insertPoint(EncryptedPathPointEntity.fromPlain(point, crypto))
    }

    suspend fun clearAll() = pathPointDao.clearAll()
}
