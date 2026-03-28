package aubg.hack.ailyak.viewmodel

data class CoverageCellViewModel(
    val id:Int,
    val longitude: Double,
    val latitude: Double,
    //Mobile country code
    val mobileCountryCode: Int,
    //Mobile network code, ex A1's code
    val mobileNetworkCode:Int,
    val localAreaCode:Int,
    val averageSignalStrength:Double,
    val range:Double,
    val isChangeable: Boolean,
    val radioType:String
    )

data class CoverageCellsViewModelList(
    val cells: List<CoverageCellViewModel>,
    val cellsCount:Int
)