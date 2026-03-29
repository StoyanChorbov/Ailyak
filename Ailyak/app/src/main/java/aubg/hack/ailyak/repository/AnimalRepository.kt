package aubg.hack.ailyak.repository

import aubg.hack.ailyak.data.model.AnimalUiItem
import aubg.hack.ailyak.data.model.IucnCategory
import aubg.hack.ailyak.service.AnimalApiService
import kotlin.math.*
import javax.inject.Inject
import aubg.hack.ailyak.common.Result
import aubg.hack.ailyak.data.model.AnimalGroup
import aubg.hack.ailyak.data.model.GbifOccurrence

class AnimalRepository @Inject constructor(
    private val api: AnimalApiService
) {
    suspend fun getNearbyAnimals(lat: Double, lng: Double): Result<List<AnimalUiItem>> {
        return try {
            val response = api.getNearbyAnimals(lat, lng)

            val items = response.results
                .distinctBy { it.species ?: it.scientificName }
                .mapNotNull { it.toUiItem(lat, lng) }
                .sortedWith(
                    compareBy<AnimalUiItem> { it.iucnCategory == IucnCategory.UNKNOWN }
                        .thenByDescending { it.iucnCategory.ordinal }
                        .thenBy { it.distanceMeters }
                )

            Result.Success(items)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load animals", e)
        }
    }

    private fun GbifOccurrence.toUiItem(userLat: Double, userLng: Double): AnimalUiItem? {
        val name = species ?: scientificName ?: return null
        val photoUrl = media?.firstOrNull { it.type == "StillImage" }?.identifier
        val distance = if (lat != null && lng != null)
            haversineMeters(userLat, userLng, lat, lng) else 0.0

        return AnimalUiItem(
            id = key,
            scientificName = name,
            commonName = commonName,
            photoUrl = photoUrl,
            kingdom = kingdom,
            order = order,
            family = family,
            iucnCategory = parseIucn(iucnCategory),
            distanceMeters = distance,
            animalGroup = resolveGroup(order, phylum)
        )
    }

    private fun parseIucn(code: String?): IucnCategory = when (code?.uppercase()) {
        "LC" -> IucnCategory.LC
        "NT" -> IucnCategory.NT
        "VU" -> IucnCategory.VU
        "EN" -> IucnCategory.EN
        "CR" -> IucnCategory.CR
        "EW" -> IucnCategory.EW
        "EX" -> IucnCategory.EX
        else -> IucnCategory.UNKNOWN
    }

    private fun resolveGroup(order: String?, phylum: String?): AnimalGroup {
        val o = order?.lowercase() ?: ""
        val p = phylum?.lowercase() ?: ""
        return when {
            o in listOf("passeriformes","falconiformes","strigiformes",
                "galliformes","anseriformes","columbiformes") -> AnimalGroup.BIRD
            o in listOf("carnivora","artiodactyla","rodentia",
                "chiroptera","primates","lagomorpha") -> AnimalGroup.MAMMAL
            o in listOf("squamata","testudines","crocodilia") -> AnimalGroup.REPTILE
            o in listOf("anura","caudata","gymnophiona") -> AnimalGroup.AMPHIBIAN
            p == "arthropoda" -> AnimalGroup.INSECT
            p == "chordata" && o.contains("form") -> AnimalGroup.FISH
            else -> AnimalGroup.OTHER
        }
    }

    private fun haversineMeters(
        lat1: Double, lng1: Double,
        lat2: Double, lng2: Double
    ): Double {
        val r = 6_371_000.0
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val dPhi = Math.toRadians(lat2 - lat1)
        val dLam = Math.toRadians(lng2 - lng1)
        val a = sin(dPhi / 2).pow(2) + cos(phi1) * cos(phi2) * sin(dLam / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}