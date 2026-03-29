package aubg.hack.ailyak.data.model

data class OverpassResponse(
    val elements: List<OverpassElement>
)

data class OverpassElement(
    val id: Long,
    val type: String,           // "node", "way", "relation"
    val lat: Double?,           // present on nodes
    val lon: Double?,
    val center: OverpassCenter?, // present on ways/relations
    val tags: Map<String, String>?
)

data class OverpassCenter(
    val lat: Double,
    val lon: Double
)

data class WaterSourceUi(
    val id: Long,
    val name: String,
    val type: WaterType,
    val lat: Double,
    val lng: Double,
    val drinkable: DrinkableStatus,
    val distanceMeters: Double = 0.0
)

enum class WaterType {
    SPRING, RIVER, LAKE, STREAM, DRINKING_TAP, WELL, POND, OTHER;

    fun label(): String = when (this) {
        SPRING       -> "🌊 Spring"
        RIVER        -> "🏞 River"
        LAKE         -> "🏔 Lake"
        STREAM       -> "💧 Stream"
        DRINKING_TAP -> "🚰 Drinking Tap"
        WELL         -> "🪣 Well"
        POND         -> "🌿 Pond"
        OTHER        -> "💦 Water Source"
    }
}

enum class DrinkableStatus {
    YES, NO, UNKNOWN;

    fun label(): String = when (this) {
        YES     -> "Drinkable"
        NO      -> "Not drinkable"
        UNKNOWN -> "Unknown purity"
    }
}