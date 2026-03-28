package aubg.hack.ailyak.data.model
data class GbifResponse(
    val count: Int,
    val offset: Int,
    val limit: Int,
    val endOfRecords: Boolean,
    val results: List<PlantOccurrence>
)

data class PlantOccurrence(
    val species: String?,
    val scientificName: String?,
    val decimalLatitude: Double?,
    val decimalLongitude: Double?,
    val stateProvince: String?,
    val eventDate: String?
)
