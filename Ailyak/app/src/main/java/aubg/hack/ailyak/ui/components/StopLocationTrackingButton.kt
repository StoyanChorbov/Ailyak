package aubg.hack.ailyak.ui.components

import android.content.Intent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import aubg.hack.ailyak.service.LocationService

@Composable
fun StopLocationTrackingButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val intent = Intent(context, LocationService::class.java)

    Button(modifier = modifier, onClick = {
        context.stopService(intent)
    }) {
        Text("Stop Tracking")
    }
}