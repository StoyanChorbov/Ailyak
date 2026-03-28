package aubg.hack.ailyak.ui

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import aubg.hack.ailyak.service.MapDownloadService
import aubg.hack.ailyak.ui.components.SurvivalMap
import com.mapbox.geojson.Point

@Composable
fun OfflineMapDownloadScreen(modifier: Modifier = Modifier) {
    var showDownloadButton by remember { mutableStateOf(false) }

    var center by remember { mutableStateOf<Point?>(null) }

    fun downloadMap() {

        val polygon = MapDownloadService.createBoundingPolygon(center!!, 5.0)
        MapDownloadService.downloadOfflineRegion(polygon)
    }


    Box(modifier = modifier.fillMaxSize()) {
//        SurvivalMap(Point = center, onPointSelected = {
//            center = it
//            showDownloadButton = true
//        })

        if (showDownloadButton) {
            FloatingActionButton(onClick = { downloadMap() }, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 72.dp) ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Download Map")
                }
            }
        }
    }
}