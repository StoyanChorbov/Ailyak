package aubg.hack.ailyak.service

import aubg.hack.ailyak.GBIFConstants
import aubg.hack.ailyak.data.model.GbifResponse
import aubg.hack.ailyak.data.model.PlantOccurrence
import aubg.hack.ailyak.https.KtorClient
import org.json.JSONObject

class GbifService {
    suspend fun getPlantsByCountry(
        countryCode: String,
        limit: Int = 20,
        offset: Int = 0
    ): Result<GbifResponse> {
        return KtorClient.get(
            GBIFConstants.apiUrl + "occurrence/search",
            params = mapOf(
                "country"       to countryCode,
                "kingdomKey"    to "6",
                "hasCoordinate" to "true",
                "limit"         to limit.toString(),
                "offset"        to offset.toString()
            )
        ).mapCatching { json -> parsePlantsByCountry(json) }
    }

    private fun parsePlantsByCountry(json: String): GbifResponse {
        val root = JSONObject(json)
        val results = root.getJSONArray("results")
        val endOfRecords = root.optBoolean("endOfRecords", true)

        return GbifResponse(
            count        = root.optInt("count"),
            offset       = root.optInt("offset"),
            limit        = root.optInt("limit"),
            endOfRecords = endOfRecords,
            plantsByCountry = List(results.length()) { i ->
                val plant = results.getJSONObject(i)
                PlantOccurrence(
                    species          = plant.optString("species").takeIf { it.isNotEmpty() },
                    scientificName   = plant.optString("scientificName").takeIf { it.isNotEmpty() },
                    decimalLatitude  = if (plant.has("decimalLatitude")) plant.getDouble("decimalLatitude") else null,
                    decimalLongitude = if (plant.has("decimalLongitude")) plant.getDouble("decimalLongitude") else null,
                    stateProvince    = plant.optString("stateProvince").takeIf { it.isNotEmpty() },
                    eventDate        = plant.optString("eventDate").takeIf { it.isNotEmpty() }
                )
            }
        )
    }

    suspend fun getAllPlantsByCountry(countryCode: String): List<PlantOccurrence> {
        val allResults = mutableListOf<PlantOccurrence>()
        var offset = 0
        val limit = 300

        do {
            val response = getPlantsByCountry(countryCode, limit, offset).getOrThrow()
            allResults.addAll(response.plantsByCountry)
            offset += limit
        } while (!response.endOfRecords)

        return allResults
    }
}