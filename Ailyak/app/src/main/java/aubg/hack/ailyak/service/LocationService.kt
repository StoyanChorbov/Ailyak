package aubg.hack.ailyak.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

    class LocationService(private val context: Context) {

        private val client = LocationServices.getFusedLocationProviderClient(context)

        public suspend fun getCurrentLatAndLong(): DoubleArray? {

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return null
            }

            val location = client.lastLocation.await()

            return location?.let {
                doubleArrayOf(it.latitude, it.longitude)
            }
        }
    }