package aubg.hack.ailyak.repository

import aubg.hack.ailyak.common.Result
import aubg.hack.ailyak.data.model.OverpassElement
import aubg.hack.ailyak.data.model.ShelterType
import aubg.hack.ailyak.data.model.ShelterUiItem
import aubg.hack.ailyak.service.WaterSourceService
import kotlin.math.*
import javax.inject.Inject

class ShelterRepository @Inject constructor(
    private val api: WaterSourceService   // reuse Overpass service
) {
    suspend fun getNearbyShelters(
        lat: Double,
        lng: Double,
        radiusM: Int = 20000
    ): Result<List<ShelterUiItem>> {
        return try {
            val query = """
                [out:json][timeout:25];
                (
                  node["amenity"="shelter"](around:$radiusM,$lat,$lng);
                  node["tourism"="wilderness_hut"](around:$radiusM,$lat,$lng);
                  node["tourism"="alpine_hut"](around:$radiusM,$lat,$lng);
                  node["tourism"="camp_site"](around:$radiusM,$lat,$lng);
                  node["natural"="cave_entrance"](around:$radiusM,$lat,$lng);
                  way["amenity"="shelter"](around:$radiusM,$lat,$lng);
                  way["tourism"="wilderness_hut"](around:$radiusM,$lat,$lng);
                  way["tourism"="alpine_hut"](around:$radiusM,$lat,$lng);
                );
                out center tags 60;
            """.trimIndent()

            val response = api.queryWater(query)
            val items = response.elements
                .mapNotNull { it.toShelterItem(lat, lng) }
                .sortedBy { it.distanceMeters }

            Result.Success(items)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load shelters", e)
        }
    }

    private fun OverpassElement.toShelterItem(
        userLat: Double,
        userLng: Double
    ): ShelterUiItem? {
        val tags = tags ?: return null
        val sourceLat = lat ?: center?.lat ?: return null
        val sourceLng = lon ?: center?.lon ?: return null

        val name = tags["name"]
            ?: tags["description"]
            ?: resolveDefaultName(tags)

        return ShelterUiItem(
            id = id,
            name = name,
            type = resolveType(tags),
            lat = sourceLat,
            lng = sourceLng,
            hasWater = when (tags["drinking_water"]) {
                "yes" -> true
                "no"  -> false
                else  -> null
            },
            isLockable = when (tags["lockable"]) {
                "yes" -> true
                "no"  -> false
                else  -> null
            },
            accessType = tags["access"],
            distanceMeters = haversineMeters(userLat, userLng, sourceLat, sourceLng)
        )
    }

    private fun resolveDefaultName(tags: Map<String, String>): String = when {
        tags["tourism"] == "wilderness_hut" -> "Unnamed Wilderness Hut"
        tags["tourism"] == "alpine_hut"     -> "Unnamed Alpine Hut"
        tags["tourism"] == "camp_site"      -> "Unnamed Camp Site"
        tags["natural"] == "cave_entrance"  -> "Cave Entrance"
        tags["shelter_type"] != null        -> "Unnamed ${tags["shelter_type"]!!
            .replace("_", " ").replaceFirstChar { it.uppercase() }}"
        else                                -> "Unnamed Shelter"
    }

    private fun resolveType(tags: Map<String, String>): ShelterType = when {
        tags["tourism"] == "wilderness_hut"          -> ShelterType.WILDERNESS_HUT
        tags["tourism"] == "alpine_hut"              -> ShelterType.ALPINE_HUT
        tags["natural"] == "cave_entrance"           -> ShelterType.CAVE
        tags["shelter_type"] == "basic_hut"          -> ShelterType.BASIC_HUT
        tags["shelter_type"] == "lean_to"            -> ShelterType.LEAN_TO
        tags["shelter_type"] == "weather_shelter"    -> ShelterType.WEATHER_SHELTER
        tags["shelter_type"] == "picnic_shelter"     -> ShelterType.PICNIC_SHELTER
        else                                         -> ShelterType.OTHER
    }

    private fun haversineMeters(
        lat1: Double, lng1: Double,
        lat2: Double, lng2: Double
    ): Double {
        val r = 6_371_000.0
        val phi1 = Math.toRadians(lat1); val phi2 = Math.toRadians(lat2)
        val dPhi = Math.toRadians(lat2 - lat1)
        val dLam = Math.toRadians(lng2 - lng1)
        val a = sin(dPhi / 2).pow(2) + cos(phi1) * cos(phi2) * sin(dLam / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}