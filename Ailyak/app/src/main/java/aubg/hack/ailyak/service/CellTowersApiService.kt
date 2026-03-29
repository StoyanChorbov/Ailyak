package aubg.hack.ailyak.service

import aubg.hack.ailyak.data.model.OpenCellIdResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CellTowerApiService {
    // OpenCelliD get cells in bounding box
    @GET("cell/getInArea")
    suspend fun getCellsInArea(
        @Query("key") apiKey: String,
        @Query("BBOX") bbox: String,     // "minLat,minLon,maxLat,maxLon"
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 100
    ): OpenCellIdResponse
}