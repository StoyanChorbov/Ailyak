package aubg.hack.ailyak.service
import aubg.hack.ailyak.data.model.OverpassResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface WaterSourceService {
    @FormUrlEncoded
    @POST("interpreter")
    suspend fun queryWater(@Field("data") query: String): OverpassResponse
}