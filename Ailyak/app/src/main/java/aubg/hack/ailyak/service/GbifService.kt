package aubg.hack.ailyak.service

import aubg.hack.ailyak.GBIFConstants
import aubg.hack.ailyak.data.model.GbifResponse
import aubg.hack.ailyak.data.model.PlantOccurrence
import aubg.hack.ailyak.https.KtorClient
import org.json.JSONObject
import kotlin.math.*

object GbifService {

    // ~111 km per degree of latitude
    private fun boundingBox(lat: Double, lng: Double, radiusKm: Double): Map<String, String> {
        val deltaLat = radiusKm / 111.0
        val deltaLng = radiusKm / (111.0 * cos(Math.toRadians(lat)))
        return mapOf(
            "decimalLatitude"  to "${lat - deltaLat},${lat + deltaLat}",
            "decimalLongitude" to "${lng - deltaLng},${lng + deltaLng}"
        )
    }

    suspend fun getPlantsByLocation(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 50.0,
        limit: Int = 20,
        offset: Int = 0
    ): Result<GbifResponse> {
        val bbox = boundingBox(latitude, longitude, radiusKm)
        return KtorClient.get(
            GBIFConstants.apiUrl + "occurrence/search",
            params = mapOf(
                "kingdomKey"    to "6",
                "hasCoordinate" to "true",
                "limit"         to limit.toString(),
                "offset"        to offset.toString()
            ) + bbox
        ).mapCatching { json -> parsePlants(json) }
    }

    private fun parsePlants(json: String): GbifResponse {
        val root = JSONObject(json)
        val results = root.getJSONArray("results")

        return GbifResponse(
            count        = root.optInt("count"),
            offset       = root.optInt("offset"),
            limit        = root.optInt("limit"),
            endOfRecords = root.optBoolean("endOfRecords", true),
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

    suspend fun getAllPlantsByLocation(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 50.0
    ): List<PlantOccurrence> {
        val allResults = mutableListOf<PlantOccurrence>()
        var offset = 0
        val limit = 300

        do {
            val response = getPlantsByLocation(latitude, longitude, radiusKm, limit, offset).getOrThrow()
            allResults.addAll(response.plantsByCountry)
            offset += limit
        } while (!response.endOfRecords)

        return allResults
    }
}