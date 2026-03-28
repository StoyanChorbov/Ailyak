package aubg.hack.ailyak.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import aubg.hack.ailyak.viewmodel.MapViewModel
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location


@Composable
fun SurvivalMap(
    modifier: Modifier = Modifier,
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
                        viewModel.onMapReady(mapView.mapboxMap)
                    })
        }
    }
}

private fun MapboxMap.loadStyle(
    styleExtension: Style?,
    styleTransitionOptions: () -> Unit
) {
}