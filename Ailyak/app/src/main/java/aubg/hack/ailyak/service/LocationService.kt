package aubg.hack.ailyak.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices

class LocationService(private val context: Context) {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    fun getLocation(onResult: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Request permissions
            return
        }
        client.lastLocation.addOnSuccessListener {
            onResult(it)
        }
    }
}