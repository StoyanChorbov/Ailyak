package aubg.hack.ailyak.service
import retrofit2.http.GET
import retrofit2.http.Query

import aubg.hack.ailyak.data.model.*

interface CoverageCellService {
    @GET("cell/getInArea")
    suspend fun getCellsInArea(
        @Query("key") apiKey: String,
        @Query("BBOX") bbox: String,     // "minLat,minLon,maxLat,maxLon"
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 100
    ): OpenCellIdResponse
}