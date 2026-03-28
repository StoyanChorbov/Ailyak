package com.plantservice.service

import com.google.gson.Gson
import com.plantservice.http.HttpClient
import com.plantservice.model.PerenualResponse
import com.plantservice.model.PlantDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PerenualService(private val apiKey: String) {

    private val gson = Gson()
    private val baseUrl = "https://perenual.com/api/v2/species-list"

    suspend fun getPlantSafety(scientificName: String): PlantDetails? =
        withContext(Dispatchers.IO) {
            runCatching {
                val json = HttpClient.get(baseUrl, mapOf(
                    "key" to apiKey,
                    "q"   to scientificName
                ))
                gson.fromJson(json, PerenualResponse::class.java).data.firstOrNull()
            }.getOrNull()
        }
}
