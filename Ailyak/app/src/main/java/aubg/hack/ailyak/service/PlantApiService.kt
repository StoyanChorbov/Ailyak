package aubg.hack.ailyak.service

import aubg.hack.ailyak.data.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlantApiService {

    @GET("observations/species_counts")
    suspend fun getNearbySpecies(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radiusKm: Int = 15,
        @Query("iconic_taxa[]") iconicTaxa: String,
        @Query("per_page") perPage: Int = 50,
        @Query("order_by") orderBy: String = "observations_count",
        @Query("photos") photos: Boolean = true,
        @Query("quality_grade") qualityGrade: String = "research"
    ): SpeciesCountResponse

    // Fetches full taxon info including Wikipedia summary
    @GET("taxa/{id}")
    suspend fun getTaxonDetail(
        @Path("id") taxonId: Long
    ): TaxonDetailResponse
}