package aubg.hack.ailyak.service

import aubg.hack.ailyak.CellConstants
import aubg.hack.ailyak.data.model.CoverageCell
import aubg.hack.ailyak.data.model.CoverageCellParams
import aubg.hack.ailyak.data.model.CoverageCellsList
import aubg.hack.ailyak.https.KtorClient
import org.json.JSONObject

object CoverageCellService {
    suspend fun fetchCellTowersInArea(cell: CoverageCellParams):Result<CoverageCellsList>{

        return KtorClient.get(CellConstants.apiUrl+"cell/getInArea",
            params = mapOf(
                //"apiKey"    to,
                "latmin"   to cell.latituteMin.toString(),
                "lonmin" to cell.longitudeMin.toString(),
                "latmax"  to cell.latituteMax.toString(),
                "lonmax" to cell.longitudeMax.toString(),
                "mcc" to cell.mobileCountryCode.toString(),
                "mnc" to cell.mobileNetworkCode.toString(),
                "lac" to cell.localAreaCode.toString()
            ))
            .mapCatching { json ->
            parseCells(json)
        }
    }

    suspend fun parseCells(json:String):CoverageCellsList{
        val cellsResult = JSONObject(json)
        val cells = cellsResult.getJSONArray("cells")
        return CoverageCellsList(
             cellsCount = cellsResult.getInt("count"),
             cells= List(cells.length()) { i ->
                val cell = cells.getJSONObject(i)
                CoverageCell(
                    id = cell.getInt("cellid"),
                    latitude = cell.getDouble("lat"),
                    longitude = cell.getDouble("lon"),
                    range= cell.getDouble("range"),
                    averageSignalStrength = cell.getDouble("averageSignalStrength"),
                    radioType =cell.getString("radio"),
                    isChangeable = cell.getBoolean("changeable"),
                    mobileCountryCode= cell.getInt("mcc"),
                    mobileNetworkCode = cell.getInt("mnc"),
                    localAreaCode = cell.getInt("lac")
                )
            }
        )
    }


}