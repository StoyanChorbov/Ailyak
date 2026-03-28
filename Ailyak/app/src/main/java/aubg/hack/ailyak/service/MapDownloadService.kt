package aubg.hack.ailyak.service

import android.util.Log
import aubg.hack.ailyak.ui.components.OfflineTileStoreProvider
import com.mapbox.common.TileRegionLoadOptions
import com.mapbox.common.TileStore
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.maps.OfflineManager
import com.mapbox.maps.Style
import com.mapbox.maps.TilesetDescriptorOptions
import java.util.UUID

object MapDownloadService {
    fun createBoundingPolygon(center: Point, distanceKm: Double): Polygon {
        val lat = center.latitude()
        val lon = center.longitude()

        val delta = distanceKm / 111.0

        val sw = Point.fromLngLat(lon - delta, lat - delta)
        val se = Point.fromLngLat(lon + delta, lat - delta)
        val ne = Point.fromLngLat(lon + delta, lat + delta)
        val nw = Point.fromLngLat(lon - delta, lat + delta)

        return Polygon.fromLngLats(
            listOf(
                listOf(sw, se, ne, nw, sw)
            )
        )
    }

    fun downloadOfflineRegion(
        polygon: Polygon
    ) {
        val tileStore = OfflineTileStoreProvider.tileStore

        val descriptor = OfflineManager()
            .createTilesetDescriptor(
                TilesetDescriptorOptions.Builder()
                    .styleURI(Style.MAPBOX_STREETS)
                    .minZoom(10)
                    .maxZoom(19)
                    .build()
            )

        val options = TileRegionLoadOptions.Builder()
            .geometry(polygon)
            .descriptors(listOf(descriptor))
            .acceptExpired(true)
            .build()

        tileStore.loadTileRegion(
            "region-${UUID.randomUUID()}",
            options,
            { progress ->
                Log.d("Offline", "Progress: ${progress.completedResourceCount}")
            },
            { result ->
                if (result.isValue) {
                    Log.d("Offline", "Download complete")
                } else {
                    Log.e("Offline", "Error: ${result.error}")
                }
            }
        )
    }
}