package aubg.hack.ailyak.service
import android.content.Context
import android.telephony.SmsManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyContactManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun sendEmergencySms(phoneNumber: String, lastLat: Double, lastLng: Double) {
        val msg = "WILDGUARD EMERGENCY: User offline 24h+. Last location: https://maps.google.com/?q=${"$"}{lastLat},${"$"}{lastLng}"
        try {
            context.getSystemService(SmsManager::class.java)
                .sendTextMessage(phoneNumber, null, msg, null, null)
        } catch (e: Exception) { e.printStackTrace() }
    }
}
