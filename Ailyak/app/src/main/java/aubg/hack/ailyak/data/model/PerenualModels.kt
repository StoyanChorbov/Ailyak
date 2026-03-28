package com.plantservice.model

data class PerenualResponse(
    val data: List<PlantDetails>
)

data class PlantDetails(
    val id: Int,
    val common_name: String?,
    val scientific_name: List<String>,
    val edible: Boolean?,
    val edible_fruit: Boolean?,
    val poisonous: Boolean?
)
