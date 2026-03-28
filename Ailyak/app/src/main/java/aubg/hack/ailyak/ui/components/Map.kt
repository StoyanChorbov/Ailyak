package aubg.hack.ailyak.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.R.drawable.default_marker_outer
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import androidx.core.graphics.createBitmap
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager

@Composable
fun SurvivalMap(
    onPointSelected: (Point) -> Unit,
    point: Point?,
    modifier: Modifier = Modifier
) {
    val mapViewportState = rememberMapViewportState()

    val annotationManagerState = remember { mutableStateOf<PointAnnotationManager?>(null) }

    MapboxMap(
        modifier = modifier,
        mapViewportState = mapViewportState,
        onMapClickListener = {
            onPointSelected(it)
            true
        }
    ) {
        // Initialize manager once
        MapEffect(Unit) { mapView ->
            annotationManagerState.value =
                mapView.annotations.createPointAnnotationManager()
        }

        // Update marker when point changes
        MapEffect(point) { mapView ->
            val manager = annotationManagerState.value ?: return@MapEffect

            manager.deleteAll()

            point?.let {
                manager.create(
                    PointAnnotationOptions()
                        .withPoint(it)
                        .withIconImage(bitmapFromDrawable(mapView, default_marker_outer))
                )
            }
        }
    }
}
fun bitmapFromDrawable(mapView: MapView, drawableRes: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(mapView.context, drawableRes)
        ?: throw IllegalArgumentException("Drawable not found")

    val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)

    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}