package aubg.hack.ailyak.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import aubg.hack.ailyak.data.model.CoverageCellParams
import aubg.hack.ailyak.service.CoverageCellService
import aubg.hack.ailyak.service.MapDownloadService
import aubg.hack.ailyak.service.WaterSourceService
import aubg.hack.ailyak.ui.components.ClickableMap
import com.mapbox.geojson.Point
import kotlinx.coroutines.launch

@Composable
fun OfflineMapDownloadScreen(modifier: Modifier = Modifier) {
    var showDownloadButton by remember { mutableStateOf(false) }

    var center by remember { mutableStateOf<Point>(Point.fromLngLat(0.0, 0.0)) }

    val scope = rememberCoroutineScope()

    fun downloadMapData() {
        val radius = 5.0 // in km
        val polygon = MapDownloadService.createBoundingPolygon(center, radius)

        val lat = center.latitude()
        val lon = center.longitude()

        scope.launch {
            val cellTowers = CoverageCellService.fetchCellTowersInArea(CoverageCellParams(
                latituteMin = lat - radius,
                latituteMax = lat + radius,
                longitudeMin = lon - radius,
                longitudeMax = lon + radius,
                localAreaCode = null,
                mobileNetworkCode = null,
                mobileCountryCode = null,
            ))
            val plants = PlantSafetyService.getPlantSafetyInfoNearby(lat, lon, radius)
            val waterSources = WaterSourceService.getDrinkingWaterNearby(lat, lon,
                (radius * 1000).toInt()
            )
        }

        MapDownloadService.downloadOfflineRegion(polygon)
    }


    Box(modifier = modifier.fillMaxSize()) {
        ClickableMap(point = center, onPointSelected = {
            center = it
            showDownloadButton = true
        })

        if (showDownloadButton) {
            FloatingActionButton(onClick = { downloadMapData() }, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 72.dp) ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Download Map")
                }
            }
        }
    }
}