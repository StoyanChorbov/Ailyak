package aubg.hack.ailyak.data.model

import com.google.gson.annotations.SerializedName

data class SpeciesCountResponse(
    val results: List<SpeciesCountResult>,
    @SerializedName("total_results") val totalResults: Int
)

data class SpeciesCountResult(
    val count: Int,
    val taxon: TaxonInfo
)

data class TaxonDetailResponse(
    val results: List<TaxonInfo>
)

data class TaxonInfo(
    val id: Long,
    val name: String,
    @SerializedName("preferred_common_name") val commonName: String?,
    @SerializedName("default_photo") val photo: TaxonPhoto?,
    @SerializedName("wikipedia_url") val wikipediaUrl: String?,
    @SerializedName("wikipedia_summary") val wikipediaSummary: String?,
    @SerializedName("iconic_taxon_name") val iconicTaxonName: String?,
    val rank: String?,
    val threatened: Boolean = false,
    @SerializedName("conservation_status") val conservationStatus: ConservationStatus?
)

data class TaxonPhoto(
    @SerializedName("medium_url") val mediumUrl: String?,
    @SerializedName("square_url") val squareUrl: String?,
    @SerializedName("attribution") val attribution: String?
)

data class ConservationStatus(
    val status: String?,
    @SerializedName("status_name") val statusName: String?
)

data class PlantUiItem(
    val id: Long,
    val scientificName: String,
    val commonName: String?,
    val photoUrl: String?,
    val photoAttribution: String?,
    val wikipediaUrl: String?,
    val summary: String?,          // fetched lazily from /taxa/{id}
    val isFungus: Boolean,
    val safetyLevel: SafetyLevel,
    val observationCount: Int,
    val rank: String?
)

enum class SafetyLevel {
    EDIBLE, CAUTION, TOXIC, UNKNOWN
}
