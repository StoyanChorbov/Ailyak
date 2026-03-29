package aubg.hack.ailyak.db.model

data class PathPointEntity(
    val id: Long = 0,
    val lat: Double,
    val lng: Double,
    val timestamp: Long,
    val hadConnection: Boolean = true
)
