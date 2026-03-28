package com.plantservice.service

import com.plantservice.model.PlantOccurrence
import com.plantservice.model.PlantSafetyInfo

class PlantSafetyService(
    private val gbifService: GbifService,
    private val perenualService: PerenualService
) {

    suspend fun getSafePlantsInCountry(countryCode: String): List<PlantSafetyInfo> =
        getPlantSafetyInfoForCountry(countryCode).filter { it.isSafeToEat }

    suspend fun getPlantSafetyInfoForCountry(countryCode: String): List<PlantSafetyInfo> {
        val occurrences = gbifService.getAllPlantsByCountry(countryCode)

        return occurrences
            .filter { it.species != null }
            .distinctBy { it.species }
            .map { enrichWithSafetyData(it) }
    }

    private suspend fun enrichWithSafetyData(occurrence: PlantOccurrence): PlantSafetyInfo {
        val safety = perenualService.getPlantSafety(occurrence.species!!)
        return PlantSafetyInfo(
            species     = occurrence.species,
            commonName  = safety?.common_name,
            lat         = occurrence.decimalLatitude,
            lon         = occurrence.decimalLongitude,
            isEdible    = safety?.edible,
            isPoisonous = safety?.poisonous,
            region      = occurrence.stateProvince
        )
    }
}
