package aubg.hack.ailyak.service

import com.google.gson.Gson
import aubg.hack.ailyak.https.HttpClient
import aubg.hack.ailyak.data.model.OverpassResponse
import aubg.hack.ailyak.data.model.OverpassElement
import aubg.hack.ailyak.data.model.WaterSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class WaterSourceService {

    private val gson = Gson()
    private val overpassUrl = "https://overpass-api.de/api/interpreter"

    companion object {
        // Radius in metres
        const val DEFAULT_RADIUS = 2000
    }

    suspend fun getWaterSourcesNearby(
        lat: Double,
        lon: Double,
        radiusMetres: Int = DEFAULT_RADIUS
    ): List<WaterSource> = withContext(Dispatchers.IO) {
        val query = buildOverpassQuery(lat, lon, radiusMetres)
        val encoded = URLEncoder.encode(query, "UTF-8")
        val json = HttpClient.get("$overpassUrl?data=$encoded")
        val response = gson.fromJson(json, OverpassResponse::class.java)
        response.elements.mapNotNull { it.toWaterSource() }
    }

    suspend fun getDrinkingWaterNearby(
        lat: Double,
        lon: Double,
        radiusMetres: Int = DEFAULT_RADIUS
    ): List<WaterSource> =
        getWaterSourcesNearby(lat, lon, radiusMetres)
            .filter { it.type == "drinking_water" && it.access != "private" }

    suspend fun getSpringsNearby(
        lat: Double,
        lon: Double,
        radiusMetres: Int = DEFAULT_RADIUS
    ): List<WaterSource> =
        getWaterSourcesNearby(lat, lon, radiusMetres)
            .filter { it.type == "spring" }

    // --- Private helpers ---

    private fun buildOverpassQuery(lat: Double, lon: Double, radius: Int): String {
        return """
            [out:json][timeout:25];
            (
              node["amenity"="drinking_water"](around:$radius,$lat,$lon);
              node["natural"="spring"](around:$radius,$lat,$lon);
              node["natural"="water"](around:$radius,$lat,$lon);
              node["waterway"="river"](around:$radius,$lat,$lon);
              node["waterway"="stream"](around:$radius,$lat,$lon);
              way["natural"="water"](around:$radius,$lat,$lon);
              way["waterway"="river"](around:$radius,$lat,$lon);
            );
            out center;
        """.trimIndent()
    }

    private fun OverpassElement.toWaterSource(): WaterSource? {
        val resolvedLat = lat ?: center?.lat ?: return null
        val resolvedLon = lon ?: center?.lon ?: return null
        val t = tags ?: emptyMap()

        val type = when {
            t["amenity"] == "drinking_water" -> "drinking_water"
            t["natural"] == "spring"         -> "spring"
            t["natural"] == "water"          -> "water"
            t["waterway"] == "river"         -> "river"
            t["waterway"] == "stream"        -> "stream"
            else                             -> "unknown"
        }

        return WaterSource(
            id          = id,
            type        = type,
            name        = t["name"],
            lat         = resolvedLat,
            lon         = resolvedLon,
            operator    = t["operator"],
            access      = t["access"],
            seasonal    = t["seasonal"],
            description = t["description"]
        )
    }
}
