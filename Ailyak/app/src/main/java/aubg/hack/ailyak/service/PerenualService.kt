package aubg.hack.ailyak.service

import com.google.gson.Gson
import aubg.hack.ailyak.https.KtorClient
import aubg.hack.ailyak.data.model.PlantDetails
import aubg.hack.ailyak.data.model.PerenualResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PerenualService(private val apiKey: String) {

    private val gson = Gson()
    private val baseUrl = "https://perenual.com/api/v2/species-list"

    suspend fun getPlantSafety(scientificName: String): PlantDetails? =
        withContext(Dispatchers.IO) {
            runCatching {
                val json = KtorClient.get(baseUrl, mapOf(
                    "key" to apiKey,
                    "q"   to scientificName
                ))
                gson.fromJson(json.toString(), PerenualResponse::class.java).data.firstOrNull()
            }.getOrNull()
        }
}
