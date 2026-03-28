package aubg.hack.ailyak.service

import kotlinx.coroutines.coroutineScope
import aubg.hack.ailyak.data.model.PlantOccurrence
import aubg.hack.ailyak.data.model.PlantSafetyInfo

object PlantSafetyService {
    suspend fun getSafePlantsNearby(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 50.0
    ): List<PlantSafetyInfo> =
        getPlantSafetyInfoNearby(latitude, longitude, radiusKm).filter { it.isSafeToEat }

    suspend fun getPlantSafetyInfoNearby(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 50.0
    ): List<PlantSafetyInfo> {
        val occurrences = GbifService.getAllPlantsByLocation(latitude, longitude, radiusKm)
        return coroutineScope {
            occurrences
                .filter { it.species != null }
                .distinctBy { it.species }
                .map { enrichWithSafetyData(it) }
        }
    }

    private suspend fun enrichWithSafetyData(occurrence: PlantOccurrence): PlantSafetyInfo {
        val safety = PerenualService.getPlantSafety(occurrence.species!!)
            .getOrNull()

        return PlantSafetyInfo(
            species     = occurrence.species,
            commonName  = safety?.commonName,
            lat         = occurrence.decimalLatitude,
            lon         = occurrence.decimalLongitude,
            isEdible    = safety?.edible,
            isPoisonous = safety?.poisonous,
            region      = occurrence.stateProvince
        )
    }
}