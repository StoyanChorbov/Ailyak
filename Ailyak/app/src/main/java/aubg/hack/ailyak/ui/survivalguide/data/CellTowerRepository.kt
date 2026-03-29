package aubg.hack.ailyak.ui.survivalguide.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.*
import androidx.annotation.RequiresPermission
import aubg.hack.ailyak.data.model.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlin.math.*
import javax.inject.Inject

import android.telephony.CellIdentityNr
import android.telephony.CellInfoNr
import android.telephony.CellSignalStrengthNr
import aubg.hack.ailyak.data.model.CellRadioType
import aubg.hack.ailyak.data.model.CellTowerItem
import aubg.hack.ailyak.data.model.CellTowerUi
import aubg.hack.ailyak.data.model.DeviceCellInfo
import aubg.hack.ailyak.https.CellTowerApiService

class CellTowerRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: CellTowerApiService
) {
    @SuppressLint("MissingPermission")
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    suspend fun getNearbyCellTowers(
        lat: Double,
        lng: Double,
        radiusDeg: Double = 0.05   // ~5.5km
    ): Result<List<CellTowerUi>> {
        return try {
            val bbox = "${lat - radiusDeg},${lng - radiusDeg},${lat + radiusDeg},${lng + radiusDeg}"
            val response = api.getCellsInArea(
                apiKey = "pk.a38c321cdcb3c8ab5c32ccb66b0c04ea",
                bbox = bbox
            )

            val deviceCells = getDeviceCells()

            val items = response.cells
                .map { it.toUiItem(lat, lng, deviceCells) }
                .sortedBy { it.distanceMeters }

            Result.Success(items)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load cell towers", e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceCells(): List<DeviceCellInfo> {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return try {
            tm.allCellInfo?.mapNotNull { it.toDeviceCellInfo() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun CellInfo.toDeviceCellInfo(): DeviceCellInfo? = when (this) {
        is CellInfoLte -> DeviceCellInfo(
            cellId = cellIdentity.ci.toLong(),
            lac = cellIdentity.tac,
            mcc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                cellIdentity.mccString?.toIntOrNull() ?: 0 else 0,
            mnc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                cellIdentity.mncString?.toIntOrNull() ?: 0 else 0,
            signalStrength = cellSignalStrength.dbm,
            radio = "LTE"
        )
        is CellInfoGsm -> DeviceCellInfo(
            cellId = cellIdentity.cid.toLong(),
            lac = cellIdentity.lac,
            mcc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                cellIdentity.mccString?.toIntOrNull() ?: 0 else 0,
            mnc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                cellIdentity.mncString?.toIntOrNull() ?: 0 else 0,
            signalStrength = cellSignalStrength.dbm,
            radio = "GSM"
        )
        is CellInfoWcdma -> DeviceCellInfo(
            cellId = cellIdentity.cid.toLong(),
            lac = cellIdentity.lac,
            mcc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                cellIdentity.mccString?.toIntOrNull() ?: 0 else 0,
            mnc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                cellIdentity.mncString?.toIntOrNull() ?: 0 else 0,
            signalStrength = cellSignalStrength.dbm,
            radio = "UMTS"
        )
        else -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && this is CellInfoNr) {
                val identity = cellIdentity as CellIdentityNr
                val signal = cellSignalStrength as CellSignalStrengthNr
                DeviceCellInfo(
                    cellId = identity.nci,
                    lac = identity.tac,
                    mcc = identity.mccString?.toIntOrNull() ?: 0,
                    mnc = identity.mncString?.toIntOrNull() ?: 0,
                    signalStrength = signal.dbm,
                    radio = "NR"
                )
            } else null
        }
    }

    private fun CellTowerItem.toUiItem(
        userLat: Double,
        userLng: Double,
        deviceCells: List<DeviceCellInfo>
    ): CellTowerUi {
        val isConnected = deviceCells.any {
            it.cellId == cellid && it.lac == lac
        }
        return CellTowerUi(
            id = cellid,
            lat = lat,
            lng = lon,
            radio = parseRadio(radio),
            signalStrength = averageSignal,
            rangeMeters = range,
            samples = samples,
            distanceMeters = haversineMeters(userLat, userLng, lat, lon),
            isConnected = isConnected
        )
    }

    private fun parseRadio(radio: String?): CellRadioType = when (radio?.uppercase()) {
        "GSM"  -> CellRadioType.GSM
        "UMTS" -> CellRadioType.UMTS
        "LTE"  -> CellRadioType.LTE
        "NR"   -> CellRadioType.NR
        else   -> CellRadioType.UNKNOWN
    }

    private fun haversineMeters(
        lat1: Double, lng1: Double,
        lat2: Double, lng2: Double
    ): Double {
        val r = 6_371_000.0
        val phi1 = Math.toRadians(lat1); val phi2 = Math.toRadians(lat2)
        val dPhi = Math.toRadians(lat2 - lat1)
        val dLam = Math.toRadians(lng2 - lng1)
        val a = sin(dPhi / 2).pow(2) + cos(phi1) * cos(phi2) * sin(dLam / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}