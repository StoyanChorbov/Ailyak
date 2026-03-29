package aubg.hack.ailyak.service

import aubg.hack.ailyak.data.model.GbifResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AnimalApiService {

    @GET("occurrence/search")
    suspend fun getNearbyAnimals(
        @Query("decimalLatitude") lat: Double,
        @Query("decimalLongitude") lng: Double,
        @Query("radius") radiusKm: Int = 10,
        @Query("kingdom") kingdom: String = "Animalia",
        @Query("mediaType") mediaType: String = "StillImage",
        @Query("limit") limit: Int = 50,
        @Query("hasCoordinate") hasCoordinate: Boolean = true,
        @Query("occurrenceStatus") status: String = "PRESENT"
    ): GbifResponse
}