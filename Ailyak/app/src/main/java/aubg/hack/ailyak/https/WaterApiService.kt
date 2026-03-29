package aubg.hack.ailyak.https

import aubg.hack.ailyak.data.model.OverpassResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface WaterApiService {
    // Overpass uses POST with form-encoded body for complex queries
    @FormUrlEncoded
    @POST("interpreter")
    suspend fun queryWater(@Field("data") query: String): OverpassResponse
}