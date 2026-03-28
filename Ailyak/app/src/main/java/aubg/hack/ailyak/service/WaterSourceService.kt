package aubg.hack.ailyak.service

import aubg.hack.ailyak.https.KtorClient
import aubg.hack.ailyak.data.model.OverpassElement
import aubg.hack.ailyak.data.model.OverpassResponse
import aubg.hack.ailyak.data.model.WaterSource
import org.json.JSONObject
import aubg.hack.ailyak.WaterSourceConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

object WaterSourceService {

    suspend fun getWaterSourcesNearby(
        lat: Double,
        lon: Double,
        radiusMetres: Int = WaterSourceConstants.defaultRadius
    ): Result<List<WaterSource>> {
        val query = buildOverpassQuery(lat, lon, radiusMetres)
        val encoded = withContext(Dispatchers.IO) {
            URLEncoder.encode(query, "UTF-8")
        }

        return KtorClient.get("${WaterSourceConstants.apiUrl}overpassUrl?data=$encoded")
            .mapCatching { json -> parseWaterSources(json) }
    }

    suspend fun getDrinkingWaterNearby(
        lat: Double,
        lon: Double,
        radiusMetres: Int = WaterSourceConstants.defaultRadius
    ): Result<List<WaterSource>> =
        getWaterSourcesNearby(lat, lon, radiusMetres)
            .map { list -> list.filter { it.type == "drinking_water" && it.access != "private" } }

    suspend fun getSpringsNearby(
        lat: Double,
        lon: Double,
        radiusMetres: Int = WaterSourceConstants.defaultRadius
    ): Result<List<WaterSource>> =
        getWaterSourcesNearby(lat, lon, radiusMetres)
            .map { list -> list.filter { it.type == "spring" } }

    private fun parseWaterSources(json: String): List<WaterSource> {
        val root = JSONObject(json)
        val elements = root.optJSONArray("elements") ?: return emptyList()

        return List(elements.length()) { i -> elements.getJSONObject(i) }
            .mapNotNull { el ->
                val resolvedLat = if (el.has("lat")) el.getDouble("lat")
                else el.optJSONObject("center")?.getDouble("lat") ?: return@mapNotNull null
                val resolvedLon = if (el.has("lon")) el.getDouble("lon")
                else el.optJSONObject("center")?.getDouble("lon") ?: return@mapNotNull null

                val tags = el.optJSONObject("tags")
                    ?.let { t -> t.keys().asSequence().associateWith { k -> t.getString(k) } }
                    ?: emptyMap()

                val type = when {
                    tags["amenity"] == "drinking_water" -> "drinking_water"
                    tags["natural"] == "spring"         -> "spring"
                    tags["natural"] == "water"          -> "water"
                    tags["waterway"] == "river"         -> "river"
                    tags["waterway"] == "stream"        -> "stream"
                    else                                -> "unknown"
                }

                WaterSource(
                    id          = el.getLong("id"),
                    type        = type,
                    name        = tags["name"],
                    lat         = resolvedLat,
                    lon         = resolvedLon,
                    operator    = tags["operator"],
                    access      = tags["access"],
                    seasonal    = tags["seasonal"],
                    description = tags["description"]
                )
            }
    }

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
}