package aubg.hack.ailyak.service

import aubg.hack.ailyak.https.KtorClient
import aubg.hack.ailyak.data.model.PlantDetails
import org.json.JSONObject
import aubg.hack.ailyak.PerenualConstants

class PerenualService(private val apiKey: String) {

    suspend fun getPlantSafety(scientificName: String): Result<PlantDetails?> {
        return KtorClient.get(
            PerenualConstants.apiUrl+"species-list",
            params = mapOf(
                "key" to apiKey,
                "q"   to scientificName
            )
        ).mapCatching { json -> parseFirstPlant(json) }
    }

    private fun parseFirstPlant(json: String): PlantDetails? {
        val root = JSONObject(json)
        val data = root.optJSONArray("data") ?: return null
        if (data.length() == 0) return null

        val plant = data.getJSONObject(0)

        val scientificNames = plant.optJSONArray("scientific_name")
            ?.let { arr -> List(arr.length()) { i -> arr.getString(i) } }
            ?: emptyList()

        return PlantDetails(
            id = plant.optInt("id"),
            commonName = plant.optString("common_name").takeIf { it.isNotEmpty() },
            scientificName = scientificNames,
            edible= if (plant.has("edible")) plant.getBoolean("edible") else null,
            edibleFruit = if (plant.has("edible_fruit")) plant.getBoolean("edible_fruit") else null,
            poisonous= if (plant.has("poisonous")) plant.getBoolean("poisonous") else null
        )
    }
}