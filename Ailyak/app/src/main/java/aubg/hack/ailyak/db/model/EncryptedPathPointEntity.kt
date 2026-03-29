package aubg.hack.ailyak.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import aubg.hack.ailyak.security.KeystoreCrypto

@Entity(tableName = "path_points")
data class EncryptedPathPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latEnc: String,
    val lngEnc: String,
    val timestampEnc: String,
    val hadConnectionEnc: String
) {
    fun toPlain(crypto: KeystoreCrypto): PathPointEntity? = try {
        PathPointEntity(
            id = id,
            lat = crypto.decryptOrPlain(latEnc).toDouble(),
            lng = crypto.decryptOrPlain(lngEnc).toDouble(),
            timestamp = crypto.decryptOrPlain(timestampEnc).toLong(),
            hadConnection = crypto.decryptOrPlain(hadConnectionEnc).toBooleanStrict()
        )
    } catch (_: Exception) {
        null
    }

    companion object {
        fun fromPlain(point: PathPointEntity, crypto: KeystoreCrypto): EncryptedPathPointEntity =
            EncryptedPathPointEntity(
                id = point.id,
                latEnc = crypto.encrypt(point.lat.toString()),
                lngEnc = crypto.encrypt(point.lng.toString()),
                timestampEnc = crypto.encrypt(point.timestamp.toString()),
                hadConnectionEnc = crypto.encrypt(point.hadConnection.toString())
            )
    }
}
