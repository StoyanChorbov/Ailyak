package aubg.hack.ailyak.data.model

data class CoverageCellParams (
 val latituteMin: Double,
 val longitudeMin:Double,
 val latituteMax:Double,
 val longitudeMax:Double,
 val mobileCountryCode: Int?,
 val mobileNetworkCode:Int?,
 val localAreaCode:Int?
)

data class CoverageCell(
 val id:Int,
 val longitude: Double, // Coordinate
 val latitude: Double, // Coordinate
 //Mobile country code
 val mobileCountryCode: Int,
 //Mobile network code, ex A1's code
 val mobileNetworkCode:Int,
 val localAreaCode:Int,
 val averageSignalStrength:Double,
 val range:Double, // In meters
 val isChangeable: Boolean,
 val radioType:String
)

data class CoverageCellsList(
 val cells: List<CoverageCell>,
 val cellsCount:Int
)
