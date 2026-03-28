package aubg.hack.ailyak.data.model

data class CoverageCell (
   val latmin: Double,
    val lonmin:Double,
    val latmax:Double,
    val lonmax:Double,
   //Mobile country code
    val mobileCountryCode: Int,
   //Mobile network code, ex A1's code
    val mobileNetworkCode:Int


)