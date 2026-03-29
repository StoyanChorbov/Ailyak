package aubg.hack.ailyak.ui.survivalguide.data

import aubg.hack.ailyak.data.model.PlantUiItem
import aubg.hack.ailyak.data.model.SafetyLevel
import aubg.hack.ailyak.data.model.SpeciesCountResult
import aubg.hack.ailyak.data.model.Result
import aubg.hack.ailyak.db.dao.CachedPlantDao
import aubg.hack.ailyak.db.model.CachedPlantEntity
import aubg.hack.ailyak.https.PlantApiService
import kotlin.math.*
import javax.inject.Inject

class PlantRepository @Inject constructor(
    private val api: PlantApiService,
    private val dao: CachedPlantDao
) {
    private val cacheTtlMs    = 6 * 60 * 60 * 1000L  // 6 hours
    private val cacheRadiusKm = 2.0

    private val toxicGenera = setOf(
        "Amanita", "Galerina", "Lepiota", "Cortinarius", "Inocybe",
        "Conium", "Aconitum", "Digitalis", "Taxus", "Solanum",
        "Datura", "Brugmansia", "Veratrum", "Colchicum", "Cicuta"
    )
    private val edibleGenera = setOf(
        "Cantharellus", "Boletus", "Morchella", "Pleurotus", "Craterellus",
        "Fragaria", "Vaccinium", "Rubus", "Malus", "Pyrus",
        "Taraxacum", "Urtica", "Sambucus", "Allium", "Chenopodium"
    )

    suspend fun getNearbyPlants(lat: Double, lng: Double): Result<List<PlantUiItem>> {
        // ── 1. Try fresh Room cache ────────────────────────────────
        val fresh = dao.getFresh(since = System.currentTimeMillis() - cacheTtlMs)
        if (fresh.isNotEmpty()) {
            val cLat = fresh.first().cachedLat
            val cLng = fresh.first().cachedLng
            if (haversineKm(lat, lng, cLat, cLng) < cacheRadiusKm) {
                return Result.Success(fresh.map { it.toPlantUiItem() })
            }
        }

        // ── 2. Fetch from API ──────────────────────────────────────
        return try {
            val plants = api.getNearbySpecies(lat, lng, iconicTaxa = "Plantae")
            val fungi  = api.getNearbySpecies(lat, lng, iconicTaxa = "Fungi")

            val combined = (plants.results + fungi.results)
                .distinctBy { it.taxon.id }
                .map { result -> result.toPlantUiItem() }
                .sortedWith(
                    compareByDescending<PlantUiItem> { it.safetyLevel == SafetyLevel.EDIBLE }
                        .thenByDescending { it.observationCount }
                )

            // ── 3. Persist to Room ─────────────────────────────────
            dao.clearAll()
            dao.insertAll(combined.map { it.toEntity(lat, lng) })

            Result.Success(combined)
        } catch (e: Exception) {
            // ── 4. Serve stale cache on failure ────────────────────
            val stale = dao.getAll()
            if (stale.isNotEmpty()) Result.Success(stale.map { it.toPlantUiItem() })
            else Result.Error(e.message ?: "Failed to load species", e)
        }
    }

    suspend fun getTaxonSummary(taxonId: Long): String? {
        return try {
            api.getTaxonDetail(taxonId).results.firstOrNull()?.wikipediaSummary
        } catch (e: Exception) {
            null
        }
    }

    // ── SpeciesCountResult → PlantUiItem ───────────────────────────
    private fun SpeciesCountResult.toPlantUiItem(): PlantUiItem {
        val genus = taxon.name.split(" ").firstOrNull() ?: ""
        val safety = when {
            taxon.conservationStatus?.status in listOf("EX", "EW", "CR") -> SafetyLevel.CAUTION
            genus in toxicGenera  -> SafetyLevel.TOXIC
            genus in edibleGenera -> SafetyLevel.EDIBLE
            taxon.threatened      -> SafetyLevel.CAUTION
            else                  -> SafetyLevel.UNKNOWN
        }
        return PlantUiItem(
            id = taxon.id,
            scientificName = taxon.name,
            commonName = taxon.commonName,
            photoUrl = taxon.photo?.mediumUrl,
            photoAttribution = taxon.photo?.attribution,
            wikipediaUrl = taxon.wikipediaUrl,
            summary = taxon.wikipediaSummary,
            isFungus = taxon.iconicTaxonName == "Fungi",
            safetyLevel = safety,
            observationCount = count,
            rank = taxon.rank
        )
    }

    // ── CachedPlantEntity → PlantUiItem ───────────────────────────
    private fun CachedPlantEntity.toPlantUiItem() = PlantUiItem(
        id = id,
        scientificName = scientificName,
        commonName = commonName,
        photoUrl = photoUrl,
        photoAttribution = photoAttribution,
        wikipediaUrl = wikipediaUrl,
        summary = null,   // fetched lazily on expand
        isFungus = isFungus,
        safetyLevel = SafetyLevel.valueOf(safetyLevel),
        observationCount = observationCount,
        rank = rank
    )

    // ── PlantUiItem → CachedPlantEntity ───────────────────────────
    private fun PlantUiItem.toEntity(lat: Double, lng: Double) = CachedPlantEntity(
        id               = id,
        scientificName   = scientificName,
        commonName       = commonName,
        photoUrl         = photoUrl,
        photoAttribution = photoAttribution,
        wikipediaUrl     = wikipediaUrl,
        isFungus         = isFungus,
        safetyLevel      = safetyLevel.name,
        observationCount = observationCount,
        rank             = rank,
        cachedLat        = lat,
        cachedLng        = lng
    )

    // ── Haversine distance in km ───────────────────────────────────
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
}