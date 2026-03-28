package aubg.hack.ailyak.data.model

data class WaterSource(
    val id: Long,
    val type: String,           // "drinking_water", "spring", "river", "lake", etc.
    val name: String?,
    val lat: Double,
    val lon: Double,
    val operator: String?,
    val access: String?,        // "yes", "public", "private"
    val seasonal: String?,      // "yes" / "no"
    val description: String?
)
