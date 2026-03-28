package aubg.hack.ailyak.service

import com.google.gson.Gson
import aubg.hack.ailyak.https.KtorClient
import aubg.hack.ailyak.data.model.GbifResponse
import aubg.hack.ailyak.data.model.PlantOccurrence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GbifService {

    private val gson = Gson()
    private val baseUrl = "https://api.gbif.org/v1/occurrence/search"

    suspend fun getPlantsByCountry(
        countryCode: String,
        limit: Int = 20,
        offset: Int = 0
    ): GbifResponse = withContext(Dispatchers.IO) {
        val json = KtorClient.get(baseUrl, mapOf(
            "country"       to countryCode,
            "kingdomKey"    to "6",
            "hasCoordinate" to "true",
            "limit"         to limit.toString(),
            "offset"        to offset.toString()
        ))

        gson.fromJson(json.toString(), GbifResponse::class.java)
    }

    suspend fun getAllPlantsByCountry(countryCode: String): List<PlantOccurrence> {
        val allResults = mutableListOf<PlantOccurrence>()
        var offset = 0
        val limit = 300

        do {
            val response = getPlantsByCountry(countryCode, limit, offset)
            allResults.addAll(response.results)
            offset += limit
        } while (!response.endOfRecords)

        return allResults
    }
}
