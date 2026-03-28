package aubg.hack.ailyak.viewmodel

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aubg.hack.ailyak.location.LocationTracker
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private var mapboxMap: MapboxMap? = null
    private var locationJob: Job? = null

    // Keeps the full location history
    private val locationHistory = mutableListOf<Point>()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation

    fun onMapReady(map: MapboxMap) {
        mapboxMap = map
    }

    // ─────────────────────────────────────────
    // LOCATION TRACKING
    // ─────────────────────────────────────────

    fun startLocationTracking(context: Context, intervalMs: Long = 3000L) {
        locationJob?.cancel()
        val tracker = LocationTracker(context)

        locationJob = viewModelScope.launch {
            tracker.locationUpdates(intervalMs).collect { location ->
                _currentLocation.value = location

                val newPoint = Point.fromLngLat(location.longitude, location.latitude)
                locationHistory.add(newPoint)

                // Draw line between last two positions
                if (locationHistory.size >= 2) {
                    val last = locationHistory.takeLast(2)
                    drawLocationLine(last)
                }
            }
        }
    }

    fun stopLocationTracking() {
        locationJob?.cancel()
        locationJob = null
    }

    // ─────────────────────────────────────────
    // LINE BETWEEN LAST TWO LOCATIONS
    // ─────────────────────────────────────────
    private fun drawLocationLine(points: List<Point>) {
        viewModelScope.launch(Dispatchers.Main) {
            mapboxMap?.getStyle { style ->
                val sourceId = "location-line-source"
                val layerId = "location-line-layer"

                val existingSource = style.getSource(sourceId)

                if (existingSource == null) {
                    style.addSource(geoJsonSource(sourceId) {
                        geometry(LineString.fromLngLats(points))
                    })

                    style.addLayer(lineLayer(layerId, sourceId) {
                        lineColor("#2196F3")
                        lineWidth(4.0)
                        lineCap(LineCap.ROUND)
                        lineJoin(LineJoin.ROUND)
                    })
                } else {
                    val geoJsonSource = style.getSourceAs<GeoJsonSource>(sourceId)
                    geoJsonSource?.geometry(LineString.fromLngLats(points))
                }
            }
        }
    }
//    private fun drawLocationLine(points: List<Point>) {
//        viewModelScope.launch(Dispatchers.Main) {
//            mapboxMap?.loadStyle(Style.MAPBOX_STREETS) { style ->
////                clearLayer("location-line-layer", "location-line-source", style)
////
////                style.addSource(geoJsonSource("location-line-source") {
////                    geometry(LineString.fromLngLats(points))
////                })
////                style.addLayer(lineLayer("location-line-layer", "location-line-source") {
////                    lineColor("#2196F3")     // blue
////                    lineWidth(4.0)
////                    lineCap(LineCap.ROUND)
////                    lineJoin(LineJoin.ROUND)
////                })
//                val sourceId = "location-line-source"
//                val layerId = "location-line-layer"
//
//                val existingSource = style.getSource(sourceId)
//
//                if (existingSource == null) {
//                    style.addSource(geoJsonSource(sourceId) {
//                        geometry(LineString.fromLngLats(points))
//                    })
//
//                    style.addLayer(lineLayer(layerId, sourceId) {
//                        lineColor("#2196F3")
//                        lineWidth(4.0)
//                        lineCap(LineCap.ROUND)
//                        lineJoin(LineJoin.ROUND)
//                    })
//                } else {
//                    val geoJsonSource = style.getSourceAs<GeoJsonSource>(sourceId)
//                    geoJsonSource?.geometry(LineString.fromLngLats(points))
//                }
//            }
//        }
//    }

    // Draw the FULL traveled path instead of just last two points
    fun drawFullPath() {
        if (locationHistory.size < 2) return
        viewModelScope.launch(Dispatchers.Main) {
            mapboxMap?.getStyle { style ->
                clearLayer("location-line-layer", "location-line-source", style)
                style.addSource(geoJsonSource("location-line-source") {
                    geometry(LineString.fromLngLats(locationHistory))
                })
                style.addLayer(lineLayer("location-line-layer", "location-line-source") {
                    lineColor("#2196F3")
                    lineWidth(4.0)
                    lineCap(LineCap.ROUND)
                    lineJoin(LineJoin.ROUND)
                })
            }
        }
    }

    fun clearLocationPath() = removeOverlay("location-line-layer", "location-line-source")
    fun clearLocationHistory() { locationHistory.clear() }

    // ─────────────────────────────────────────
    // ... rest of your existing drawLine,
    //     drawHeatmap, clearAll etc.
    // ─────────────────────────────────────────

    private fun removeOverlay(layerId: String, sourceId: String?) {
        viewModelScope.launch(Dispatchers.Main) {
            mapboxMap?.getStyle { style -> clearLayer(layerId, sourceId, style) }
        }
    }

    private fun clearLayer(layerId: String, sourceId: String?, style: com.mapbox.maps.Style) {
        if (style.styleLayerExists(layerId)) style.removeStyleLayer(layerId)
        if (sourceId != null && style.styleSourceExists(sourceId)) style.removeStyleSource(sourceId)
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationTracking()
        mapboxMap = null
    }
}
