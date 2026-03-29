package aubg.hack.ailyak.location
import android.R
import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {
    @Inject lateinit var locationRepository: LocationRepository
    private lateinit var fusedClient: FusedLocationProviderClient
    private val channelId = "wildguard"

    override fun onCreate() {
        super.onCreate()
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        val channel = NotificationChannel(channelId, "Location Tracking", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("WildGuard is tracking your path")
            .setSmallIcon(R.drawable.ic_menu_mylocation).build()
        startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3 * 60 * 1000L).build()
        try { fusedClient.requestLocationUpdates(req, locationRepository.locationCallback, mainLooper) }
        catch (e: SecurityException) { stopSelf() }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
