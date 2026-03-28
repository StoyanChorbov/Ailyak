package aubg.hack.ailyak.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aubg.hack.ailyak.service.LocationService
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private var mapboxMap: MapboxMap? = null

    fun onMapReady(map: MapboxMap) {
        mapboxMap = map
    }

    fun drawLine(points: List<Point>) {
        viewModelScope.launch(Dispatchers.Main) {  // ← force main thread
            mapboxMap?.getStyle { style ->
                if (style.styleSourceExists("route-source")) {
                    style.removeStyleLayer("route-layer")
                    style.removeStyleSource("route-source")
                }
                style.addSource(geoJsonSource("route-source") {
                    geometry(LineString.fromLngLats(points))
                })
                style.addLayer(lineLayer("route-layer", "route-source") {
                    lineColor("#FF0000")
                    lineWidth(4.0)
                    lineCap(LineCap.ROUND)
                    lineJoin(LineJoin.ROUND)
                })
            }
        }
    }

    fun clearLine() {
        viewModelScope.launch(Dispatchers.Main) {  // ← force main thread
            mapboxMap?.getStyle { style ->
                if (style.styleSourceExists("route-layer"))
                    style.removeStyleLayer("route-layer")
                if (style.styleSourceExists("route-source"))
                    style.removeStyleSource("route-source")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mapboxMap = null
    }
}
