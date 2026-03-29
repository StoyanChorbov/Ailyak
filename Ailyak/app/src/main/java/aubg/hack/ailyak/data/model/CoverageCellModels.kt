package aubg.hack.ailyak.data.model

data class OpenCellIdResponse(
 val count: Int,
 val cells: List<CellTowerItem>
)

data class CellTowerItem(
 val lat: Double,
 val lon: Double,
 val mcc: Int,
 val mnc: Int,
 val lac: Int,
 val cellid: Long,
 val averageSignal: Int?,
 val range: Int?,
 val samples: Int?,
 val radio: String?
)

data class DeviceCellInfo(
 val cellId: Long,
 val lac: Int,
 val mcc: Int,
 val mnc: Int,
 val signalStrength: Int,
 val radio: String
)

data class CellTowerUi(
 val id: Long,
 val lat: Double,
 val lng: Double,
 val radio: CellRadioType,
 val signalStrength: Int?,
 val rangeMeters: Int?,
 val samples: Int?,
 val distanceMeters: Double,
 val isConnected: Boolean = false
)

enum class CellRadioType {
 GSM, UMTS, LTE, NR, UNKNOWN;

 fun label(): String = when (this) {
  GSM     -> "2G GSM"
  UMTS    -> "3G UMTS"
  LTE     -> "4G LTE"
  NR      -> "5G NR"
  UNKNOWN -> "Unknown"
 }

 fun color(): Long = when (this) {
  GSM     -> 0xFF757575
  UMTS    -> 0xFF1565C0
  LTE     -> 0xFF2E7D32
  NR      -> 0xFF6A1B9A
  UNKNOWN -> 0xFF757575
 }
}
