package aubg.hack.ailyak.ui.components

import aubg.hack.ailyak.viewmodel.MapViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location

import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource


@Composable
fun SurvivalMap(
    modifier: Modifier,
    viewModel: MapViewModel = viewModel()
) {
    val mapViewportState = rememberMapViewportState()

    MapboxMap(
        modifier = modifier,
        mapViewportState = mapViewportState
    ) {
        MapEffect(Unit) { mapView ->
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = true
            }
            mapViewportState.transitionToFollowPuckState()

            mapView.mapboxMap.loadStyle(mapView.mapboxMap.style, {
                        viewModel.onMapReady(mapView.getMapboxMap())
                    })
        }
    }
}

private fun MapboxMap.loadStyle(
    styleExtension: Style?,
    styleTransitionOptions: () -> Unit
) {
}
