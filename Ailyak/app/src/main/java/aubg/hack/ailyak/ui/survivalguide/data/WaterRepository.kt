package aubg.hack.ailyak.ui.survivalguide.data

import aubg.hack.ailyak.data.model.DrinkableStatus
import aubg.hack.ailyak.data.model.OverpassElement
import aubg.hack.ailyak.data.model.Result
import aubg.hack.ailyak.data.model.WaterSourceUi
import aubg.hack.ailyak.data.model.WaterType
import aubg.hack.ailyak.db.dao.CachedWaterDao
import aubg.hack.ailyak.db.model.CachedWaterEntity
import aubg.hack.ailyak.https.WaterApiService
import kotlin.math.*
import javax.inject.Inject

class WaterRepository @Inject constructor(
    private val api: WaterApiService,
    private val dao: CachedWaterDao
) {
    private val cacheTtlMs    = 6 * 60 * 60 * 1000L  // 6 hours
    private val cacheRadiusKm = 2.0

    suspend fun getNearbyWater(
        lat: Double,
        lng: Double,
        radiusM: Int = 8000
    ): Result<List<WaterSourceUi>> {
        // ── 1. Try fresh Room cache ────────────────────────────────
        val fresh = dao.getFresh(since = System.currentTimeMillis() - cacheTtlMs)
        if (fresh.isNotEmpty()) {
            val cLat = fresh.first().cachedLat
            val cLng = fresh.first().cachedLng
            if (haversineKm(lat, lng, cLat, cLng) < cacheRadiusKm) {
                return Result.Success(fresh.map { it.toWaterUiItem() })
            }
        }

        // ── 2. Fetch from Overpass API ─────────────────────────────
        return try {
            val query = """
                [out:json][timeout:25][maxsize:1048576];
                (
                  node["natural"="spring"](around:$radiusM,$lat,$lng);
                  node["amenity"="drinking_water"](around:$radiusM,$lat,$lng);
                  node["man_made"="water_well"](around:$radiusM,$lat,$lng);
                  node["man_made"="water_tap"](around:$radiusM,$lat,$lng);
                  way["natural"="water"](around:$radiusM,$lat,$lng);
                  way["waterway"="river"](around:$radiusM,$lat,$lng);
                  way["waterway"="stream"](around:$radiusM,$lat,$lng);
                );
                out center tags 50;
            """.trimIndent()

            val response = api.queryWater(query)
            val items = response.elements
                .mapNotNull { it.toWaterSource(lat, lng) }
                .sortedBy { it.distanceMeters }
                .take(30)

            // ── 3. Persist to Room ─────────────────────────────────
            dao.clearAll()
            dao.insertAll(items.map { it.toEntity(lat, lng) })

            Result.Success(items)
        } catch (e: Exception) {
            // ── 4. Serve stale cache on failure ────────────────────
            val stale = dao.getAll()
            if (stale.isNotEmpty()) Result.Success(stale.map { it.toWaterUiItem() })
            else Result.Error(e.message ?: "Failed to load water sources", e)
        }
    }

    // ── CachedWaterEntity → WaterSourceUi ─────────────────────────
    private fun CachedWaterEntity.toWaterUiItem() = WaterSourceUi(
        id = id,
        name = name,
        type = WaterType.valueOf(type),
        lat = lat,
        lng = lng,
        drinkable = DrinkableStatus.valueOf(drinkable),
        distanceMeters = distanceMeters
    )

    // ── WaterSourceUi → CachedWaterEntity ─────────────────────────
    private fun WaterSourceUi.toEntity(cLat: Double, cLng: Double) = CachedWaterEntity(
        id              = id,
        name            = name,
        type            = type.name,
        lat             = lat,
        lng             = lng,
        drinkable       = drinkable.name,
        distanceMeters  = distanceMeters,
        cachedLat       = cLat,
        cachedLng       = cLng
    )

    // ── OverpassElement → WaterSourceUi ───────────────────────────
    private fun OverpassElement.toWaterSource(
        userLat: Double,
        userLng: Double
    ): WaterSourceUi? {
        val tags = tags ?: return null
        val sourceLat = lat ?: center?.lat ?: return null
        val sourceLng = lon ?: center?.lon ?: return null
        val name = tags["name"] ?: tags["description"] ?: resolveDefaultName(tags)
        return WaterSourceUi(
            id = id,
            name = name,
            type = resolveWaterType(tags),
            lat = sourceLat,
            lng = sourceLng,
            drinkable = resolveDrinkable(tags),
            distanceMeters = haversineMeters(userLat, userLng, sourceLat, sourceLng)
        )
    }

    private fun resolveDefaultName(tags: Map<String, String>): String = when {
        tags["natural"] == "spring"         -> "Unnamed Spring"
        tags["amenity"] == "drinking_water" -> "Drinking Water Point"
        tags["man_made"] == "water_well"    -> "Unnamed Well"
        tags["man_made"] == "water_tap"     -> "Water Tap"
        tags["waterway"] == "river"         -> "Unnamed River"
        tags["waterway"] == "stream"        -> "Unnamed Stream"
        tags["water"] == "lake"             -> "Unnamed Lake"
        tags["water"] == "pond"             -> "Unnamed Pond"
        else                                -> "Water Source"
    }

    private fun resolveWaterType(tags: Map<String, String>): WaterType = when {
        tags["natural"] == "spring"         -> WaterType.SPRING
        tags["amenity"] == "drinking_water" -> WaterType.DRINKING_TAP
        tags["man_made"] == "water_well"    -> WaterType.WELL
        tags["man_made"] == "water_tap"     -> WaterType.DRINKING_TAP
        tags["waterway"] == "river"         -> WaterType.RIVER
        tags["waterway"] == "stream"        -> WaterType.STREAM
        tags["water"] == "lake"             -> WaterType.LAKE
        tags["water"] == "pond"             -> WaterType.POND
        else                                -> WaterType.OTHER
    }

    private fun resolveDrinkable(tags: Map<String, String>): DrinkableStatus = when {
        tags["drinking_water"] == "yes"     -> DrinkableStatus.YES
        tags["drinking_water"] == "no"      -> DrinkableStatus.NO
        tags["amenity"] == "drinking_water" -> DrinkableStatus.YES
        tags["man_made"] == "water_tap"     -> DrinkableStatus.YES
        else                                -> DrinkableStatus.UNKNOWN
    }

    private fun haversineKm(
        lat1: Double, lng1: Double,
        lat2: Double, lng2: Double
    ): Double {
        val r    = 6371.0
        val phi1 = Math.toRadians(lat1); val phi2 = Math.toRadians(lat2)
        val dPhi = Math.toRadians(lat2 - lat1)
        val dLam = Math.toRadians(lng2 - lng1)
        val a    = sin(dPhi / 2).pow(2) + cos(phi1) * cos(phi2) * sin(dLam / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    private fun haversineMeters(
        lat1: Double, lng1: Double,
        lat2: Double, lng2: Double
    ): Double = haversineKm(lat1, lng1, lat2, lng2) * 1000
}