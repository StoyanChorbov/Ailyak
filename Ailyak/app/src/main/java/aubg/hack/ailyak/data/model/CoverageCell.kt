package aubg.hack.ailyak.data.model

import aubg.hack.ailyak.CellConstants

data class CoverageCell (
 val latituteMin: Double,
 val longitudeMin:Double,
 val latituteMax:Double,
 val longitudeMax:Double,
 val mobileCountryCode: Int?,
 val mobileNetworkCode:Int?,
 val localAreaCode:Int?
)
