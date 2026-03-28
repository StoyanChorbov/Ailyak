package aubg.hack.ailyak.service

import android.Manifest
import android.R.drawable.ic_menu_mylocation
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

    class LocationService() : Service() {

        private lateinit var fusedClient: FusedLocationProviderClient
        private lateinit var callback: LocationCallback

        private fun startLocationUpdates() {

            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                5000L
            ).build()


            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedClient.requestLocationUpdates(
                request,
                callback,
                Looper.getMainLooper()
            )
        }

        private fun startForegroundServiceNotification() {
            val channelId = "location_channel"

            val channel = NotificationChannel(
                channelId,
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Tracking location")
                .setContentText("Location is being tracked in background")
                .setSmallIcon(ic_menu_mylocation)
                .build()

            startForeground(1, notification)
        }

        override fun onCreate() {
            super.onCreate()

            fusedClient = LocationServices.getFusedLocationProviderClient(this)

            callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation ?: return

                    val lat = location.latitude
                    val lon = location.longitude
                }
            }

            startForegroundServiceNotification()
            startLocationUpdates()
        }


        override fun onBind(intent: Intent?): IBinder? = null
    }