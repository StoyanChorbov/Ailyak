package aubg.hack.ailyak.data.model

data class PlantDetails(
    val id: Int,
    val commonName: String?,
    val scientificName: List<String>,
    val edible: Boolean?,
    val edibleFruit: Boolean?,
    val poisonous: Boolean?
)


data class PlantSafetyInfo(
    val species: String,
    val commonName: String?,
    val lat: Double?,
    val lon: Double?,
    val isEdible: Boolean?,
    val isPoisonous: Boolean?,
    val region: String?
) {
    val isSafeToEat: Boolean
        get() = isEdible == true && isPoisonous != true
}

