package aubg.hack.ailyak.data.model

import com.google.gson.annotations.SerializedName

data class GbifResponse(
    val results: List<GbifOccurrence>,
    val count: Int,
    val endOfRecords: Boolean
)

data class GbifOccurrence(
    val key: Long,
    val species: String?,
    val scientificName: String?,
    @SerializedName("vernacularName") val commonName: String?,
    val kingdom: String?,
    val phylum: String?,
    val order: String?,
    val family: String?,
    @SerializedName("decimalLatitude") val lat: Double?,
    @SerializedName("decimalLongitude") val lng: Double?,
    @SerializedName("mediaType") val mediaTypes: List<String>?,
    val media: List<GbifMedia>?,
    @SerializedName("iucnRedListCategory") val iucnCategory: String?,
    val taxonKey: Long?
)

data class GbifMedia(
    val type: String?,
    val identifier: String?,   // image URL
    val title: String?,
    val license: String?
)

data class AnimalUiItem(
    val id: Long,
    val scientificName: String,
    val commonName: String?,
    val photoUrl: String?,
    val kingdom: String?,
    val order: String?,
    val family: String?,
    val iucnCategory: IucnCategory,
    val distanceMeters: Double,
    val animalGroup: AnimalGroup
)

enum class AnimalGroup {
    MAMMAL, BIRD, REPTILE, AMPHIBIAN, INSECT, FISH, OTHER;

    fun label(): String = when (this) {
        MAMMAL    -> "🦊 Mammal"
        BIRD      -> "🐦 Bird"
        REPTILE   -> "🦎 Reptile"
        AMPHIBIAN -> "🐸 Amphibian"
        INSECT    -> "🐛 Insect"
        FISH      -> "🐟 Fish"
        OTHER     -> "🐾 Animal"
    }
}

enum class IucnCategory {
    LC, NT, VU, EN, CR, EW, EX, UNKNOWN;

    fun label(): String = when (this) {
        LC      -> "Least Concern"
        NT      -> "Near Threatened"
        VU      -> "Vulnerable"
        EN      -> "Endangered"
        CR      -> "Critically Endangered"
        EW      -> "Extinct in Wild"
        EX      -> "Extinct"
        UNKNOWN -> "Not Assessed"
    }

    fun shortLabel(): String = when (this) {
        UNKNOWN -> "N/A"
        else    -> name
    }
}