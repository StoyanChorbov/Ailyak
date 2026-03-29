package aubg.hack.ailyak.data.model

import com.google.gson.annotations.SerializedName

// OpenCelliD response for nearby cells
data class OpenCellIdResponse(
    val count: Int,
    val cells: List<CellTowerItem>
)

data class CellTowerItem(
    val lat: Double,
    val lon: Double,
    val mcc: Int,           // mobile country code
    val mnc: Int,           // mobile network code
    val lac: Int,           // location area code
    val cellid: Long,
    val averageSignal: Int?,
    val range: Int?,        // estimated range in meters
    val samples: Int?,      // number of measurements
    val radio: String?      // "GSM", "UMTS", "LTE", "NR"
)

// Device's currently connected cell info (from TelephonyManager)
data class DeviceCellInfo(
    val cellId: Long,
    val lac: Int,
    val mcc: Int,
    val mnc: Int,
    val signalStrength: Int,  // dBm
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
    val isConnected: Boolean = false   // true if this is the device's active tower
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
        GSM     -> 0xFF757575  // grey
        UMTS    -> 0xFF1565C0  // blue
        LTE     -> 0xFF2E7D32  // green
        NR      -> 0xFF6A1B9A  // purple
        UNKNOWN -> 0xFF757575
    }
}