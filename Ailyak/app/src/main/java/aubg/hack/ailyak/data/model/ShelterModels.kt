package aubg.hack.ailyak.data.model

data class ShelterUiItem(
    val id: Long,
    val name: String,
    val type: ShelterType,
    val lat: Double,
    val lng: Double,
    val hasWater: Boolean?,
    val isLockable: Boolean?,
    val accessType: String?,
    val distanceMeters: Double
)

enum class ShelterType {
    WILDERNESS_HUT,
    ALPINE_HUT,
    BASIC_HUT,
    LEAN_TO,
    WEATHER_SHELTER,
    PICNIC_SHELTER,
    CAVE,
    OTHER;

    fun label(): String = when (this) {
        WILDERNESS_HUT  -> "🏕 Wilderness Hut"
        ALPINE_HUT      -> "🏔 Alpine Hut"
        BASIC_HUT       -> "🛖 Basic Hut"
        LEAN_TO         -> "🪵 Lean-To"
        WEATHER_SHELTER -> "⛺ Weather Shelter"
        PICNIC_SHELTER  -> "🌿 Picnic Shelter"
        CAVE            -> "🪨 Cave"
        OTHER           -> "🏠 Shelter"
    }

    fun description(): String = when (this) {
        WILDERNESS_HUT  -> "Remote unserviced building with sleeping accommodation and heating."
        ALPINE_HUT      -> "Mountain hut with food, shelter and sleeping places, staffed seasonally."
        BASIC_HUT       -> "Simple enclosed cottage, may have a fireplace."
        LEAN_TO         -> "Open-sided shelter, partial weather protection."
        WEATHER_SHELTER -> "Structure protecting against rain, sun or wind."
        PICNIC_SHELTER  -> "Roofed shelter for picnic areas, rain protection."
        CAVE            -> "Natural cave used as emergency shelter."
        OTHER           -> "Small structure offering protection against bad weather."
    }
}